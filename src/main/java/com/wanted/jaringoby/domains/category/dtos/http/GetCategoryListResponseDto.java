package com.wanted.jaringoby.domains.category.dtos.http;

import java.util.List;
import lombok.Builder;

@Builder
public record GetCategoryListResponseDto(
        List<String> categories
) {

}
