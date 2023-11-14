package com.wanted.jaringoby.session.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wanted.jaringoby.common.config.validation.ValidationConfig;
import com.wanted.jaringoby.common.validations.BindingResultChecker;
import com.wanted.jaringoby.session.applications.LoginService;
import com.wanted.jaringoby.session.dtos.LoginRequestDto;
import com.wanted.jaringoby.session.dtos.LoginResponseDto;
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
@Import(ValidationConfig.class)
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
}
