package com.wanted.jaringoby.category.dtos;

import java.util.List;
import lombok.Builder;

@Builder
public record GetCategoryListResponseDto(
        List<String> categories
) {

}
