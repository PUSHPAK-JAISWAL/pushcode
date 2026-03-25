package com.pushcode.backend.execptions;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiError {

    private int status;
    private String message;
    private String path;
    private LocalDateTime timestamp;

}
