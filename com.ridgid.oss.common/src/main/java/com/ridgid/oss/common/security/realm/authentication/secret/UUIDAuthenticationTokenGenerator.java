package com.ridgid.oss.common.security.realm.authentication.secret;

import java.util.UUID;

@SuppressWarnings("unused")
public class UUIDAuthenticationTokenGenerator<ST, EST extends EncryptedSecret<ST>>
        implements AuthenticationTokenGenerator<ST, EST, UUID> {

    @Override
    public UUID tokenFromEncryptedSecret(EST encryptedSecret) {
        return UUID.randomUUID();
    }
}
