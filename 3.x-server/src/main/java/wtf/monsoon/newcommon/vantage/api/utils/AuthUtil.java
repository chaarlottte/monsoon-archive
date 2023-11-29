package wtf.monsoon.newcommon.vantage.api.utils;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import wtf.monsoon.newcommon.vantage.api.RequestUtil;
import wtf.monsoon.newcommon.vantage.api.models.AuthResponse;
import wtf.monsoon.newcommon.vantage.api.utils.enc.AESUtil;
import wtf.monsoon.newcommon.vantage.api.utils.enc.RSAUtil;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;

public class AuthUtil {
    private static final Gson g = new Gson();
    private static final String RSA_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAw4Pu3HXzzT5rjKjeIJbdjQ6P/v9pOEi83DBP1z+tCNbxonaH/uh3lshon/tgiSLo73XwcULUn0WRHgIyg8sTI/0T24E9Un55m7Pot8HIb1H7SMwZ5NweZPetNLIpOExPlPGpGOv+BuHCJ7IcQFM0FtJGDGd7+0tiG2SDuPtSJz1kTw8ggLfEvkaFDFwAsdIFDKQ70l69D91K8ymiyrEyDsH+vfWoBCbcUO9MdXwX9KVH5MnQTI8opW//hmipTncKaUQ4/ZbYBwVLVkEA0EMNHHt2WNx5+eU4fikiS8Bs5U/GX8IvEBwEttRevqggxVCqCE6rL6NqwMESKVtTvkmSRwIDAQAB";
    private static final String BASE_URL = "https://vantage.rip";
    private static final String PRODUCT_ID = "63d106727842de723ada3bf0";

    public static AuthResponse authenticate(String username) throws Exception {
        return authenticate(username, HWIDUtil.getHWID());
    }

    public static AuthResponse authenticate(String username, String hwid) throws Exception {
        /* Generate AES Key to Layer with RSA */
        SecretKey key = AESUtil.getSecretKey();
        IvParameterSpec iv = AESUtil.generateIv();

        /* Get Base64 encoded Key Data to send within request */
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());

        /* Create Maps for JSON Data to send within request */
        LinkedTreeMap<String, Object> mainMap = new LinkedTreeMap<>();
        LinkedTreeMap<String, String> aes = new LinkedTreeMap<>();
        LinkedTreeMap<String, String> data = new LinkedTreeMap<>();

        /* Add AES data to send with request */
        aes.put("key", encodedKey);
        aes.put("iv", Base64.getEncoder().encodeToString(iv.getIV()));

        /* Add auth data that will get Encrypted */
        data.put("username", username);
        data.put("pid", PRODUCT_ID);
        data.put("hwid", hwid);
        data.put("ts", String.valueOf(Instant.now().getEpochSecond()));

        /* Encrypt the AES data with the RSA Public Key as RSA cannot encrypt large amounts of data */
        mainMap.put("aes", RSAUtil.encrypt(g.toJson(aes), RSA_KEY));

        /* Encrypt the Login Information with AES, which will be decrypted via the PrivateKey */
        mainMap.put("data", AESUtil.encrypt(g.toJson(data), key, iv));

        /* Convert map to String JSON */
        String jsonFormat = g.toJson(mainMap);
        //create the body, add the headers, etc

        String resp = RequestUtil.postRequest(
                String.format("%s/api/security/auth/verify", BASE_URL), jsonFormat);

        HashMap outputMap = g.fromJson(resp, HashMap.class);
        HashMap decrypted = g.fromJson(AESUtil.decrypt((String) outputMap.get("data"), key, iv), HashMap.class);
        return g.fromJson(g.toJson(decrypted, HashMap.class), AuthResponse.class);
    }
}
