package com.Prince.TeaManagmentSystem.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Prince.TeaManagmentSystem.response.VendorResponse;
import com.Prince.TeaManagmentSystem.service.VendorService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
public class VendorController {

	private final VendorService vendorService;

	@PostMapping("/savevendor")
	public ResponseEntity<VendorResponse> createVendor(HttpServletRequest request) throws Exception {

		return new ResponseEntity<>(vendorService.createVendor(request), HttpStatus.OK);
	}

//	@PostMapping("/savevendor")
//	public ResponseEntity<VendorResponse> createVendor(@RequestBody VendorRequest vendorRequest) throws Exception {
//
//		System.err.println("VendorName :::::::::::: -----" + vendorRequest.getVendorName());
//		System.err.println("VendorNumber :::::::::::: -----" + vendorRequest.getVendorNumber());
//
//		return new ResponseEntity<>(vendorService.createVendor(vendorRequest), HttpStatus.OK);
//	}

}
