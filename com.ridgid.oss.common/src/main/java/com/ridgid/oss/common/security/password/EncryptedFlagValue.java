package com.ridgid.oss.common.security.password;

public enum EncryptedFlagValue {

    ENCRYPTED_PW_NO((short) 0),
    ENCRYPTED_PW_YES((short) 1),
    ENCRYPTED_PW_SUGGEST_CHANGE((short) 2),
    ENCRYPTED_SECURE_PW_YES((short) 3),
    ENCRYPTED_SECURE_PW_SUGGEST_CHANGE((short) 4);

    private short value;

    EncryptedFlagValue(short value) {
        this.value = value;
    }

    public short getValue() { return value; }

    @Override
    public String toString() {
        return Short.toString(value);
    }

    public static EncryptedFlagValue from ( short value ) {
        switch ( value ) {
            case 0:
                return ENCRYPTED_PW_NO;
            case 1:
                return ENCRYPTED_PW_YES;
            case 2:
                return ENCRYPTED_PW_SUGGEST_CHANGE;
            case 3:
                return ENCRYPTED_SECURE_PW_YES;
            case 4:
                return ENCRYPTED_SECURE_PW_SUGGEST_CHANGE;
            default:
                throw new EnumConstantNotPresentException( EncryptedFlagValue.class,
                                                           String.format( "Invalid Constant Value: %d", value ) );
        }
    }
}
