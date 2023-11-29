package com.wanted.jaringoby.common.constants;

import java.time.LocalDate;
import java.time.ZoneId;

public class Date {

    // TODO: TODAY로 명칭 변경
    //       메서드 혹은 테스트 메서드에서 now로 사용하고 있을 경우에도 today로 변경해야 함
    //
    public static final LocalDate NOW = LocalDate.now(ZoneId.of("Asia/Seoul"));
}
