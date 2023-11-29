package com.Prince.TeaManagmentSystem.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Prince.TeaManagmentSystem.entity.Vendor;
import com.Prince.TeaManagmentSystem.request.PagedRequest;
import com.Prince.TeaManagmentSystem.response.TeaResponse;
import com.Prince.TeaManagmentSystem.response.VendorResponse;
import com.Prince.TeaManagmentSystem.service.TeaService;
import com.Prince.TeaManagmentSystem.service.VendorService;
import com.itextpdf.text.DocumentException;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final VendorService vendorService;

	private final TeaService teaService;

	@Value("${root.url}")
	public String url;

	@GetMapping("/downloadPdf")
	public ResponseEntity<String> downloadPDFOfListTeas(@RequestParam String vendorName)
			throws DocumentException, IOException {

		System.err.println("vendor name::" + vendorName);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "inline; filename=example.pdf");

		return null;
	}

	@GetMapping("/previous")
	public String masterHomePageableViewForPrevious(Model model, @RequestParam(defaultValue = "0") int page,
			@ModelAttribute PagedRequest pagedRequest) {
		String startDate = pagedRequest.getStartDate();
		String endDate = pagedRequest.getEndDate();
		Long vendorId = pagedRequest.getVendorId();

		model.addAttribute("sDatePreview", startDate);
		model.addAttribute("eDatePreview", endDate);
		model.addAttribute("vIdPreview", vendorId);

		return "redirect:/getinventory";
	}

	@GetMapping("/")
	public String masterHomePageableView(Model model, @RequestParam(defaultValue = "1") int page,
			@ModelAttribute PagedRequest pagedRequest) {

		System.err.println("url ::" + url);

		Page<TeaResponse> listVendors = teaService.getAllTeasPage(PageRequest.of(page, 5), pagedRequest);

		model.addAttribute("url", url);
		model.addAttribute("products", listVendors);
		model.addAttribute("currentPage", page);

		List<Vendor> listVendorNames = vendorService.getAllVendors();
		model.addAttribute("listVendorNames", listVendorNames);
		return "MasterHomePageable";
	}

	@PostMapping("/")
	public String masterHomePageable(Model model, @ModelAttribute PagedRequest pagedRequest) {

		String startDate = pagedRequest.getStartDate();
		String endDate = pagedRequest.getEndDate();
		Long vendorId = pagedRequest.getVendorId();

		Page<TeaResponse> listVendors = teaService
				.findPaginatedForPageResponse(PageRequest.of(pagedRequest.getPage(), 5), pagedRequest);

		List<TeaResponse> contents = listVendors.getContent();
		String vName = null;
		for (TeaResponse t : contents) {
			vName = t.getVendor().getVName();
		}

		model.addAttribute("sDate", startDate);
		model.addAttribute("eDate", endDate);
		model.addAttribute("vId", vendorId);
		model.addAttribute("vName", vName);

		model.addAttribute("products", listVendors);
		model.addAttribute("currentPage", pagedRequest.getPage());

		List<Vendor> listVendorNames = vendorService.getAllVendors();
		model.addAttribute("listVendorNames", listVendorNames);
		return "MasterHomePageable";
	}

	@GetMapping("/savevendor")
	public String getSaveVendor(Model model, @RequestParam(defaultValue = "0") int page) {
		Page<VendorResponse> paginatedForListVendors = vendorService.paginatedForListVendors(PageRequest.of(page, 2));

		model.addAttribute("products", paginatedForListVendors);
		model.addAttribute("currentPage", page);
		return "home-vendor";
	}

	@GetMapping("/savetea")
	public String getSaveTea(Model model) {
		List<Vendor> listVendors = vendorService.getAllVendors();
		model.addAttribute("listVendors", listVendors);
		return "home-tea";
	}

	@GetMapping("/homevendor")
	public String saveVendor(Model model) {
		return "home-vendor";
	}

	@GetMapping("/hometea")
	public String savetea(Model model) {
		List<Vendor> listVendors = vendorService.getAllVendors();
		model.addAttribute("listVendors", listVendors);
		return "home-tea";
	}

	@GetMapping("/filterpage")
	public String filterPage(Model model) {
		List<Vendor> listVendorNames = vendorService.getAllVendors();
		model.addAttribute("listVendorNames", listVendorNames);
		return "filter-page";
	}

	@PostMapping("/filterdate")
	public String getAllTeasByDate(Model model, @RequestParam String startDate, @RequestParam String endDate) {
		List<TeaResponse> listTeasByDate = teaService.getAllTeasByDate(startDate, endDate);

		Double grandTotalPrice = listTeasByDate.stream().mapToDouble(q -> q.getTotalPrice()).sum();

		Long totalQty = listTeasByDate.stream().mapToLong(a -> a.getTotalDaysQty()).sum();

		model.addAttribute("totalQty", totalQty);

		model.addAttribute("grandTotalPrice", grandTotalPrice);

		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);

		model.addAttribute("listTeasByDate", listTeasByDate);
		return "welcome-date";
	}

	@PostMapping("/filtervendor")
	public String getAllTeasByVendor(Model model, @RequestParam Long vendorName,
			@RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {

		final int currentPage = page.orElse(1);
		final int pageSize = size.orElse(5);

		Page<TeaResponse> listTeasByVendorName = teaService
				.getAllTeasByVendorNamePagination(PageRequest.of(currentPage - 1, pageSize), vendorName);

		model.addAttribute("vendorName", vendorName);

		model.addAttribute("listTeasByVendorName", listTeasByVendorName);

		int totalPages = listTeasByVendorName.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			model.addAttribute("pageNumbers", pageNumbers);
		}
		return "welcome-vendorName-pagination";
	}

	@GetMapping("/filtervendor")
	public String getAllTeasByVendorPage(Model model, @RequestParam Long vendorName) {

		return "welcome-vendorName-pagination";
	}

	/*
	 * @GetMapping("/test") public String view(Model model) { List<TeaResponse>
	 * listVendors = teaService.convertListTeasToTeaResponses();
	 * model.addAttribute("listVendors", listVendors);
	 * 
	 * List<Vendor> listVendorNames = vendorService.getAllVendors();
	 * model.addAttribute("listVendorNames", listVendorNames); return "MasterHome";
	 * }
	 */

}
