package com.ridgid.oss.common.security.realm.authentication.secret;

import java.util.Optional;

@SuppressWarnings("SpellCheckingInspection")
public interface CredentialStore<RIDT, IDT, ST, EST extends EncryptedSecret<ST>>
{
    Optional<Credential<RIDT, IDT, ST, EST>> retrieveCredentialFor
            (
                    RIDT realmId,
                    IDT userId
            );
}
