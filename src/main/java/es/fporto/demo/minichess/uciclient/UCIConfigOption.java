package es.fporto.demo.minichess.uciclient;

public enum UCIConfigOption {
	
	Contempt("Contempt"),
    MultiPV("MultiPV"),
    Skill_Level("Skill Level"),
    Move_Overhead("Move Overhead"),
    Minimum_Thinking_Time("Minimum Thinking Time"),
    Slow_Mover("Slow Mover"),
    Nodestime("nodestime"),
    Analysis_Contempt("Analysis Contempt"),
    Threads("Threads"),
    Hash("Hash"),
    Clear_Hash("Clear Hash"),
    Ponder("Ponder");
	
    private String option;
    private long value;

    UCIConfigOption(String option) {
        this.option = option;
    }
    
    UCIConfigOption(String option,long value) {
        this.option = option;
        this.value=value;
    }

    public UCIConfigOption setValue(long value) {
        this.value = value;
        return this;
    }

    public String getCommand() {
        return "setoption name " + option + " value " + value;
    }
}
