package com.wanted.jaringoby.domains.customer.dtos.http;

import com.wanted.jaringoby.common.validations.groups.MissingValueGroup;
import com.wanted.jaringoby.common.validations.groups.PatternMatchesGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Getter
public class CreateCustomerRequestDto {

    @NotBlank(groups = MissingValueGroup.class)
    @Pattern(
            groups = PatternMatchesGroup.class,
            regexp = "^(?=.*[a-z])[a-z\\d]{4,16}$")
    private String username;

    @NotBlank(groups = MissingValueGroup.class)
    @Pattern(
            groups = PatternMatchesGroup.class,
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[~`!@#$%^&*()\\-_=+\\[{\\]};:'\",<.>/?\\\\|])[A-Za-z\\d~`!@#$%^&*()\\-_=+\\[{\\]};:'\",<.>/?\\\\|]{5,}$"
    )
    private String password;

    @NotBlank(groups = MissingValueGroup.class)
    private String reconfirmPassword;
}
