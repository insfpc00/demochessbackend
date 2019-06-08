package es.fporto.demo.minichess.analysis.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.github.bhlangonijr.chesslib.move.MoveList;

import es.fporto.demo.minichess.common.Utils;
import es.fporto.demo.minichess.model.Match;
import es.fporto.demo.minichess.model.Move;
import es.fporto.demo.minichess.model.MoveAnalysis;
import es.fporto.demo.minichess.repository.MatchRepository;
import es.fporto.demo.minichess.repository.MoveRepository;
import es.fporto.demo.minichess.uciclient.UCIClient;
import es.fporto.demo.minichess.uciclient.exception.UCIClientException;

@Service
public class AnalysisService {

	@Autowired
	private MatchRepository matchRepository;

	@Autowired
	private MoveRepository moveRepository;

	@Autowired
	private BlockingQueue<UCIClient> analysisClients;

	private static final Pattern matePattern = Pattern.compile("^mate (-?\\d+)");
	private static final Pattern centiPawnsPattern = Pattern.compile("^cp (-?\\d+)");

	public List<MoveAnalysis> analyseMatch(final long matchId) throws AnalysisException {

		Optional<Match> matchOpt = matchRepository.findById(matchId);
		if (matchOpt.isEmpty()) {
			throw new AnalysisException("Match not found");
		}

		Match match = matchOpt.get();

		List<MoveAnalysis> movesAnalysis = match.getMoves().parallelStream().map(m -> {
			try {
				int position = match.getMoves().indexOf(m);
				String fen = Utils.STARTING_FEN;
				if (position > 0) {
					fen = match.getMoves().get(position - 1).getFenPosition();
				}
				return analyseMove(m, fen);
			} catch (AnalysisException e) {
				e.printStackTrace();
				return null;
			}
		}).sorted((ma1, ma2) -> ma1.getMove().getNumber() - ma2.getMove().getNumber()).collect(Collectors.toList());

		movesAnalysis.forEach(ma -> {
			ma.getMove().setAnalysis(ma);
			moveRepository.save(ma.getMove());
		});

		return movesAnalysis;// movesAnalysis.stream().map( ma -> new MoveAnalysisDto(ma)
								// ).collect(Collectors.toList());
	}

	public MoveAnalysis analyseMove(Move m, String fen) throws AnalysisException {

		try {
			UCIClient client = analysisClients.take();
			UCIClient.Move bestMove = client.makeAMove(fen);
			String bestMoveAcn = convertMoveToACN(fen, bestMove.getMove(), !m.getColor());

			Integer mateInX = null;
			Integer score = null;

			if (bestMove.getEvaluation() != null) {
				Matcher mateMatcher = matePattern.matcher(bestMove.getEvaluation());
				if (mateMatcher.matches()) {
					mateInX = Integer.valueOf(mateMatcher.group(1))*(m.getColor() ? 1 : -1);
				} else {
					Matcher cpMatcher = centiPawnsPattern.matcher(bestMove.getEvaluation());
					if (cpMatcher.matches()) {
						score = Integer.valueOf(cpMatcher.group(1)) * (m.getColor() ? 1 : -1);
					}
				}
			}

			MoveAnalysis analysis = new MoveAnalysis(m, bestMoveAcn, score, mateInX);

			analysisClients.add(client);

			return analysis;

		} catch (UCIClientException | MoveConversionException | InterruptedException e) {
			throw new AnalysisException("Exception analysing move", e);
		}

	}

	private String convertMoveToACN(String fen, String move, boolean whiteMove) throws MoveConversionException {
		MoveList moveList = new MoveList(fen);

		Square from = Square.fromValue(move.substring(0, 2).toUpperCase());
		Square to = Square.fromValue(move.substring(2, 4).toUpperCase());
		com.github.bhlangonijr.chesslib.move.Move convertedMove;
		if (move.length() > 4) {
			String promotion = move.substring(4);
			Piece promotedTo = null;
			switch (promotion) {
			case "q":
				promotedTo = whiteMove ? Piece.WHITE_QUEEN : Piece.BLACK_QUEEN;
				break;
			case "b":
				promotedTo = whiteMove ? Piece.WHITE_BISHOP : Piece.BLACK_BISHOP;
				break;
			case "k":
				promotedTo = whiteMove ? Piece.WHITE_KNIGHT : Piece.BLACK_KNIGHT;
				break;
			case "r":
				promotedTo = whiteMove ? Piece.WHITE_ROOK : Piece.BLACK_ROOK;
				break;
			}

			convertedMove = new com.github.bhlangonijr.chesslib.move.Move(from, to, promotedTo);
		} else {
			convertedMove = new com.github.bhlangonijr.chesslib.move.Move(from, to);
		}

		moveList.add(convertedMove);
		String[] sanMoves;
		sanMoves = moveList.toSanArray();

		return sanMoves[sanMoves.length - 1];

	}
}
