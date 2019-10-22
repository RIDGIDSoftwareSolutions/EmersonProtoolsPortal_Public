package com.ridgid.oss.common.security.password;

import java.util.Objects;

@SuppressWarnings({"WeakerAccess", "unused"})
public class SecurePasswordEncryptionResult
{
    public final String             encryptedPassword;
    public final String             salt;
    public final short              iterations;
    public final short              keyLength;
    public final EncryptedFlagValue encryptedFlagValue;

    public SecurePasswordEncryptionResult(String encryptedPassword,
                                          String salt,
                                          short iterations,
                                          short keyLength)
    {
        this.encryptedPassword  = encryptedPassword;
        this.salt               = salt;
        this.iterations         = iterations;
        this.keyLength          = keyLength;
        this.encryptedFlagValue = EncryptedFlagValue.ENCRYPTED_SECURE_PW_YES;
    }

    public SecurePasswordEncryptionResult(String encryptedPassword) {
        this.encryptedPassword  = encryptedPassword;
        this.salt               = "";
        this.iterations         = 0;
        this.keyLength          = 0;
        this.encryptedFlagValue = EncryptedFlagValue.ENCRYPTED_PW_YES;
    }

    @Override
    public int hashCode() {
        return Objects.hash(encryptedPassword,
                            salt,
                            iterations,
                            keyLength,
                            encryptedFlagValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SecurePasswordEncryptionResult)) return false;
        SecurePasswordEncryptionResult that = (SecurePasswordEncryptionResult) o;
        return iterations == that.iterations &&
               keyLength == that.keyLength &&
               encryptedPassword.equals(that.encryptedPassword) &&
               salt.equals(that.salt) &&
               encryptedFlagValue == that.encryptedFlagValue;
    }

    @Override
    public String toString() {
        return "SecurePasswordEncryptionResult{" +
               "encryptedPassword='" + encryptedPassword + '\'' +
               ", salt='" + salt + '\'' +
               ", iterations=" + iterations +
               ", keyLength=" + keyLength +
               ", encryptedFlagValue=" + encryptedFlagValue +
               '}';
    }
}
