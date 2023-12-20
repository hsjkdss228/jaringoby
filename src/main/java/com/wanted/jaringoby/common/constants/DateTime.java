package com.wanted.jaringoby.common.constants;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateTime {

    public static LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    }
}
