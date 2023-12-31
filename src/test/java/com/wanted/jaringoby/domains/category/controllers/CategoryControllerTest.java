package com.wanted.jaringoby.domains.category.controllers;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wanted.jaringoby.common.config.jwt.JwtConfig;
import com.wanted.jaringoby.common.config.security.SecurityConfig;
import com.wanted.jaringoby.common.utils.JwtUtil;
import com.wanted.jaringoby.domains.category.applications.GetCategoryListService;
import com.wanted.jaringoby.domains.category.dtos.http.GetCategoryListResponseDto;
import com.wanted.jaringoby.domains.customer.entities.CustomerId;
import com.wanted.jaringoby.domains.customer.repositories.CustomerRepository;
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
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CategoryController.class)
@Import({SecurityConfig.class, JwtConfig.class})
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetCategoryListService getCategoryListService;

    @MockBean
    private CustomerRepository customerRepository;

    @SpyBean
    private JwtUtil jwtUtil;

    @DisplayName("GET /v1.0/customer/categories")
    @Nested
    class GetCategories {

        private static final CustomerId CUSTOMER_ID = CustomerId.of("CUSTOMER_ID");

        private String accessToken;

        @BeforeEach
        void setUp() {
            accessToken = jwtUtil.issueAccessToken(CUSTOMER_ID);
        }

        @DisplayName("카테고리 목록을 응답으로 반환")
        @Test
        void categories() throws Exception {
            given(customerRepository.existsById(CUSTOMER_ID))
                    .willReturn(true);

            GetCategoryListResponseDto getCategoryListResponseDto = GetCategoryListResponseDto
                    .builder()
                    .categories(List.of(
                            "Meal",
                            "Transportation",
                            "Leisure",
                            "Living",
                            "PersonalDevelopment"))
                    .build();

            given(getCategoryListService.getCategories()).willReturn(getCategoryListResponseDto);

            mockMvc.perform(get("/v1.0/customer/categories")
                            .header("Authorization", "Bearer " + accessToken))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("""
                            "categories":["Meal","Transportation","Leisure","Living","PersonalDevelopment"]""")));
        }
    }
}
