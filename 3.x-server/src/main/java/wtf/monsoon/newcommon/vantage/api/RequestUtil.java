package wtf.monsoon.newcommon.vantage.api;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

public class RequestUtil {
    public static String getRequest(String url) throws IOException {
        HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
        conn.addRequestProperty("User-Agent", "Vantage-API");

        BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String lbf;
        StringBuilder resp = new StringBuilder();
        while ((lbf = bf.readLine()) != null)
            resp.append(lbf);

        return resp.toString();
    }

    public static String postRequest(String url, String jsonAsString) throws IOException {
        HttpsURLConnection conn = (HttpsURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("User-Agent", "Vantage-API");
        conn.setDoOutput(true);
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonAsString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        BufferedReader br;
        if (responseCode >= 200 && responseCode < 300) {
            br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));
        } else {
            br = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), "utf-8"));
        }

        StringBuilder response = new StringBuilder();
        String responseLine = null;
        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }
        // System.out.println(response.toString());
        return response.toString();
    }
}
