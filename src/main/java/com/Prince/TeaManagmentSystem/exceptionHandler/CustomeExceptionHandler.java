package com.Prince.TeaManagmentSystem.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.Prince.TeaManagmentSystem.exception.TeaNotFoundException;
import com.Prince.TeaManagmentSystem.response.ErrorResponse;

@RestControllerAdvice
public class CustomeExceptionHandler {

//	@ExceptionHandler(TeaNotFoundException.class)
//	public ResponseEntity<String> getErrorMsg(TeaNotFoundException tnfe) {
//
//		return new ResponseEntity<String>(tnfe.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//
//	}

	@ExceptionHandler(TeaNotFoundException.class)
	public ResponseEntity<ErrorResponse> getErrorMsg(TeaNotFoundException tnfe) {

		return new ResponseEntity<ErrorResponse>(new ErrorResponse(tnfe.getMessage()),
				HttpStatus.INTERNAL_SERVER_ERROR);

	}

}
