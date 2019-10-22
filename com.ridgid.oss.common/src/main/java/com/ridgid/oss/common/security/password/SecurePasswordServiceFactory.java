package com.ridgid.oss.common.security.password;

@SuppressWarnings({"unused", "WeakerAccess"})
public class SecurePasswordServiceFactory
{
    /**
     * Password entropy checker used by the SecurePasswordService. If null (the default), no entropy check of the password is performed.
     */
    private static SecurePasswordEntropyChecker PasswordEntropyCheck = new StandardPasswordEntropyChecker();

    private static       SecurePasswordService instance     = null;
    private static final Object                instanceLock = new Object();

    public static SecurePasswordService NewInstance() {
        synchronized ( instanceLock ) {
            if ( instance == null ) instance = new SecurePasswordServiceImpl(PasswordEntropyCheck);
        }
        return instance;
    }

    public static void setPasswordEntropyCheck(SecurePasswordEntropyChecker passwordEntropyCheck) {
        synchronized ( instanceLock) {
            instance = null;
            PasswordEntropyCheck = passwordEntropyCheck;
        }
    }

    public static SecurePasswordEntropyChecker getPasswordEntropyCheck() {
        return PasswordEntropyCheck;
    }
}