package com.ridgid.oss.common.security.password;

@SuppressWarnings({"JavaDoc", "unused"})
public interface SecurePasswordService
{
    /**
     * Encrypts the given password with a cryptographically secure random salt and preferred iterations and key length.
     * Prefer using this method over the other methods to allow for each upgrade to more iterations or longer key length in the future.
     *
     * @param password password to encrypt
     * @return the encrypted password (base-64 encoded), salt used (base-64 encoded), number of iterations used, and key length used
     * @throws SecurePasswordServiceException if the encryption fails in any way including if the password does not meet the minimum requirements for entropy as enforced by the implementation
     */
    SecurePasswordEncryptionResult EncryptPassword(String password)
        throws SecurePasswordServiceException;

    /**
     * Encrypts the given password with the salt provided (must be a base-64 encoded string of bytes for the implementation) using preferred iterations and key length.
     * Prefer using the SecurePasswordEncryptionResult EncryptPassword(String password) instead
     *
     * @param password password to encrypt
     * @param salt     salt to use to encrypt the password
     * @return the encrypted password (base-64 encoded), salt used (base-64 encoded), number of iterations used, and key length used
     * @throws SecurePasswordServiceException if the encryption fails in any way including if the password does not meet the minimum requirements for entropy as enforced by the implementation
     */
    SecurePasswordEncryptionResult EncryptPassword(String password,
                                                   String salt)
        throws SecurePasswordServiceException;

    /**
     * Encrypts the given password with the salt, iterations, and key length provided (must be a valid values for the implementation)
     * Prefer using the SecurePasswordEncryptionResult EncryptPassword(String password) instead
     *
     * @param password   password to encrypt
     * @param salt       salt to use to encrypt the password
     * @param iterations number of iterations to use
     * @param keyLength  key length to use
     * @param verifyEntropy checks the password against the password complexity requirements. Set to <code>false</code> when encrypting the user's password when logging in to prevent unnecessary errors
     * @return the encrypted password (base-64 encoded), salt used (base-64 encoded), number of iterations used, and key length used
     * @throws SecurePasswordServiceException if the encryption fails in any way including if the password does not meet the minimum requirements for entropy as enforced by the implementation
     */
    SecurePasswordEncryptionResult EncryptPassword(String password,
                                                   String salt,
                                                   short iterations,
                                                   short keyLength,
                                                   boolean verifyEntropy)
        throws SecurePasswordServiceException;

    /**
     * Checks if the password given encrypts to same value as the encrypted password in the previousEncryptedPasswordResult using the previousEncryptedPasswordResult salt, iterations, and key length
     *
     * @param password         password to encrypt
     * @param encryptionResult result of a previously encrypted password by one of the EncryptPassword methods
     * @return true if the password given encrypts to the same value using the previously used salt, iterations, and key length; otherwise, false
     * @throws SecurePasswordServiceException
     */
    boolean VerifyPassword(String password,
                           SecurePasswordEncryptionResult encryptionResult)
        throws SecurePasswordServiceException;

    /**
     * Checks if the password given encrypts to same value as the encrypted password in the previousEncryptedPasswordResult using the previousEncryptedPasswordResult salt, iterations, and key length
     *
     * @param password         password to encrypt
     * @param encryptionResult result of a previously encrypted password by one of the EncryptPassword methods
     * @return true if the password given encrypts to the same value using the previously used salt, iterations, and key length; otherwise, false
     * @throws SecurePasswordServiceException
     */
    boolean VerifyLegacySecurePassword(String password,
                                       SecurePasswordEncryptionResult encryptionResult)
        throws SecurePasswordServiceException;

    @SuppressWarnings("WeakerAccess")
    class SecurePasswordServiceException extends RuntimeException
    {
        public final SecurePasswordServiceExceptionType type;

        /**
         * Constructs a new exception with the specified detail message.  The
         * cause is not initialized, and may subsequently be initialized by
         * a call to {@link #initCause}.
         *
         * @param message the detail message. The detail message is saved for
         *                later retrieval by the {@link #getMessage()} method.
         */
        public SecurePasswordServiceException(String message,
                                              SecurePasswordServiceExceptionType type)
        {
            super(message);
            this.type = type;
        }

        /**
         * Constructs a new exception with the specified detail message and
         * cause.  <p>Note that the detail message associated with
         * {@code cause} is <i>not</i> automatically incorporated in
         * this exception's detail message.
         *
         * @param message the detail message (which is saved for later retrieval
         *                by the {@link #getMessage()} method).
         * @param cause   the cause (which is saved for later retrieval by the
         *                {@link #getCause()} method).  (A <tt>null</tt> value is
         *                permitted, and indicates that the cause is nonexistent or
         *                unknown.)
         * @since 1.4
         */
        public SecurePasswordServiceException(String message,
                                              SecurePasswordServiceExceptionType type,
                                              Throwable cause)
        {
            super(message, cause);
            this.type = type;
        }
    }

    enum SecurePasswordServiceExceptionType
    {
        ALGORITHM_NOT_SUPPORTED,
        DOES_NOT_CONTAIN_SUFFICIENT_ENTROPY,
        INVALID_KEY_LENGTH,
        INVALID_NUMBER_OF_CHARACTERS,
        INVALID_NUMBER_OF_ITERATIONS,
        INVALID_SALT,
        SALT_GENERATION_FAILED,
    }
}
