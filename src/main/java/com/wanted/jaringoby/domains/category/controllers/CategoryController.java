package com.wanted.jaringoby.domains.category.controllers;

import com.wanted.jaringoby.common.response.Response;
import com.wanted.jaringoby.domains.category.applications.GetCategoryListService;
import com.wanted.jaringoby.domains.category.dtos.http.GetCategoryListResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1.0/customer/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final GetCategoryListService getCategoryListService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Response<GetCategoryListResponseDto> categories() {
        return Response.of(getCategoryListService.getCategories());
    }
}
