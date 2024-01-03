package com.Prince.TeaManagmentSystem.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.Prince.TeaManagmentSystem.entity.Vendor;
import com.Prince.TeaManagmentSystem.exception.TeaNotFoundException;
import com.Prince.TeaManagmentSystem.repository.VendorRepository;
import com.Prince.TeaManagmentSystem.request.VendorRequest;
import com.Prince.TeaManagmentSystem.response.VendorResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendorService {

	private final VendorRepository vendorRepository;

	public VendorResponse createVendor(HttpServletRequest request) throws Exception {
		String vendorName = request.getParameter("vendorName");
		String vendorNumber = request.getParameter("vendorNumber");

		System.err.println(":::::::::::: -----" + vendorName);
		VendorResponse response = new VendorResponse();
		if (vendorNumber != null) {
			Vendor findByVendorNumber = vendorRepository.findByNumber(vendorNumber);

			if (findByVendorNumber != null) {

				throw new TeaNotFoundException("Duplicate Vendor Mobile Number Not Allowed !!");
			}

		}

		VendorRequest vendorRequest = new VendorRequest();
		vendorRequest.setVendorName(vendorName);
		vendorRequest.setVendorNumber(vendorNumber);

		if (!vendorRequest.getVendorName().isEmpty()) {
			Vendor vendor = new Vendor();

			vendor.setVName(vendorRequest.getVendorName());
			vendor.setNumber(vendorRequest.getVendorNumber());
			Vendor saveVendor = vendorRepository.save(vendor);
			response.setMsg("Vendor Save Successfully !!!");
			return response;
		}
		response.setMsg("Duplicate Vendor Number Not Allowed !!");
		return response;

	}

	public Page<VendorResponse> paginatedForListVendors(Pageable pageable) {

		List<Vendor> listVendors = getAllVendors();

		List<VendorResponse> emptyListVendors = new ArrayList<>();

		listVendors.stream().forEach(t -> {
			VendorResponse response = new VendorResponse();
			response.setVendorName(t.getVName());
			response.setVendorNumber(t.getNumber());
			emptyListVendors.add(response);

		});

		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();

		int startItem = currentPage * pageSize;

		List<VendorResponse> list = new ArrayList<>();

		if (emptyListVendors.size() < startItem) {
			list = Collections.emptyList();
		} else {
			int toIndex = Math.min(startItem + pageSize, emptyListVendors.size());
			list = emptyListVendors.subList(startItem, toIndex);
		}

		PageImpl<VendorResponse> vendorsPage = new PageImpl<VendorResponse>(list, PageRequest.of(currentPage, pageSize),
				emptyListVendors.size());
		return vendorsPage;

	}

	public List<Vendor> getAllVendors() {
		return vendorRepository.findAll();
	}

	public boolean isValidMobileNo(String mobileNumber) {
		Pattern ptrn = Pattern.compile("^[6-9]\\d{9}$");

		Matcher match = ptrn.matcher(mobileNumber);

		return (match.find() && match.group().equals(mobileNumber));
	}

	public VendorResponse createVendor(VendorRequest vendorRequest) throws Exception {
//		String vendorName = request.getParameter("vendorName");
//		String vendorNumber = request.getParameter("vendorNumber");

		System.err.println(":::::::::::: -----" + vendorRequest.getVendorName());
		VendorResponse response = new VendorResponse();
		if (vendorRequest.getVendorNumber() != null) {
			Vendor findByVendorNumber = vendorRepository.findByNumber(vendorRequest.getVendorNumber());

			if (findByVendorNumber != null) {

				throw new TeaNotFoundException("Duplicate Vendor Mobile Number Not Allowed !!");
			}

		}

//		VendorRequest vendorRequest = new VendorRequest();
//		vendorRequest.setVendorName(vendorName);
//		vendorRequest.setVendorNumber(vendorNumber);

		if (!vendorRequest.getVendorName().isEmpty()) {
			Vendor vendor = new Vendor();

			vendor.setVName(vendorRequest.getVendorName());
			vendor.setNumber(vendorRequest.getVendorNumber());
			Vendor saveVendor = vendorRepository.save(vendor);
			response.setMsg("Vendor Save Successfully !!!");
			return response;
		}
		response.setMsg("Duplicate Vendor Number Not Allowed !!");
		return response;

	}

}
