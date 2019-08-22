package com.ridgid.oss.common.security.realm.authentication.secret;


import java.util.function.Predicate;

public interface EncryptedSecret<ST>
{
    static <ST> Predicate<EncryptedSecret<ST>> matchedBy(ST secret) {
        return es -> es.isMatch(secret);
    }
    boolean isMatch(ST secret);
}
