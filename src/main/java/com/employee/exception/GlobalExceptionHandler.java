package com.employee.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
		logger.warn("Resource not found: {}", ex.getMessage());
		Map<String, Object> response = new HashMap<>();
		response.put("message", ex.getMessage());
		response.put("timestamp", LocalDateTime.now());
		response.put("status", HttpStatus.NOT_FOUND.value());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
		logger.warn("Validation error: {}", ex.getMessage());
		Map<String, Object> response = new HashMap<>();
		response.put("message", ex.getMessage());
		response.put("timestamp", LocalDateTime.now());
		response.put("status", HttpStatus.BAD_REQUEST.value());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
		// Log the full exception with stack trace to console
		logger.error("❌ ERROR occurred in API: {}", ex.getMessage(), ex);
		System.err.println("❌ ERROR: " + ex.getMessage());
		ex.printStackTrace();
		
		Map<String, Object> response = new HashMap<>();
		response.put("message", ex.getMessage() != null ? ex.getMessage() : "Internal server error");
		response.put("timestamp", LocalDateTime.now());
		response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
		
		// Include exception class name and cause for debugging
		if (ex.getCause() != null) {
			response.put("cause", ex.getCause().getMessage());
		}
		response.put("exception", ex.getClass().getSimpleName());
		
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

