package wtf.monsoon.newcommon.encryption.impl;

import wtf.monsoon.newcommon.encryption.Encryption;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class RSAEncryption implements Encryption {

    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    public RSAEncryption() throws Exception {
        final KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);

        final KeyPair pair = keyGen.generateKeyPair();

        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();

        System.out.println(this.publicKey.getClass().getName());
    }

    public RSAEncryption(final PrivateKey privateKey, final PublicKey publicKey) {
        this.privateKey = privateKey;
        this.publicKey = publicKey;
    }

    @Override
    public byte[] encrypt(final byte[] bytes) throws Exception {
        final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);

        return cipher.doFinal(bytes);
    }

    @Override
    public byte[] decrypt(final byte[] bytes) throws Exception {
        final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

        cipher.init(Cipher.DECRYPT_MODE, this.privateKey);

        return cipher.doFinal(bytes);
    }

    public static PublicKey toPublicKey(final String input) throws Exception {
        final byte[] derPublicKey = Base64.getDecoder().decode(input);
        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        final X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(derPublicKey);

        return keyFactory.generatePublic(publicKeySpec);
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }
}
