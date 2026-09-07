package com.br.historico.service.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Map<String, Object> handleException(Exception ex) {
        log.error("Unexpected error", ex);
        
        Map<String, Object> error = new HashMap<>();
        error.put("message", ex.getMessage());
        error.put("code", "INTERNAL_SERVER_ERROR");
        error.put("timestamp", LocalDateTime.now());
        
        return error;
    }
}
