package wtf.monsoon.newcommon.vantage.api.utils.os;

public enum OperatingSystem {
    WINDOWS("Windows"),
    MACOSX("MacOS"),
    LINUX("Linux");

    private final String os;

    OperatingSystem(String os) {
        this.os = os;
    }
    public String getNiceName() {
        return this.os;
    }
}