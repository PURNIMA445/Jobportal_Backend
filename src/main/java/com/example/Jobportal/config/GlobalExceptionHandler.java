package com.example.Jobportal.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;
import java.io.PrintWriter;
import java.io.StringWriter;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> handleAllExceptions(Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String stackTrace = sw.toString();
        
        try {
            String msg = "\n=== GLOBAL EXCEPTION CAUGHT ===\n" + stackTrace + "\n=================================\n";
            java.nio.file.Files.write(
                java.nio.file.Paths.get("debug.txt"), 
                msg.getBytes(), 
                java.nio.file.StandardOpenOption.CREATE, 
                java.nio.file.StandardOpenOption.APPEND
            );
        } catch (Exception ignored) {}

        return ResponseEntity.status(500).body(Map.of(
            "error", "Internal Server Error",
            "message", ex.getMessage() != null ? ex.getMessage() : ex.getClass().getName(),
            "trace", stackTrace
        ));
    }
}