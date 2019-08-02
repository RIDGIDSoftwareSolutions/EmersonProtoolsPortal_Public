package com.ridgid.oss.common.security.realm.authentication.secret;

// TODO: Needs completed
public class StandardEncryptedSecret implements EncryptedSecret<String> {


    @Override
    public boolean isMatch(String secret) {
        return false;
    }
}
