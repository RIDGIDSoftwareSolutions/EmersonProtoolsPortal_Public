package com.ridgid.oss.common.security.realm;

import java.net.InetAddress;

/**
 * @param <RIDT> Realm ID Type
 * @param <IDT>  ID Type
 */
@SuppressWarnings({"SpellCheckingInspection"})
public interface RealmLogger<RIDT, IDT> {

    void warn(RIDT realmId,
              IDT id,
              InetAddress clientNetworkAddress,
              String message,
              Exception ex);

    void warn(RIDT realmId,
              IDT id,
              String message,
              Exception ex);

    void error(RIDT realmId,
               IDT id,
               InetAddress clientNetworkAddress,
               String message,
               Exception ex);
}
