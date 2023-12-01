package com.wanted.jaringoby.domains.ledger.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wanted.jaringoby.common.config.jwt.JwtConfig;
import com.wanted.jaringoby.common.config.security.SecurityConfig;
import com.wanted.jaringoby.common.config.validation.ValidationConfig;
import com.wanted.jaringoby.common.utils.JwtUtil;
import com.wanted.jaringoby.common.validations.BindingResultChecker;
import com.wanted.jaringoby.domains.customer.models.customer.CustomerId;
import com.wanted.jaringoby.domains.customer.repositories.CustomerRepository;
import com.wanted.jaringoby.domains.ledger.applications.CreateLedgerService;
import com.wanted.jaringoby.domains.ledger.applications.GetOngoingLedgerService;
import com.wanted.jaringoby.domains.ledger.applications.ModifyLedgerBudgetsService;
import com.wanted.jaringoby.domains.ledger.applications.ModifyLedgerPeriodService;
import com.wanted.jaringoby.domains.ledger.controllers.LedgerController;
import com.wanted.jaringoby.domains.ledger.dtos.CreateLedgerRequestDto;
import com.wanted.jaringoby.domains.ledger.dtos.CreateLedgerResponseDto;
import com.wanted.jaringoby.domains.ledger.dtos.GetBudgetResponseDto;
import com.wanted.jaringoby.domains.ledger.dtos.GetLedgerDetailResponseDto;
import com.wanted.jaringoby.domains.ledger.dtos.ModifyLedgerBudgetsRequestDto;
import com.wanted.jaringoby.domains.ledger.dtos.ModifyLedgerPeriodRequestDto;
import java.time.LocalDate;
import java.util.List;
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
    private CustomerRepository customerRepository;

    @SpyBean
    private JwtUtil jwtUtil;

    @SpyBean
    private BindingResultChecker bindingResultChecker;

    private String accessToken;

    private static final String CUSTOMER_ID = "CUSTOMER_ID";

    @BeforeEach
    void setUp() {
        accessToken = jwtUtil.issueAccessToken(CustomerId.of(CUSTOMER_ID));

        given(customerRepository.existsById(CustomerId.of(CUSTOMER_ID)))
                .willReturn(true);
    }

    @MockBean
    private GetOngoingLedgerService getOngoingLedgerService;

    @DisplayName("GET /v1.0/customer/ledgers/now")
    @Nested
    class GetLedgersNow {

        private static final String LEDGER_ID = "LEDGER_ULID";
        private static final String BUDGET_ID_1 = "LEDGER_ULID_1";
        private static final String BUDGET_ID_2 = "LEDGER_ULID_2";

        @DisplayName("조회된 Ledger 정보 및 해당 Ledger와 연관된 모든 Budget 정보 응답 반환")
        @Test
        void now() throws Exception {
            List<GetBudgetResponseDto> budgetResponseDtos = List.of(
                    GetBudgetResponseDto.builder()
                            .id(BUDGET_ID_1)
                            .category("commute")
                            .amount(500_000L)
                            .build(),
                    GetBudgetResponseDto.builder()
                            .id(BUDGET_ID_2)
                            .category("books")
                            .amount(2_000_000L)
                            .build()
            );

            GetLedgerDetailResponseDto getLedgerDetailResponseDto
                    = GetLedgerDetailResponseDto.builder()
                    .id(LEDGER_ID)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusMonths(1))
                    .budgets(budgetResponseDtos)
                    .build();

            given(getOngoingLedgerService.getOngoingLedger(CUSTOMER_ID))
                    .willReturn(getLedgerDetailResponseDto);

            mockMvc.perform(get("/v1.0/customer/ledgers/now")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk());
        }
    }

    @MockBean
    private CreateLedgerService createLedgerService;

    @DisplayName("POST /v1.0/customer/ledgers")
    @Nested
    class PostLedgers {

        private static final String LEDGER_ID = "LEDGER_ID";

        private void performAndExpectBadRequest(String content) throws Exception {
            mockMvc.perform(post("/v1.0/customer/ledgers")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(
                                    content
                            ))
                    .andExpect(status().isBadRequest());

            verify(createLedgerService, never()).createLedger(
                    any(String.class),
                    any(CreateLedgerRequestDto.class)
            );
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

                mockMvc.perform(post("/v1.0/customer/ledgers")
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

            @DisplayName("시작일 입력되지 않은 경우 예외처리")
            @Test
            void nullStartDate() throws Exception {
                performAndExpectBadRequest("""
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
                performAndExpectBadRequest("""
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
                performAndExpectBadRequest("""
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
                performAndExpectBadRequest("""
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
                performAndExpectBadRequest("""
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
                performAndExpectBadRequest("""
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

    @MockBean
    private ModifyLedgerPeriodService modifyLedgerPeriodService;

    @DisplayName("PATCH /v1.0/customer/ledgers/{ledger-id}/period")
    @Nested
    class PatchLedgersPeriod {

        private static final String LEDGER_ID = "LEDGER_ID";

        private void performAndExpectBadRequest(String content) throws Exception {
            mockMvc.perform(patch("/v1.0/customer/ledgers/" + LEDGER_ID + "/period")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(
                                    content
                            ))
                    .andExpect(status().isBadRequest());

            verify(modifyLedgerPeriodService, never()).modifyLedgerPeriod(
                    any(String.class),
                    any(String.class),
                    any(ModifyLedgerPeriodRequestDto.class)
            );
        }

        @DisplayName("성공")
        @Nested
        class Success {

            @DisplayName("전달된 Ledger 식별자에 대해 시작일, 종료일 변경 수행 메서드 호출")
            @Test
            void modifyPeriod() throws Exception {
                mockMvc.perform(patch("/v1.0/customer/ledgers/" + LEDGER_ID + "/period")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "startDate": "2023-12-24",
                                            "endDate": "2024-01-24"
                                        }
                                        """))
                        .andExpect(status().isNoContent());

                verify(modifyLedgerPeriodService).modifyLedgerPeriod(
                        eq(CUSTOMER_ID),
                        eq(LEDGER_ID),
                        any(ModifyLedgerPeriodRequestDto.class)
                );
            }
        }

        @DisplayName("실패")
        @Nested
        class Failure {

            @DisplayName("시작일 입력되지 않은 경우 예외처리")
            @Test
            void nullStartDate() throws Exception {
                performAndExpectBadRequest("""
                        {
                            "endDate": "2023-12-15"
                        }
                        """);
            }

            @DisplayName("종료일 입력되지 않은 경우 예외처리")
            @Test
            void nullEndDate() throws Exception {
                performAndExpectBadRequest("""
                        {
                            "startDate": "2023-12-15",
                            "endDate": null
                        }
                        """);
            }
        }
    }

    @MockBean
    private ModifyLedgerBudgetsService modifyLedgerBudgetsService;

    @DisplayName("PATCH /v1.0/customer/ledgers/{ledger-id}/budgets")
    @Nested
    class PatchLedgersBudgets {

        private static final String LEDGER_ID = "LEDGER_ID";

        private void performAndExpectBadRequest(String content) throws Exception {
            mockMvc.perform(patch("/v1.0/customer/ledgers/" + LEDGER_ID + "/budgets")
                            .header("Authorization", "Bearer " + accessToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(
                                    content
                            ))
                    .andExpect(status().isBadRequest());

            verify(modifyLedgerBudgetsService, never()).modifyLedgerBudgets(
                    any(String.class),
                    any(String.class),
                    any(ModifyLedgerBudgetsRequestDto.class)
            );
        }

        @DisplayName("성공")
        @Nested
        class Success {

            @DisplayName("전달된 Ledger 식별자에 대해 예산 추가/변경/삭제 수행 메서드 호출")
            @Test
            void modifyBudgets() throws Exception {
                mockMvc.perform(patch("/v1.0/customer/ledgers/" + LEDGER_ID + "/budgets")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "budgets": [
                                                {
                                                    "category": "Leisures",
                                                    "amount": 1500000
                                                },
                                                {
                                                    "category": "Medications",
                                                    "amount": 500000
                                                }
                                            ]
                                        }
                                        """))
                        .andExpect(status().isNoContent());

                verify(modifyLedgerBudgetsService).modifyLedgerBudgets(
                        eq(CUSTOMER_ID),
                        eq(LEDGER_ID),
                        any(ModifyLedgerBudgetsRequestDto.class)
                );
            }
        }

        @DisplayName("실패")
        @Nested
        class Failure {

            @DisplayName("예산이 하나 이상 입력되지 않은 경우 예외처리")
            @Test
            void emptyBudgets() throws Exception {
                performAndExpectBadRequest("""
                        {
                             "budgets": []
                        }
                         """);
            }

            @DisplayName("카테고리가 입력되지 않은 예산이 존재하는 경우 예외처리")
            @Test
            void blankCategory() throws Exception {
                performAndExpectBadRequest("""
                        {
                             "budgets": [
                                {
                                    "amount": 500000
                                },
                                {
                                    "category": "Medications",
                                    "amount": 1000000
                                }
                             ]
                        }
                         """);
            }

            @DisplayName("금액이 입력되지 않은 예산이 존재하는 경우 예외처리")
            @Test
            void nullAmount() throws Exception {
                performAndExpectBadRequest("""
                        {
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
                performAndExpectBadRequest("""
                        {
                             "budgets": [
                                {
                                    "category": "Food",
                                    "amount": 500000
                                },
                                {
                                    "category": "Medications",
                                    "amount": 0
                                }
                             ]
                        }
                         """);
            }
        }
    }
}
