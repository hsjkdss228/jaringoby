package com.wanted.jaringoby.session.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wanted.jaringoby.common.config.jwt.JwtConfig;
import com.wanted.jaringoby.common.config.security.SecurityConfig;
import com.wanted.jaringoby.common.config.validation.ValidationConfig;
import com.wanted.jaringoby.common.utils.JwtUtil;
import com.wanted.jaringoby.common.validations.BindingResultChecker;
import com.wanted.jaringoby.customer.models.customer.CustomerId;
import com.wanted.jaringoby.customer.repositories.CustomerRepository;
import com.wanted.jaringoby.session.applications.LoginService;
import com.wanted.jaringoby.session.applications.LogoutService;
import com.wanted.jaringoby.session.dtos.LoginRequestDto;
import com.wanted.jaringoby.session.dtos.LoginResponseDto;
import com.wanted.jaringoby.session.exceptions.CustomerRefreshTokenIsNullException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(SessionController.class)
@Import({SecurityConfig.class, JwtConfig.class, ValidationConfig.class})
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoginService loginService;

    @SpyBean
    private BindingResultChecker bindingResultChecker;

    @DisplayName("POST /customer/v1.0/sessions")
    @Nested
    class PostSessions {

        @DisplayName("성공")
        @Nested
        class Success {

            private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
            private static final String REFRESH_TOKEN = "REFRESH_TOKEN";

            @DisplayName("생성된 accessToken, refreshToken 식별자를 응답으로 반환")
            @Test
            void create() throws Exception {
                LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                        .accessToken(ACCESS_TOKEN)
                        .refreshToken(REFRESH_TOKEN)
                        .build();

                given(loginService.login(any(LoginRequestDto.class)))
                        .willReturn(loginResponseDto);

                mockMvc.perform(post("/customer/v1.0/sessions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "hsjkdss228",
                                            "password": "Password!1"
                                        }
                                        """))
                        .andExpect(status().isCreated());
            }
        }

        @DisplayName("실패")
        @Nested
        class Failure {

            private void performAndExpectIsBadRequest(String content) throws Exception {
                mockMvc.perform(post("/customer/v1.0/sessions")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(
                                        content
                                ))
                        .andExpect(status().isBadRequest());

                verify(loginService, never())
                        .login(any(LoginRequestDto.class));
            }

            @DisplayName("username 미입력된 경우 예외처리")
            @Test
            void blankUsername() throws Exception {
                performAndExpectIsBadRequest("""
                        {
                            "username": "  ",
                            "password": "Password!1"
                        }
                        """);
            }

            @DisplayName("password 미입력된 경우 예외처리")
            @Test
            void blankPassword() throws Exception {
                performAndExpectIsBadRequest("""
                        {
                            "username": "hsjkdss228"
                        }
                        """);
            }
        }
    }

    @MockBean
    private LogoutService logoutService;

    @MockBean
    private CustomerRepository customerRepository;

    @SpyBean
    private JwtUtil jwtUtil;

    private static final String CUSTOMER_ID = "CUSTOMER_ID";

    @DisplayName("DELETE /customer/v1.0/sessions")
    @Nested
    class DeleteSessions {

        private String token;

        @DisplayName("성공")
        @Nested
        class Success {

            @DisplayName("리프레시 토큰을 전달하는 경우, LogoutService 메서드 호출")
            @Test
            void logout() throws Exception {
                token = jwtUtil.issueRefreshToken(CustomerId.of(CUSTOMER_ID));

                given(customerRepository.existsById(CustomerId.of(CUSTOMER_ID)))
                        .willReturn(true);

                mockMvc.perform(delete("/customer/v1.0/sessions")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isNoContent());

                verify(logoutService).logout(CUSTOMER_ID, token);
            }
        }

        @DisplayName("실패")
        @Nested
        class Failure {

            @DisplayName("리프레시 토큰이 아닌 토큰을 전달하는 경우, null refreshToken 전달하고 "
                    + "리프레시 토큰 미존재 예외 반환")
            @Test
            void logout() throws Exception {
                token = jwtUtil.issueAccessToken(CustomerId.of(CUSTOMER_ID));

                given(customerRepository.existsById(CustomerId.of(CUSTOMER_ID)))
                        .willReturn(true);

                doThrow(new CustomerRefreshTokenIsNullException())
                        .when(logoutService)
                        .logout(CUSTOMER_ID, null);

                mockMvc.perform(delete("/customer/v1.0/sessions")
                                .header("Authorization", "Bearer " + token))
                        .andExpect(status().isBadRequest());
            }
        }
    }
}
