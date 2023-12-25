package com.Prince.TeaManagmentSystem.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Prince.TeaManagmentSystem.response.VendorResponse;
import com.Prince.TeaManagmentSystem.service.VendorService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class VendorController {

	private final VendorService vendorService;

	@PostMapping("/savevendor")
	public ResponseEntity<VendorResponse> createVendor(HttpServletRequest request) throws Exception {

		return new ResponseEntity<>(vendorService.createVendor(request), HttpStatus.OK);
	}

	

}
