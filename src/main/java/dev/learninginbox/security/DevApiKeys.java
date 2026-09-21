package dev.learninginbox.security;

import java.util.UUID;

/** Seed credentials from Flyway V2 — local and tests only. */
public final class DevApiKeys {
    public static final UUID ALICE_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
    public static final UUID BOB_ID = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
    public static final String ALICE = "li_alice_dev_key_001";
    public static final String BOB = "li_bob_dev_key_002";

    private DevApiKeys() {}
}
