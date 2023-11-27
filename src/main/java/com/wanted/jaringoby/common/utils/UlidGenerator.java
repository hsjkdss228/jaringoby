package com.wanted.jaringoby.common.utils;

import de.huxhorn.sulky.ulid.ULID;

public class UlidGenerator {

    private final ULID ulid = new ULID();

    public String createRandomCustomerULID() {
        return "CUSTOMER_" + ulid.nextULID();
    }

    public String createRandomCustomerRefreshTokenULID() {
        return "CUSTOMER_REFRESH_TOKEN_" + ulid.nextULID();
    }

    public String createRandomLedgerULID() {
        return "LEDGER_" + ulid.nextULID();
    }

    public String createRandomBudgetULID() {
        return "BUDGET_" + ulid.nextULID();
    }
}
