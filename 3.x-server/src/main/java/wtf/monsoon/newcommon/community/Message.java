package wtf.monsoon.newcommon.community;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {
    public User author;
    public String message, time;

    public Message(User author, String message) {
        this.author = author;
        this.message = message;

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        Date date = new Date();

        this.time = formatter.format(date);
    }
}
