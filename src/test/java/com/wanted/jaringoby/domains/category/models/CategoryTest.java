package com.wanted.jaringoby.domains.category.models;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.wanted.jaringoby.domains.category.exceptions.CategoryNotFoundException;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CategoryTest {

    @DisplayName("toCategories")
    @Nested
    class ToCategories {

        @DisplayName("성공")
        @Nested
        class Success {

            @DisplayName("카테고리 이름 목록을 카테고리 목록으로 변환")
            @Test
            void toCategories() {
                List<String> categoryNames = List.of(
                        Category.Living.categoryName(),
                        Category.Meal.categoryName(),
                        Category.PersonalDevelopment.categoryName()
                );

                List<Category> categories = Category.toCategories(categoryNames);

                assertThat(categories).containsAll(List.of(
                        Category.Living,
                        Category.Meal,
                        Category.PersonalDevelopment
                ));
            }
        }

        @DisplayName("실패")
        @Nested
        class Failure {

            @DisplayName("존재하지 않는 카테고리 이름이 포함된 경우 예외처리")
            @Test
            void toCategories() {
                List<String> categoryNames = List.of(
                        Category.Living.categoryName(),
                        Category.Meal.categoryName(),
                        "유흥비"
                );

                assertThrows(CategoryNotFoundException.class,
                        () -> Category.toCategories(categoryNames));
            }
        }
    }
}
