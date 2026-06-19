package com.devsuperior.dscommerce.controllers.handlers;

import com.devsuperior.dscommerce.services.exceptions.ForbiddenException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ControllerExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, Object>> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", Instant.now());
		body.put("status", HttpStatus.NOT_FOUND.value());
		body.put("error", e.getMessage());
		body.put("path", request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
	}

	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<Map<String, Object>> forbidden(ForbiddenException e, HttpServletRequest request) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", Instant.now());
		body.put("status", HttpStatus.FORBIDDEN.value());
		body.put("error", e.getMessage());
		body.put("path", request.getRequestURI());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
		Map<String, Object> body = new HashMap<>();
		body.put("timestamp", Instant.now());
		body.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
		body.put("error", "Dados inválidos");
		body.put("path", request.getRequestURI());
		Map<String, String> errors = new HashMap<>();
		for (FieldError f : e.getBindingResult().getFieldErrors()) {
			errors.put(f.getField(), f.getDefaultMessage());
		}
		body.put("errors", errors);
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
	}
}
