package com.ridgid.oss.common.security.password;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.stream.Stream;

import static com.ridgid.oss.common.security.password.SecurePasswordService.SecurePasswordServiceExceptionType.*;

@SuppressWarnings("SpellCheckingInspection")
public class SecurePasswordServiceImpl
    implements SecurePasswordService
{
    private static final short  ITERATIONS            = 4096;
    private static final short  KEY_LENGTH            = 256; // bits
    private static final String KEY_ALGORITHM         = "PBKDF2WithHmacSHA256";
    private static final String SALT_RANDOM_ALGORITHM = "SHA1PRNG";

    private final SecurePasswordEntropyChecker passwordEntropyCheck;

    SecurePasswordServiceImpl(SecurePasswordEntropyChecker passwordEntropyCheck) {
        this.passwordEntropyCheck = passwordEntropyCheck;
    }

    @Override
    public SecurePasswordEncryptionResult EncryptPassword(String password)
        throws SecurePasswordServiceException
    {
        checkPasswordEntropy(password);
        byte[] saltBytes = generateSalt();
        String salt      = Base64.getEncoder().encodeToString(saltBytes);
        return generateSecurePasswordEncryptionResult(password,
                                                      salt,
                                                      saltBytes,
                                                      ITERATIONS,
                                                      KEY_LENGTH);
    }

    @Override
    public SecurePasswordEncryptionResult EncryptPasswordWithoutCheckingEntropy(String password) throws SecurePasswordServiceException {
        byte[] saltBytes = generateSalt();
        String salt      = Base64.getEncoder().encodeToString(saltBytes);
        return generateSecurePasswordEncryptionResult(password,
                salt,
                saltBytes,
                ITERATIONS,
                KEY_LENGTH);
    }

    @Override
    public SecurePasswordEncryptionResult EncryptPassword(String password, String salt)
        throws SecurePasswordServiceException
    {
        return generateSecurePasswordEncryptionResult(password,
                                                      salt,
                                                      ITERATIONS,
                                                      KEY_LENGTH,
                                                      true);
    }

    @Override
    public SecurePasswordEncryptionResult EncryptPassword(String password,
                                                          String salt,
                                                          short iterations,
                                                          short keyLength,
                                                          boolean verifyEntropy)
        throws SecurePasswordServiceException
    {
        validateIterationsAndKeyLength(iterations, keyLength);
        return generateSecurePasswordEncryptionResult(password,
                                                      salt,
                                                      iterations,
                                                      keyLength,
                                                      verifyEntropy);
    }

    @Override
    public boolean VerifyPassword(String password,
                                  SecurePasswordEncryptionResult encryptionResult)
        throws SecurePasswordServiceException
    {
        return Stream.of(password,
                         password.toLowerCase(),
                         password.toUpperCase())
                     .map(p -> EncryptPassword(password,
                                               encryptionResult.salt,
                                               encryptionResult.iterations,
                                               encryptionResult.keyLength,
                                               false))
                     .anyMatch(encryptionResult::equals);
    }

    @Override
    public boolean VerifyLegacySecurePassword(String password,
                                              SecurePasswordEncryptionResult encryptionResult)
        throws SecurePasswordServiceException
    {
        return encryptionResult.encryptedPassword.equalsIgnoreCase(legacyEncrypt(password))
               || encryptionResult.encryptedPassword.equalsIgnoreCase(legacyEncrypt(password.toLowerCase()))
               || encryptionResult.encryptedPassword.equalsIgnoreCase(legacyEncrypt(password.toUpperCase()))
               || encryptionResult.encryptedPassword.equalsIgnoreCase(password);
    }

    /*
     *
     * PRIVATE METHODS
     *
     */

    private static String legacyEncrypt(String plaintext)
    {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("PasswordService.java::encrypt()", e);
        }
        md.update(plaintext.getBytes(StandardCharsets.UTF_8));
        byte[] unencryptedPass = md.digest();
        return Base64.getEncoder()
                     .encodeToString(unencryptedPass)
                     .replaceAll("[\\r\\n]", "");
    }

    private byte[] generateSalt()
        throws SecurePasswordServiceException
    {
        try {
            SecureRandom sr   = SecureRandom.getInstance(SALT_RANDOM_ALGORITHM);
            byte[]       salt = new byte[16];
            sr.nextBytes(salt);
            return salt;
        } catch (NoSuchAlgorithmException e) {
            throw new SecurePasswordServiceException("Unable to generate Salt", SALT_GENERATION_FAILED, e);
        }
    }

    private SecurePasswordEncryptionResult generateSecurePasswordEncryptionResult(String password,
                                                                                  String salt,
                                                                                  short iterations,
                                                                                  short keyLength,
                                                                                  boolean verifyEntropy)
        throws SecurePasswordServiceException
    {
        if (verifyEntropy) {
            checkPasswordEntropy(password);
        }

        byte[] saltBytes = validateSaltAndConvertToBytes(salt);
        return generateSecurePasswordEncryptionResult(password,
                                                      salt,
                                                      saltBytes,
                                                      iterations,
                                                      keyLength);
    }

    private SecurePasswordEncryptionResult generateSecurePasswordEncryptionResult(String password,
                                                                                  String salt,
                                                                                  byte[] saltBytes,
                                                                                  short iterations,
                                                                                  short keyLength)
        throws SecurePasswordServiceException
    {
        char[] passwordChars = password.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(
            passwordChars,
            saltBytes,
            iterations,
            keyLength
        );

        SecretKeyFactory key;
        try {
            key = SecretKeyFactory.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new SecurePasswordServiceException(
                String.format("Unable to obtain %s Algorithm Instance", KEY_ALGORITHM),
                ALGORITHM_NOT_SUPPORTED,
                e);
        }

        byte[] hashedPassword;
        try {
            hashedPassword = key.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            throw new SecurePasswordServiceException(
                String.format("Unable to obtain %s Algorithm Instance", KEY_ALGORITHM), ALGORITHM_NOT_SUPPORTED,
                e);
        }

        String encryptedPassword = Base64.getEncoder().encodeToString(hashedPassword);

        return new SecurePasswordEncryptionResult(encryptedPassword,
                                                  salt,
                                                  iterations,
                                                  keyLength);
    }

    private void checkPasswordEntropy(String password)
        throws SecurePasswordServiceException
    {
        if (passwordEntropyCheck == null || passwordEntropyCheck.hasSufficientEntropy(password)) return;
        throw new SecurePasswordServiceException
            (
                "Password Invalid. Must match the following rules: "
                + passwordEntropyCheck.rulesAsText(),
                DOES_NOT_CONTAIN_SUFFICIENT_ENTROPY
            );

    }

    private byte[] validateSaltAndConvertToBytes(String salt)
        throws SecurePasswordServiceException
    {
        byte[] saltBytes;
        try {
            saltBytes = Base64.getDecoder().decode(salt);
        } catch (Throwable t) {
            throw new SecurePasswordServiceException("Salt is invalid (Not Base-64 Encoded?)", INVALID_SALT, t);
        }
        if (saltBytes.length != 16)
            throw new SecurePasswordServiceException(
                "Salt is invalid (Must be exactly 16 bytes when Base-64 decoded)", INVALID_SALT);
        return saltBytes;
    }

    private void validateIterationsAndKeyLength(short iterations,
                                                short keyLength)
        throws SecurePasswordServiceException
    {
        if (iterations < 1024 || iterations > 8192)
            throw new SecurePasswordServiceException(
                "Number of iterations must be between 1,024 and 8,192 inclusive", INVALID_NUMBER_OF_ITERATIONS);
        if (keyLength != 256)
            throw new SecurePasswordServiceException("Key Length must be 256", INVALID_KEY_LENGTH);
    }

}
