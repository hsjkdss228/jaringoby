package com.wanted.jaringoby.session.applications;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LogoutService {

    public void logout(String customerId, String refreshToken) {

    }
}
