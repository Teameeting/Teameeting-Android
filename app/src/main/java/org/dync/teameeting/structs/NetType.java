package org.dync.teameeting.structs;

/**
 * The type of network connection
 *
 * @author nyist
 */
public enum NetType {
    /**
     * No network connection
     */
    TYPE_NULL,
    /**
     * WIFI connection
     */
    TYPE_WIFI,
    TYPE_WIFI_NULL,
    /**
     * 4G connection
     */
    TYPE_4G,
    /**
     * 3G connection
     */
    TYPE_3G,
    /**
     * 2G connection
     */
    TYPE_2G,
    /**
     * unkenow connection
     */
    TYPE_UNKNOWN

}
