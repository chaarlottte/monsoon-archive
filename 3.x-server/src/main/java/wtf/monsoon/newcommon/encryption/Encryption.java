package wtf.monsoon.newcommon.encryption;

public interface Encryption {
    byte[] encrypt(final byte[] bytes) throws Exception;

    byte[] decrypt(final byte[] bytes) throws Exception;
}
