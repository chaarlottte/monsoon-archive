package wtf.monsoon.newcommon.encryption;

import wtf.monsoon.newcommon.encryption.impl.AESEncryption;
import wtf.monsoon.newcommon.util.interfaces.Factory;

public final class EncryptionFactory implements Factory<Encryption> {

    private String key;

    public EncryptionFactory setKey(final String key) {
        this.key = key;
        return this;
    }

    @Override
    public Encryption build() {
        try {
            return new AESEncryption(this.key);
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
