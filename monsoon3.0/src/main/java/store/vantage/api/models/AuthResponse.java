package store.vantage.api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    @Expose
    @SerializedName("error")
    public boolean isError;

    @Expose
    @SerializedName("msg")
    public String message;

    public AuthResponse(boolean isError, String message) {
        this.isError = isError;
        this.message = message;
    }
}

