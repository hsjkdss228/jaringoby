package com.wanted.jaringoby.session.applications;

import com.wanted.jaringoby.session.dtos.ReissueAccessTokenResultDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReissueAccessTokenService {

    @Transactional(readOnly = true)
    public ReissueAccessTokenResultDto reissueAccessToken(
            String customerId,
            String refreshToken
    ) {
        return null;
    }
}
