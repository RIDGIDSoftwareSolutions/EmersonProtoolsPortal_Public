package com.ridgid.oss.common.security.password;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the SecurePasswordService interface and implementation provided by the SecurePasswordServiceFactory
 */
@SuppressWarnings({"WeakerAccess", "UnnecessaryLocalVariable", "unused"})
public class SecurePasswordServiceTest
{

    private static final String PASSWORD_WITH_SUFFICIENT_ENTROPY_1 = "pwdThatH453n0ugh3nTr0Py";
    private static final String PASSWORD_WITH_SUFFICIENT_ENTROPY_2 = "pwdThat4L5OH453n0ugh3nTr0Py";

    private static final String PASSWORD_WITH_INSUFFICIENT_ENTROPY_1 = "XXXX";

    @Test
    public void instantiate_a_SecurePasswordService_works_via_SecurePasswordServiceFactory() {
        SecurePasswordService pws = assertDoesNotThrow(SecurePasswordServiceFactory::NewInstance);
        assertNotNull(pws, "Password Service should not be null");
    }

    @Test
    public void encrypt_a_password_that_meets_entropy_requirements_should_produce_valid_result() {

        // Arrange
        SecurePasswordService pws          = SecurePasswordServiceFactory.NewInstance();
        String                passwordUsed = PASSWORD_WITH_SUFFICIENT_ENTROPY_1;

        // Act
        SecurePasswordEncryptionResult actualResult =
            assertDoesNotThrow(() -> pws.EncryptPassword(passwordUsed),
                               "Should not throw an exception for a password with sufficient entropy");

        // Assert
        assertNotNull(actualResult, "Encrypted password result must never be null");

        assertNotEquals(actualResult.encryptedPassword, actualResult.salt,
                        "Salt and Encrypted Password must not be equal");
        assertNotEquals(actualResult.encryptedPassword, passwordUsed,
                        "Encrypted password must not match unencrypted password");
        assertThat("Encrypted password must be at least 32 characters in length",
                   actualResult.encryptedPassword.length(), greaterThanOrEqualTo(32));
        assertThat("Encrypted password must be at most 64 characters in length",
                   actualResult.encryptedPassword.length(), lessThanOrEqualTo(64));

        assertNotEquals(actualResult.salt, passwordUsed, "salt must not match unencrypted password");
        assertThat("salt must be at least 24 characters in length", actualResult.salt.length(),
                   greaterThanOrEqualTo(24));
        assertThat("salt must be at most 64 characters in length", actualResult.salt.length(), lessThanOrEqualTo(64));

        assertThat("iterations must be at least 1024", actualResult.iterations, greaterThanOrEqualTo((short) 1024));
        assertThat("iterations must be at most 8192", actualResult.iterations, lessThanOrEqualTo((short) 8192));

        assertThat("key length must be at least 192", actualResult.keyLength, greaterThanOrEqualTo((short) 192));
        assertThat("key length must be at most 4096", actualResult.keyLength, lessThanOrEqualTo((short) 4096));

    }

    @Test
    public void encrypt_a_password_should_require_no_more_than_1_second() {

        // Arrange
        SecurePasswordService pws          = SecurePasswordServiceFactory.NewInstance();
        String                passwordUsed = PASSWORD_WITH_SUFFICIENT_ENTROPY_1;

        // Act & Assert
        SecurePasswordEncryptionResult actualResult =
            assertTimeout(Duration.ofMillis(1500), () -> pws.EncryptPassword(passwordUsed),
                          "Should not require more than about 1 second to encrypt");
    }

    @Test
    public void encrypting_two_different_passwords_results_in_different_encrypted_passwords_at_least_99_out_of_100_times() {

        // Arrange
        SecurePasswordService pws           = SecurePasswordServiceFactory.NewInstance();
        String                passwordUsed1 = PASSWORD_WITH_SUFFICIENT_ENTROPY_1;
        String                passwordUsed2 = PASSWORD_WITH_SUFFICIENT_ENTROPY_2;
        int                   timesMatched  = 0;

        // Act
        for (int i = 0; i < 100; i++) {
            SecurePasswordEncryptionResult actualResult1 = assertDoesNotThrow(() -> pws.EncryptPassword(passwordUsed1));
            SecurePasswordEncryptionResult actualResult2 = assertDoesNotThrow(() -> pws.EncryptPassword(passwordUsed2));
            if (actualResult1.encryptedPassword.equals(actualResult2.encryptedPassword)) timesMatched++;
        }

        // Assert
        assertThat("two different passwords encrypted will at most have an encrypted match 1 out of 100 times",
                   timesMatched, lessThanOrEqualTo(1));
    }

