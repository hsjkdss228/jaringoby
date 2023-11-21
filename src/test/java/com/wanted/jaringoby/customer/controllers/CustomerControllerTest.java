package com.wanted.jaringoby.customer.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wanted.jaringoby.common.config.jwt.JwtConfig;
import com.wanted.jaringoby.common.config.security.SecurityConfig;
import com.wanted.jaringoby.common.config.validation.ValidationConfig;
import com.wanted.jaringoby.common.validations.BindingResultChecker;
import com.wanted.jaringoby.customer.applications.CreateCustomerService;
import com.wanted.jaringoby.customer.dtos.CreateCustomerRequestDto;
import com.wanted.jaringoby.customer.dtos.CreateCustomerResponseDto;
import com.wanted.jaringoby.customer.repositories.CustomerRepository;
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

@WebMvcTest(CustomerController.class)
@Import({SecurityConfig.class, JwtConfig.class, ValidationConfig.class})
@MockBean(CustomerRepository.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateCustomerService createCustomerService;

    @SpyBean
    private BindingResultChecker bindingResultChecker;

    @DisplayName("POST /customer/v1.0/customers")
    @Nested
    class PostCustomers {

        private static final String CUSTOMER_ID = "CUSTOMER_ID";

        @DisplayName("성공")
        @Nested
        class Success {

            @DisplayName("생성된 Customer 식별자를 응답으로 반환")
            @Test
            void create() throws Exception {
                CreateCustomerResponseDto createCustomerResponseDto = CreateCustomerResponseDto
                        .builder()
                        .customerId(CUSTOMER_ID)
                        .build();

                given(createCustomerService.createCustomer(any(CreateCustomerRequestDto.class)))
                        .willReturn(createCustomerResponseDto);

                mockMvc.perform(post("/customer/v1.0/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "hsjkdss228",
                                            "password": "Password!1",
                                            "reconfirmPassword": "Password!1"
                                        }
                                        """))
                        .andExpect(status().isCreated());
            }
        }

        @DisplayName("실패")
        @Nested
        class Failure {

            private void performAndExpectIsBadRequest(String content) throws Exception {
                mockMvc.perform(post("/customer/v1.0/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(
                                        content
                                ))
                        .andExpect(status().isBadRequest());

                verify(createCustomerService, never())
                        .createCustomer(any(CreateCustomerRequestDto.class));
            }

            @DisplayName("username 미입력된 경우 예외처리")
            @Test
            void blankUsername() throws Exception {
                performAndExpectIsBadRequest("""
                        {
                            "password": "Password!1",
                            "reconfirmPassword": "Password!1"
                        }
                        """);
            }

            @DisplayName("password 미입력된 경우 예외처리")
            @Test
            void blankPassword() throws Exception {
                performAndExpectIsBadRequest("""
                        {
                            "username": "hsjkdss228",
                            "password": null,
                            "reconfirmPassword": "Password!1"
                        }
                        """);
            }

            @DisplayName("reconfirmPassword 미입력된 경우 예외처리")
            @Test
            void blankReconfirmPassword() throws Exception {
                performAndExpectIsBadRequest("""
                        {
                            "username": "hsjkdss228",
                            "password": "Password!1",
                            "reconfirmPassword": ""
                        }
                        """);
            }

            @DisplayName("username 입력 조건에 부합하지 않을 경우 예외처리 (4자 미만)")
            @Test
            void invalidUsernameUnder4() throws Exception {
                performAndExpectIsBadRequest("""
                        {
                            "username": "ua7",
                            "password": "Password!1",
                            "reconfirmPassword": "Password!1"
                        }
                        """);
            }

            @DisplayName("username 입력 조건에 부합하지 않을 경우 예외처리 (16자 초과)")
            @Test
            void invalidUsernameOver16() throws Exception {
                performAndExpectIsBadRequest("""
                        {
                            "username": "username12username12",
                            "password": "Password!1",
                            "reconfirmPassword": "Password!1"
                        }
                        """);
            }

            @DisplayName("username 입력 조건에 부합하지 않을 경우 예외처리 (영어 소문자 미포함)")
            @Test
            void invalidUsernameWithoutLowercase() throws Exception {
                performAndExpectIsBadRequest("""
                        {
                            "username": "11223344",
                            "password": "Password!1",
                            "reconfirmPassword": "Password!1"
                        }
                        """);
            }

            @DisplayName("username 입력 조건에 부합하지 않을 경우 예외처리 (영어 소문자, 숫자 이외 문자 포함)")
            @Test
            void invalidUsernameWithDisallowedCharacters() throws Exception {
                performAndExpectIsBadRequest("""
                        {
                            "username": "hsjkdss228%",
                            "password": "Password!1",
                            "reconfirmPassword": "Password!1"
                        }
                        """);
            }

            @DisplayName("password 입력 조건에 부합하지 않을 경우 예외처리 (영어 대문자 미포함)")
            @Test
            void invalidPasswordWithoutUppercase() throws Exception {
                performAndExpectIsBadRequest("""
                        {
                            "username": "hsjkdss228",
                            "password": "assword!1",
                            "reconfirmPassword": "assword!1"
                        }
                        """);
            }

            @DisplayName("password 입력 조건에 부합하지 않을 경우 예외처리 (영어 소문자 미포함)")
            @Test
            void invalidPasswordWithoutLowercase() throws Exception {
                performAndExpectIsBadRequest("""
                        {
                            "username": "hsjkdss228",
                            "password": "PASSWORD!1",
                            "reconfirmPassword": "PASSWORD!1"
                        }
                        """);
            }

            @DisplayName("password 입력 조건에 부합하지 않을 경우 예외처리 (영어 숫자 미포함)")
            @Test
            void invalidPasswordWithoutDigit() throws Exception {
                performAndExpectIsBadRequest("""
                        {
                            "username": "hsjkdss228",
                            "password": "Password!",
                            "reconfirmPassword": "Password!"
                        }
                        """);
            }

            @DisplayName("password 입력 조건에 부합하지 않을 경우 예외처리 (키보드 입력 가능한 특수문자 미포함)")
            @Test
            void invalidPasswordWithoutSpecialCharacters() throws Exception {
                performAndExpectIsBadRequest("""
                        {
                            "username": "hsjkdss228",
                            "password": "Password1",
                            "reconfirmPassword": "Password1"
                        }
                        """);
            }

            @DisplayName("password 입력 조건에 부합하지 않을 경우 예외처리 "
                    + "(영어 대문자, 소문자, 숫자, 키보드 입력 가능한 특수문자 이외 문자 포함)")
            @Test
            void invalidPasswordWithDisallowedCharacters() throws Exception {
                performAndExpectIsBadRequest("""
                        {
                            "username": "hsjkdss228",
                            "password": "Password!1※",
                            "reconfirmPassword": "Password!1※"
                        }
                        """);
            }

            @DisplayName("password 입력 조건에 부합하지 않을 경우 예외처리 (5자 미만)")
            @Test
            void invalidPasswordUnder5() throws Exception {
                performAndExpectIsBadRequest("""
                        {
                            "username": "hsjkdss228",
                            "password": "Pa!1",
                            "reconfirmPassword": "Pa!1"
                        }
                        """);
            }
        }
    }
}
