package com.wanted.jaringoby.session.dtos.http;

import com.wanted.jaringoby.common.validations.groups.MissingValueGroup;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Getter
public class LoginRequestDto {

    @NotBlank(groups = MissingValueGroup.class)
    private String username;

    @NotBlank(groups = MissingValueGroup.class)
    private String password;
}
