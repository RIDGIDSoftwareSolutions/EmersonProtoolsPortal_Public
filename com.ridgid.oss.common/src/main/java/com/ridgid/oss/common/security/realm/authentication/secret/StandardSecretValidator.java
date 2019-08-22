package com.ridgid.oss.common.security.realm.authentication.secret;

import java.util.Optional;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class StandardSecretValidator<RIDT, IDT, ST, ATT, EST extends EncryptedSecret<ST>>
    implements SecretValidator<RIDT, IDT, ST, ATT>
{
    private final CredentialStore<RIDT, IDT, ST, EST>        credentialStore;
    private final AuthenticationTokenGenerator<ST, EST, ATT> tokenGenerator;

    public StandardSecretValidator(CredentialStore<RIDT, IDT, ST, EST> credentialStore,
                                   AuthenticationTokenGenerator<ST, EST, ATT> tokenGenerator)
    {
        this.credentialStore = credentialStore;
        this.tokenGenerator  = tokenGenerator;
    }

    @Override
    public Optional<ATT> authenticate(RIDT realmId,
                                      IDT userId,
                                      ST userSecret)
    {
        return credentialStore
            .retrieveCredentialFor
                (
                    realmId,
                    userId
                )
            .map
                (
                    Credential::getEncryptedSecret
                )
            .filter
                (
                    EncryptedSecret.matchedBy(userSecret)
                )
            .map
                (
                    tokenGenerator::tokenFromEncryptedSecret
                );
    }
}