    @Disabled(
        "Long-Running. Manually invoke to test during development: (encrypting_two_different_passwords_results_in_different_encrypted_passwords_at_least_99999_out_of_100000_times)")
    @Test
    public void encrypting_two_different_passwords_results_in_different_encrypted_passwords_at_least_99999_out_of_100000_times() {

        // Arrange
        SecurePasswordService pws           = SecurePasswordServiceFactory.NewInstance();
        String                passwordUsed1 = PASSWORD_WITH_SUFFICIENT_ENTROPY_1;
        String                passwordUsed2 = PASSWORD_WITH_SUFFICIENT_ENTROPY_2;
        int                   timesMatched  = 0;

        // Act
        for (int i = 0; i < 100000; i++) {
            SecurePasswordEncryptionResult actualResult1 = assertDoesNotThrow(() -> pws.EncryptPassword(passwordUsed1));
            SecurePasswordEncryptionResult actualResult2 = assertDoesNotThrow(() -> pws.EncryptPassword(passwordUsed2));
            if (actualResult1.encryptedPassword.equals(actualResult2.encryptedPassword)) timesMatched++;
        }

        // Assert
        assertThat("two different passwords encrypted will at most have an encrypted match 1 out of 100,000 times",
                   timesMatched, lessThanOrEqualTo(1));
    }

    @Test
    public void encrypting_same_password_twice_consecutively_results_in_different_encrypted_passwords_at_least_99_out_of_100_times() {

        // Arrange
        SecurePasswordService pws           = SecurePasswordServiceFactory.NewInstance();
        String                passwordUsed1 = PASSWORD_WITH_SUFFICIENT_ENTROPY_1;
        int                   timesMatched  = 0;

        // Act
        for (int i = 0; i < 100; i++) {
            SecurePasswordEncryptionResult actualResult1 = assertDoesNotThrow(() -> pws.EncryptPassword(passwordUsed1));
            SecurePasswordEncryptionResult actualResult2 = assertDoesNotThrow(() -> pws.EncryptPassword(passwordUsed1));
            if (actualResult1.encryptedPassword.equals(actualResult2.encryptedPassword)) timesMatched++;
        }

        // Assert
        assertThat(
            "same password encrypted twice consecutively will at most have an encrypted match 1 out of 1,000 times",
            timesMatched, lessThanOrEqualTo(1));
    }

    @Test
    public void encrypt_password_with_the_same_salt_should_result_in_same_encrypted_password() {

        // Arrange
        SecurePasswordService pws          = SecurePasswordServiceFactory.NewInstance();
        String                passwordUsed = PASSWORD_WITH_SUFFICIENT_ENTROPY_1;

        // Act & Assert Repeatedly
        for (int i = 0; i < 100; i++) {

            SecurePasswordEncryptionResult firstResult  = assertDoesNotThrow(() -> pws.EncryptPassword(passwordUsed));
            SecurePasswordEncryptionResult secondResult = assertDoesNotThrow(() -> pws.EncryptPassword(passwordUsed));
            SecurePasswordEncryptionResult sameSaltResult = assertDoesNotThrow(
                () -> pws.EncryptPassword(passwordUsed, firstResult.salt));

            assertThat("same password encrypted with same salt has same encrypted password",
                       sameSaltResult.encryptedPassword, equalTo(firstResult.encryptedPassword));
            assertThat("same password encrypted with same salt has same salt", sameSaltResult.salt,
                       equalTo(firstResult.salt));

            String secondSalt = secondResult.salt;

            SecurePasswordEncryptionResult differentSaltResult = assertDoesNotThrow(
                () -> pws.EncryptPassword(passwordUsed, secondSalt));

            assertThat("same password encrypted with different salt has different encrypted password",
                       differentSaltResult.encryptedPassword, not(equalTo(firstResult.encryptedPassword)));
            assertThat("same password encrypted with different salt has different salt", differentSaltResult.salt,
                       not(equalTo(firstResult.salt)));
        }

    }

    @Test
    // NOTE: This test ensures that the implementation doesn't actually change to an incompatible algorithm without us updating the tests and being aware of the change
    // If this test fails, don't just change the test to make it pass. The team should discuss and ensure we haven't messed up the security
    public void whitebox_regression_enccrypt_specific_password_with_specific_salt_iterations_keylength_results_in_specific_encrypted_password() {

        // Arrange
        SecurePasswordService pws                        = SecurePasswordServiceFactory.NewInstance();
        String                passwordUsed1              = PASSWORD_WITH_SUFFICIENT_ENTROPY_1;
        String                passwordUsed2              = PASSWORD_WITH_SUFFICIENT_ENTROPY_2;
        String                salt1                      = "aQS4hQvXGGDqAlCR9fdj1g==";
        String                salt2                      = "CFzRrDyAU4nZUWJiXv3SFg==";
        String                expectedEncryptedPassword1 = "zxRVegB67L+AfzXkWbYHJwWwvvJ6CMJ1DvmLrva8R68=";
        String                expectedEncryptedPassword2 = "LxE82GOKoIvzNCoGpjT0oX0jZZubVyDq54P6wFsj2sg=";
        short                 iterations                 = 4096;
        short                 keyLength                  = 256;

        // Act
        SecurePasswordEncryptionResult firstResult = assertDoesNotThrow(
            () -> pws.EncryptPassword(passwordUsed1, salt1, iterations, keyLength, true));
        SecurePasswordEncryptionResult secondResult = assertDoesNotThrow(
            () -> pws.EncryptPassword(passwordUsed2, salt2, iterations, keyLength, true));

        // Assert
        assertThat("actual encrypted password 1 exactly matches the expected encrypted password 1",
                   firstResult.encryptedPassword, equalTo(expectedEncryptedPassword1));
        assertThat("actual encrypted password 2 exactly matches the expected encrypted password 2",
                   secondResult.encryptedPassword, equalTo(expectedEncryptedPassword2));
    }

