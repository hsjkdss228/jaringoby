package com.wanted.jaringoby.ledger.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wanted.jaringoby.common.config.jwt.JwtConfig;
import com.wanted.jaringoby.common.config.security.SecurityConfig;
import com.wanted.jaringoby.common.config.validation.ValidationConfig;
import com.wanted.jaringoby.common.utils.JwtUtil;
import com.wanted.jaringoby.common.validations.BindingResultChecker;
import com.wanted.jaringoby.customer.models.customer.CustomerId;
import com.wanted.jaringoby.customer.repositories.CustomerRepository;
import com.wanted.jaringoby.ledger.applications.CreateLedgerService;
import com.wanted.jaringoby.ledger.dtos.CreateLedgerRequestDto;
import com.wanted.jaringoby.ledger.dtos.CreateLedgerResponseDto;
import org.junit.jupiter.api.BeforeEach;
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

@WebMvcTest(LedgerController.class)
@Import({SecurityConfig.class, JwtConfig.class, ValidationConfig.class})
class LedgerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateLedgerService createLedgerService;

    @MockBean
    private CustomerRepository customerRepository;

    @SpyBean
    private JwtUtil jwtUtil;

    @SpyBean
    private BindingResultChecker bindingResultChecker;

    @DisplayName("POST /customer/v1.0/ledgers")
    @Nested
    class PostLedgers {

        private static final String CUSTOMER_ID = "CUSTOMER_ID";
        private static final String LEDGER_ID = "LEDGER_ID";

        private String accessToken;

        @BeforeEach
        void setUp() {
            accessToken = jwtUtil.issueAccessToken(CustomerId.of(CUSTOMER_ID));

            given(customerRepository.existsById(CustomerId.of(CUSTOMER_ID)))
                    .willReturn(true);
        }

        @DisplayName("성공")
        @Nested
        class Success {

            @DisplayName("생성된 Ledger 식별자를 응답으로 반환")
            @Test
            void create() throws Exception {
                CreateLedgerResponseDto createLedgerResponseDto = CreateLedgerResponseDto
                        .builder()
                        .ledgerId(LEDGER_ID)
                        .build();

                given(createLedgerService
                        .createLedger(eq(CUSTOMER_ID), any(CreateLedgerRequestDto.class)))
                        .willReturn(createLedgerResponseDto);

                mockMvc.perform(post("/customer/v1.0/ledgers")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                             "startDate": "2023-11-24",
                                             "endDate": "2023-12-24",
                                             "budgets": [
                                                {
                                                    "category": "Food",
                                                    "amount": 500000
                                                },
                                                {
                                                    "category": "Commute",
                                                    "amount": 500000
                                                }
                                             ]
                                        }
                                         """))
                        .andExpect(status().isCreated());
            }
        }

        @DisplayName("실패")
        @Nested
        class Failure {

            private void performAndExpectIsBadRequest(String content) throws Exception {
                mockMvc.perform(post("/customer/v1.0/ledgers")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content(
                                        content
                                ))
                        .andExpect(status().isBadRequest());
            }

            @DisplayName("시작일 입력되지 않은 경우 예외처리")
            @Test
            void nullStartDate() throws Exception {
                performAndExpectIsBadRequest("""
                        {
                             "endDate": "2023-12-24",
                             "budgets": [
                                {
                                    "category": "Food",
                                    "amount": 500000
                                },
                                {
                                    "category": "Commute",
                                    "amount": 500000
                                }
                             ]
                        }
                         """);
            }

            @DisplayName("종료일 입력되지 않은 경우 예외처리")
            @Test
            void nullEndDate() throws Exception {
                performAndExpectIsBadRequest("""
                        {
                             "startDate": "2023-11-24",
                             "endDate": null,
                             "budgets": [
                                {
                                    "category": "Food",
                                    "amount": 500000
                                },
                                {
                                    "category": "Commute",
                                    "amount": 500000
                                }
                             ]
                        }
                         """);
            }

            @DisplayName("예산이 하나 이상 입력되지 않은 경우 예외처리")
            @Test
            void emptyBudgets() throws Exception {
                performAndExpectIsBadRequest("""
                        {
                             "startDate": "2023-11-24",
                             "endDate": "2023-12-23",
                             "budgets": []
                        }
                         """);
            }

            @DisplayName("카테고리가 입력되지 않은 예산이 존재하는 경우 예외처리")
            @Test
            void blankCategory() throws Exception {
                performAndExpectIsBadRequest("""
                        {
                             "startDate": "2023-11-24",
                             "endDate": "2023-12-23",
                             "budgets": [
                                {
                                    "category": "    ",
                                    "amount": 500000
                                },
                                {
                                    "category": "Medical",
                                    "amount": 1000000
                                }
                             ]
                        }
                         """);
            }

            @DisplayName("금액이 입력되지 않은 예산이 존재하는 경우 예외처리")
            @Test
            void nullAmount() throws Exception {
                performAndExpectIsBadRequest("""
                        {
                             "startDate": "2023-11-24",
                             "endDate": "2023-12-23",
                             "budgets": [
                                {
                                    "category": "Food",
                                    "amount": 500000
                                },
                                {
                                    "category": "Travel"
                                }
                             ]
                        }
                         """);
            }

            @DisplayName("입력된 예산의 금액 중 1원 이하가 존재하는 경우 예외처리")
            @Test
            void amountLessThan1() throws Exception {
                performAndExpectIsBadRequest("""
                        {
                             "startDate": "2023-11-24",
                             "endDate": "2023-12-23",
                             "budgets": [
                                {
                                    "category": "Food",
                                    "amount": 500000
                                },
                                {
                                    "category": "Medical",
                                    "amount": 0
                                }
                             ]
                        }
                         """);
            }
        }
    }
}
