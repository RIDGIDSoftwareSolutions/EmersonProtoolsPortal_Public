package com.ridgid.oss.common.security.realm.authentication.secret;

@FunctionalInterface
@SuppressWarnings({"unused"})
public interface AuthenticationTokenGenerator<ST, EST extends EncryptedSecret<ST>, ATT> {

    ATT tokenFromEncryptedSecret(EST encryptedSecret);
}
