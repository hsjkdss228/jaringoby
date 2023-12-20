package com.wanted.jaringoby.common.constants;

import java.time.LocalDate;
import java.time.ZoneId;

public class Date {

    public static LocalDate today() {
        return LocalDate.now(ZoneId.of("Asia/Seoul"));
    }
}