    @Test
    public void verify_password_works_after_encrypting_a_password_and_using_the_result_as_input_with_the_original_password() {

        // Arrange
        SecurePasswordService          pws           = SecurePasswordServiceFactory.NewInstance();
        String                         passwordUsed1 = PASSWORD_WITH_SUFFICIENT_ENTROPY_1;
        String                         passwordUsed2 = PASSWORD_WITH_SUFFICIENT_ENTROPY_2;
        SecurePasswordEncryptionResult firstResult   = assertDoesNotThrow(() -> pws.EncryptPassword(passwordUsed1));
        SecurePasswordEncryptionResult secondResult  = assertDoesNotThrow(() -> pws.EncryptPassword(passwordUsed2));

        // Act
        boolean actualVerifiedFirst     = assertDoesNotThrow(() -> pws.VerifyPassword(passwordUsed1, firstResult));
        boolean actualVerifiedSecond    = assertDoesNotThrow(() -> pws.VerifyPassword(passwordUsed2, secondResult));
        boolean actualNotVerifiedFirst  = assertDoesNotThrow(() -> pws.VerifyPassword(passwordUsed1, secondResult));
        boolean actualNotVerifiedSecond = assertDoesNotThrow(() -> pws.VerifyPassword(passwordUsed2, firstResult));

        // Assert
        assertThat("password1 verified against it's encrypted version is correct (true)", actualVerifiedFirst,
                   equalTo(true));
        assertThat("password2 verified against it's encrypted version is correct (true)", actualVerifiedSecond,
                   equalTo(true));
        assertThat("password1 verified against a different encrypted password is correct (false)",
                   actualNotVerifiedFirst, equalTo(false));
        assertThat("password2 verified against a different encrypted password is correct (false)",
                   actualNotVerifiedSecond, equalTo(false));
    }

    @Test
    public void encrypt_password_throws_exception_if_called_with_a_password_that_doesnot_meet_the_entropy_checks() {

        // Arrange
        SecurePasswordService        pws                       = SecurePasswordServiceFactory.NewInstance();
        String                       goodPasswordUsed          = PASSWORD_WITH_SUFFICIENT_ENTROPY_1;
        String                       badPasswordUsed           = PASSWORD_WITH_INSUFFICIENT_ENTROPY_1;
        SecurePasswordEntropyChecker savedPasswordEntropyCheck = SecurePasswordServiceFactory.getPasswordEntropyCheck();
        SecurePasswordServiceFactory.setPasswordEntropyCheck
            (
                new SecurePasswordEntropyChecker()
                {
                    @Override
                    public boolean hasSufficientEntropy(String password) {
                        return password.length() > 4;
                    }

                    @Override
                    public String rulesAsText() {
                        return "Password must be longer than 4 characters";
                    }
                }
            );

        // Act & Assert
        SecurePasswordEncryptionResult goodPasswordActualResult =
            assertDoesNotThrow(() -> pws.EncryptPassword(goodPasswordUsed),
                               "Should not throw an exception for a password with sufficient entropy");
        assertThrows(SecurePasswordService.SecurePasswordServiceException.class,
                     () -> pws.EncryptPassword(badPasswordUsed),
                     "Should throw an exception for a password with insufficient entropy");

        // Clean-up
        SecurePasswordServiceFactory.setPasswordEntropyCheck(savedPasswordEntropyCheck);

    }

    @Test
    public void encrypt_password_doesnot_throw_exception_if_called_with_a_password_that_doesnot_meet_the_entropy_checks_when_no_entropychecker_provided() {

        // Arrange
        SecurePasswordEntropyChecker savedPasswordEntropyCheck = SecurePasswordServiceFactory.getPasswordEntropyCheck();
        SecurePasswordServiceFactory.setPasswordEntropyCheck(null);
        SecurePasswordService pws              = SecurePasswordServiceFactory.NewInstance();
        String                goodPasswordUsed = PASSWORD_WITH_SUFFICIENT_ENTROPY_1;
        String                badPasswordUsed  = PASSWORD_WITH_INSUFFICIENT_ENTROPY_1;

        // Act & Assert
        SecurePasswordEncryptionResult goodPasswordActualResult =
            assertDoesNotThrow(() -> pws.EncryptPassword(goodPasswordUsed),
                               "Should not throw an exception for a password with sufficient entropy");
        SecurePasswordEncryptionResult badPasswordActualResult =
            assertDoesNotThrow(() -> pws.EncryptPassword(badPasswordUsed),
                               "Should not throw an exception for a password with insufficient entropy if no entropy checker provided");

        // Clean-up
        SecurePasswordServiceFactory.setPasswordEntropyCheck(savedPasswordEntropyCheck);

    }
}