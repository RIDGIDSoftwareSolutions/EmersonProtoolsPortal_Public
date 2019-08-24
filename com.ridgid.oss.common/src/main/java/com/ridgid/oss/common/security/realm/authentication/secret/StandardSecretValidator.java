package com.ridgid.oss.common.security.realm.authentication.secret;

import java.util.Collection;
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
        return
            authenticateToSingleRealm
                (
                    realmId,
                    userId,
                    userSecret
                )
                .map(tokenGenerator::tokenFromEncryptedSecret);
    }

    @Override
    public Optional<ATT> authenticate(Collection<RIDT> realmIds,
                                      IDT userId,
                                      ST userSecret)
    {
        Optional<EST> est = Optional.empty();
        for ( RIDT realmId : realmIds ) {
            est = authenticateToSingleRealm
                (
                    realmId,
                    userId,
                    userSecret
                );
            if ( !est.isPresent() ) return Optional.empty();
        }
        return est.map(tokenGenerator::tokenFromEncryptedSecret);
    }

    private Optional<EST> authenticateToSingleRealm(RIDT realmId, IDT userId, ST userSecret) {
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
                );
    }
}
