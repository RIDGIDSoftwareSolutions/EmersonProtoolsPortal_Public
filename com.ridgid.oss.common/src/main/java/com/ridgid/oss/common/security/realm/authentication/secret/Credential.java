package com.ridgid.oss.common.security.realm.authentication.secret;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public interface Credential<RIDT, IDT, ST, EST extends EncryptedSecret<ST>>
{
    EST getEncryptedSecret();
}
