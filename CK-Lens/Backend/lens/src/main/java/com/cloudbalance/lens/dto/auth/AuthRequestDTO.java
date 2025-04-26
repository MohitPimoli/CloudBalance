package com.cloudbalance.lens.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthRequestDTO {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}
