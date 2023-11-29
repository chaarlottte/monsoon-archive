package wtf.monsoon.newcommon.vantage.api.models;

public class UserModel {
    private String username;
    private String hwid;

    UserModel(String username, String hwid) {
        this.username = username;
        this.hwid = hwid;
    }
}
