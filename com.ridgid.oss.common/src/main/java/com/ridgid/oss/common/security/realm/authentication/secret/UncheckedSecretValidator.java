package com.ridgid.oss.common.security.realm.authentication.secret;

import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class UncheckedSecretValidator<RIDT, IDT, ST, ATT> implements SecretValidator<RIDT, IDT, ST, ATT> {

    private final Supplier<ATT> authTokenSupplier;

    public UncheckedSecretValidator(Supplier<ATT> authTokenSupplier) {
        this.authTokenSupplier = authTokenSupplier;
    }

    @Override
    public Optional<ATT> authenticate(RIDT realmId,
                                      IDT userId,
                                      ST userSecret) {
        return Optional.ofNullable(authTokenSupplier.get());
    }
}
