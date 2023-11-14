package com.wanted.jaringoby.session.applications;

import com.wanted.jaringoby.session.dtos.LoginRequestDto;
import com.wanted.jaringoby.session.dtos.LoginResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginService {

    @Transactional
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        return null;
    }
}
