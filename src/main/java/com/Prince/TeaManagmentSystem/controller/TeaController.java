package com.Prince.TeaManagmentSystem.controller;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Prince.TeaManagmentSystem.request.TestRequest;
import com.Prince.TeaManagmentSystem.response.BaseResponse;
import com.Prince.TeaManagmentSystem.service.TeaService;
import com.itextpdf.text.DocumentException;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
public class TeaController {

	private final TeaService teaService;

	@PostMapping("/savetea")
	public ResponseEntity<?> createTea(HttpServletRequest servletRequest) {

		return new ResponseEntity<>(teaService.saveTea(servletRequest), HttpStatus.OK);
	}
	
//	@PostMapping("/savetea")
//	public void createTea(@RequestBody TeaRequest teaRequest) {
//		System.err.println("TeaRequest :- "+ teaRequest);
//
//	}

	@PostMapping("/sendemail")
	public ResponseEntity<BaseResponse> sendEmailByDate(@ModelAttribute TestRequest testRequest)
			throws DocumentException, IOException {

		return ResponseEntity.ok(teaService.sendToEmailFileForDate(testRequest.getEmail(), testRequest.getStartDate(),
				testRequest.getEndDate(), testRequest.getVendorId()));
	}

	@PostMapping("/downloadPdf")
	public ResponseEntity<byte[]> downloadPDFOfListTeas(@ModelAttribute TestRequest testRequest)
			throws DocumentException, IOException {

		byte[] pdfContent = teaService.downloadToPdfByFilters(testRequest);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", "data.pdf");

		return ResponseEntity.ok().headers(headers).body(pdfContent);

	}

	@DeleteMapping("/removeTeas")
	public void deleteTeas() {
		teaService.allDeleteTeas();
	}

//	@PostMapping("/downloadPdf")
//	public ResponseEntity<InputStreamResource> downloadPDFOfListTeas(@ModelAttribute TestRequest testRequest)
//			throws DocumentException, IOException {
//
//		byte[] pdfContent = teaService.downloadToPdfByFilters(testRequest);
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Disposition", "inline; filename=example.pdf");
//
//		ByteArrayInputStream bis = new ByteArrayInputStream(pdfContent);
//		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_PDF)
//				.body(new InputStreamResource(bis));
//
//	}

}
