package com.ridgid.oss.common.security.password;

@SuppressWarnings("JavaDoc")
public interface SecurePasswordEntropyChecker {

    /**
     * Takes password and returns true if the password has sufficient entropy according to the rules enforeced by the implementation otherwise, false.
     *
     * @param password
     * @return true if password has sufficient entropy
     */
    boolean hasSufficientEntropy(String password);

    /**
     * Returns a textual description of the rules the implementation is enforcing with regard to password entropy
     *
     * @return - textual description (user meaningful) of the rules
     */
    String rulesAsText();
}
