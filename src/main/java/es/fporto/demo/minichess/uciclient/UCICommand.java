package es.fporto.demo.minichess.uciclient;

public enum UCICommand {

	IS_READY("isready"), READY_OK("readyok"), UCI("uci"), UCI_NEW_GAME("ucinewgame"), BESTMOVE("bestmove"),
	QUIT("quit");

	private String command;

	UCICommand(String command) {
		this.command = command;
	}

	@Override
	public String toString() {
		return command;
	}
}
