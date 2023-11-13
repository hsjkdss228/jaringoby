package com.wanted.jaringoby.common.utils;

import de.huxhorn.sulky.ulid.ULID;

public class UlidGenerator {
    private final ULID ulid = new ULID();

    public String createRandomULID() {
        return ulid.nextULID();
    }
}
