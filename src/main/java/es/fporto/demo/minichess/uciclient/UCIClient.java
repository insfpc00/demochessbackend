package es.fporto.demo.minichess.uciclient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.fporto.demo.minichess.uciclient.exception.UCIClientException;

public abstract class UCIClient {

	public class Move {
		private String move;
		private String evaluation;

		public String getMove() {
			return move;
		}

		public void setMove(String move) {
			this.move = move;
		}

		public String getEvaluation() {
			return evaluation;
		}

		public void setEvaluation(String evaluation) {
			this.evaluation = evaluation;
		}

		public Move(String move, String evaluation) {
			super();
			this.move = move;
			this.evaluation = evaluation;
		}

		public Move() {
			super();
		}

	}

	private BufferedReader input;
	private BufferedWriter output;
	private Process process;
	private static final String MOVE_REGEX = "[a-h][1-8][a-h][1-8][qrbk]?";
	// private static final String INFO_REGEX = "^info .*\\bdepth (\\d+) .*\\bnps
	// (\\d+)";
	private static final String SCORE_REGEX = "^info .*\\bscore (\\w+) (-?\\d+) .*";
	private static final Pattern scorePattern = Pattern.compile(SCORE_REGEX);

	private Long depth;
	private Long moveTime;

	private UCIConfigOption[] configOptions;

	public UCIClient(Optional<Long> depth, Optional<Long> moveTime, UCIConfigOption... options) {
		this.configOptions = options;
		this.depth = depth.orElse(-1l);
		this.moveTime = moveTime.orElse(-1l);
	}

	public Long getDepth() {
		return depth;
	}

	public void setDepth(Long depth) {
		this.depth = depth;
	}

	public Long getMoveTime() {
		return moveTime;
	}

	public void setMoveTime(Long moveTime) {
		this.moveTime = moveTime;
	}

	public UCIConfigOption[] getConfigOptions() {
		return configOptions;
	}

	public void setConfigOptions(UCIConfigOption[] configOptions) {
		this.configOptions = configOptions;
	}

	public abstract String getEnginePath();

	public void connect() throws UCIClientException {

		try {
			process = Runtime.getRuntime().exec(getEnginePath());
			input = new BufferedReader(new InputStreamReader(process.getInputStream()));
			output = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			if (configOptions != null) {
				for (UCIConfigOption option : configOptions) {
					sendCommand(option.getCommand());
				}
			}

		} catch (Exception e) {
			throw new UCIClientException("Couldn't connect to engine", e);
		}
	}

	private void waitForReady() throws UCIClientException {
		sendCommand(UCICommand.IS_READY);
		readResponse(UCICommand.READY_OK.toString());
	}

	public void newGame() throws UCIClientException {
		sendCommand(UCICommand.UCI);
		sendCommand(UCICommand.UCI_NEW_GAME);
		waitForReady();
	}

	private void sendCommand(UCICommand uciCommand) throws UCIClientException {
		this.sendCommand(uciCommand.toString());
	}

	private void sendCommand(String command) throws UCIClientException {
		try {
			output.write(command + "\n");
			output.flush();
		} catch (IOException e) {
			throw new UCIClientException(e);
		}
	}

	private List<String> readResponse(String expected) throws UCIClientException {
		try {
			List<String> lines = new ArrayList<>();
			String line;

			while ((line = input.readLine()) != null) {
				lines.add(line);
				if (line.startsWith(expected))
					break;
			}
			return lines;
		} catch (IOException e) {
			throw new UCIClientException("Error reading response", e);
		}
	}

	public Move makeAMove(String fenPosition) throws UCIClientException {
		String command = "position fen " + fenPosition;
		sendCommand(command);

		String goCommand = "go";
		goCommand += (this.depth >= 0 ? " depth " + this.depth : "");
		goCommand += (this.moveTime >= 0 ? " movetime " + this.moveTime : "");

		sendCommand(goCommand);
		List<String> response = readResponse(UCICommand.BESTMOVE.toString());
		String bestMove = response.get(response.size() - 1);
		String moveStr = bestMove.split(" ")[1];
		Move move = new Move();
		move.setMove(moveStr);
		if (moveStr.matches(MOVE_REGEX)) {
			String score = "";
			if (response.size() > 1) {
				String infoString = response.get(response.size() - 2);
				Matcher matcher = scorePattern.matcher(infoString);
				if (matcher.matches()) {
					score = matcher.group(1) + " " + matcher.group(2);
					move.setEvaluation(score);
				}
			}
			return move;

		} else {
			return null;
		}
	}

	public void disconnect() throws UCIClientException {
		if (process != null) {
			sendCommand(UCICommand.QUIT);
			process.destroy();
			try {
				input.close();
				output.close();
			} catch (IOException e) {
				throw new UCIClientException(e);
			}
		}
	}

	protected void finalize() throws Throwable {
		disconnect();
	}
}
