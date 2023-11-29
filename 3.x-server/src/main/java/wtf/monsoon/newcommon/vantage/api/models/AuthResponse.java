package wtf.monsoon.newcommon.vantage.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AuthResponse implements Serializable {
    @Expose
    @SerializedName("error")
    public boolean isError;

    @Expose
    @SerializedName("msg")
    public String message;

    @Expose
    @SerializedName("token")
    public String sessionToken;


    public AuthResponse(boolean isError, String message, String sessionToken) {
        this.isError = isError;
        this.message = message;
        this.sessionToken = sessionToken;
    }
}

