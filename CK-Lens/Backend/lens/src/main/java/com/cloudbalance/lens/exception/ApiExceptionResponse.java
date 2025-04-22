package com.cloudbalance.lens.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ApiExceptionResponse {
    private String message;
    private int status;
    private LocalDateTime timestamp;
    private String path;
}
