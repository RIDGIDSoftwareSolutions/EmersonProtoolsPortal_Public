package com.ridigid.oss.common.security;

import com.ridgid.oss.common.cache.Expirable;

/**
 * @param <IDT> Identifier Type
 * @param <ATT> Authentication Token Type
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public interface RealmAuthentication<IDT, ATT> extends Expirable {
}
