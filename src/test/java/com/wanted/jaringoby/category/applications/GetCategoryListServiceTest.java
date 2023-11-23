package com.wanted.jaringoby.category.applications;

import static org.assertj.core.api.Assertions.assertThat;

import com.wanted.jaringoby.category.dtos.GetCategoryListResponseDto;
import com.wanted.jaringoby.category.models.Category;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GetCategoryListServiceTest {

    private GetCategoryListService getCategoryListService;

    @BeforeEach
    void setUp() {
        getCategoryListService = new GetCategoryListService();
    }

    @Test
    void getCategories() {
        GetCategoryListResponseDto getCategoryListResponseDto = getCategoryListService
                .getCategories();

        assertThat(getCategoryListResponseDto).isNotNull();

        Category[] categories = Category.values();
        List<String> categoryNames = getCategoryListResponseDto.categories();
        Arrays.stream(categories)
                .forEach(category ->
                        assertThat(categoryNames).contains(category.categoryName()));
    }
}
