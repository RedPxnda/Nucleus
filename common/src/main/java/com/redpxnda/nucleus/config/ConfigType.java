package com.redpxnda.nucleus.config;

/**
 * Represents the various types of configs
 */
public enum ConfigType {
    /**
     * Only the server has "access". On the client, nothing will be done, leaving potentially null values if the client has access to the config object.
     */
    SERVER_ONLY,

    /**
     * Both the client and server have "access". The config is evaluated on the server and synced to the client.
     */
    SERVER_CLIENT_SYNCED,

    /**
     * Only the client has "access". On the server, nothing will be done, leaving potentially null values if the server has access to the config object.
     */
    CLIENT_ONLY,

    /**
     * Both the client and server have "access". The config is evaluated on each individually, leaving potentially out-of-sync values.
     */
    COMMON
}
