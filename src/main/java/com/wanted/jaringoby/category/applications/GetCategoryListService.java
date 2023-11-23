package com.wanted.jaringoby.category.applications;

import com.wanted.jaringoby.category.dtos.GetCategoryListResponseDto;
import com.wanted.jaringoby.category.models.Category;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GetCategoryListService {

    public GetCategoryListResponseDto getCategories() {
        List<String> categories = Arrays.stream(Category.values())
                .map(Category::categoryName)
                .toList();

        return GetCategoryListResponseDto.builder()
                .categories(categories)
                .build();
    }
}
