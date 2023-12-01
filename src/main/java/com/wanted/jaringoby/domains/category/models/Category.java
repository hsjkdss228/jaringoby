package com.wanted.jaringoby.domains.category.models;

import com.wanted.jaringoby.domains.category.exceptions.CategoryNotFoundException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum Category {
    Meal("식비"),
    Transportation("교통비"),
    Leisure("여가비"),
    Living("생활비"),
    PersonalDevelopment("자기계발비"),
    EtCetera("기타");

    private final String name;

    private static final Set<String> CATEGORY_NAMES = Arrays.stream(Category.values())
            .map(Category::categoryName)
            .collect(Collectors.toSet());

    public static Category of(String category) {
        return switch (category) {
            case "식비" -> Meal;
            case "교통비" -> Transportation;
            case "여가비" -> Leisure;
            case "생활비" -> Living;
            case "자기계발비" -> PersonalDevelopment;
            case "기타" -> EtCetera;
            default -> throw new CategoryNotFoundException();
        };
    }

    public String categoryName() {
        return name;
    }

    public static boolean contains(String category) {
        return CATEGORY_NAMES.contains(category);
    }
}
