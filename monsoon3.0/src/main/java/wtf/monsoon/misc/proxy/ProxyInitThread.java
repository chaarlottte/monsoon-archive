package wtf.monsoon.misc.proxy;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import wtf.monsoon.Wrapper;

public class ProxyInitThread extends Thread {

    private String username = "", password = "";
    private final String host;
    private final int port;
    private String status;

    public ProxyInitThread(String host, int port, String username, String password) {
        super("Alt Login Thread");
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.status = "Waiting...";
    }

    public ProxyInitThread(String host, int port) {
        super("Alt Login Thread");
        this.host = host;
        this.port = port;
        this.status = "Waiting...";
    }

    private Proxy createSession() {
        if (!this.username.equals("") && !this.password.equals("")) {
            Authenticator.setDefault(new Authenticator() {
                public PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password.toCharArray());
                }
            });
        } else {
            Authenticator.setDefault(null);
        }
        return new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(this.host, this.port));
    }

    public String getStatus() {
        return this.status;
    }

    @Override
    public void run() {
        try {
            Wrapper.getMonsoon().setProxy(this.createSession());
            this.status = "Success! IP: " + this.host + ", Port: " + this.port;
        } catch (Exception e) {
            e.printStackTrace();
            this.status = "Failed.";
        }
        System.out.println(this.status);
    }

    public void setStatus(String status) {
        this.status = status;
    }
}