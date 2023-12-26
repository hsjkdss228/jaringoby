package com.wanted.jaringoby.session.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wanted.jaringoby.common.config.jwt.JwtConfig;
import com.wanted.jaringoby.common.config.security.SecurityConfig;
import com.wanted.jaringoby.common.utils.JwtUtil;
import com.wanted.jaringoby.domains.customer.entities.CustomerId;
import com.wanted.jaringoby.domains.customer.repositories.CustomerRepository;
import com.wanted.jaringoby.session.applications.ReissueAccessTokenService;
import com.wanted.jaringoby.session.dtos.ReissueAccessTokenResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AccessTokenController.class)
@Import({SecurityConfig.class, JwtConfig.class})
class AccessTokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReissueAccessTokenService reissueAccessTokenService;

    @MockBean
    private CustomerRepository customerRepository;

    @SpyBean
    private JwtUtil jwtUtil;

    @DisplayName("POST /v1.0/customer/access-tokens")
    @Nested
    class PostAccessTokens {

        private static final String CUSTOMER_ID = "CUSTOMER_ID";
        private static final String ACCESS_TOKEN = "ACCESS_TOKEN";

        @BeforeEach
        void setUp() {
            given(customerRepository.existsById(CustomerId.of(CUSTOMER_ID)))
                    .willReturn(true);
        }

        @DisplayName("성공")
        @Nested
        class Success {

            @DisplayName("리프레시 토큰을 전달하는 경우, 반환된 액세스 토큰을 응답으로 전달")
            @Test
            void reissueAccessToken() throws Exception {
                String refreshToken = jwtUtil.issueRefreshToken(CustomerId.of(CUSTOMER_ID));

                ReissueAccessTokenResponseDto reissueAccessTokenResponseDto
                        = ReissueAccessTokenResponseDto.builder()
                        .accessToken(ACCESS_TOKEN)
                        .build();

                given(reissueAccessTokenService
                        .reissueAccessToken(CUSTOMER_ID, refreshToken))
                        .willReturn(reissueAccessTokenResponseDto);

                mockMvc.perform(post("/v1.0/customer/access-tokens")
                                .header("Authorization", "Bearer " + refreshToken))
                        .andExpect(status().isCreated());
            }
        }

        @DisplayName("실패")
        @Nested
        class Failure {

            @DisplayName("리프레시 토큰이 아닌 토큰을 전달하는 경우, 리프레시 토큰이 아닌 예외 반환")
            @Test
            void customerRefreshTokenNull() throws Exception {
                String token = jwtUtil.issueAccessToken(CustomerId.of(CUSTOMER_ID));

                mockMvc.perform(post("/v1.0/customer/access-tokens")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isUnauthorized());

                verify(reissueAccessTokenService, never())
                        .reissueAccessToken(any(String.class), any(String.class));
            }
        }
    }
}
