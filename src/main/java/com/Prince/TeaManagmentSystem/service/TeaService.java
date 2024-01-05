package com.Prince.TeaManagmentSystem.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.Prince.TeaManagmentSystem.config.EmailConfig;
import com.Prince.TeaManagmentSystem.entity.Tea;
import com.Prince.TeaManagmentSystem.entity.TimeEnum;
import com.Prince.TeaManagmentSystem.entity.Vendor;
import com.Prince.TeaManagmentSystem.repository.TeaRepository;
import com.Prince.TeaManagmentSystem.request.PagedRequest;
import com.Prince.TeaManagmentSystem.request.TeaRequest;
import com.Prince.TeaManagmentSystem.request.TestRequest;
import com.Prince.TeaManagmentSystem.response.BaseResponse;
import com.Prince.TeaManagmentSystem.response.TeaResponse;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeaService {

	private final TeaRepository teaRepository;

	private final EmailConfig emailConfig;

	private double fixed_price;

	private Tea save(Tea tea) {
		return teaRepository.save(tea);
	}

	public void allDeleteTeas() {
		deleteTeas();
	}

	private void deleteTeas() {
		teaRepository.deleteAll();

	}

	public Page<TeaResponse> getAllTeasPage(Pageable pageable, PagedRequest pagedRequest) {
		List<Tea> response = new ArrayList<>();

		if (pagedRequest.getVendorId() != null && pagedRequest.getStartDate().equals("")
				&& pagedRequest.getEndDate().equals("")) {
			response = getAllTeasByVendorIdForPagedRequest(pagedRequest);

		} else if (pagedRequest.getStartDate() != null && pagedRequest.getEndDate() != null) {
			response = getAllTeasByDateForPagedRequest(pagedRequest);
		} else {
			response = getAllTeasOrderedByIdDesc();

		}

		return findPaginatedForVendorName(pageable, response);
	}

	private List<Tea> getAllTeasByDateForPagedRequest(PagedRequest pagedRequest) {
		List<Tea> responses = new ArrayList<>();
		if (pagedRequest.getStartDate() != null && pagedRequest.getEndDate() != null) {

			responses = teaRepository.findByDate(pagedRequest.getStartDate(), pagedRequest.getEndDate());
			return responses;

		}
		return null;

	}

	private List<Tea> getAllTeasByVendorIdForPagedRequest(PagedRequest pagedRequest) {
		List<Tea> responses = new ArrayList<>();

		if (pagedRequest.getVendorId() != null) {

			responses = getAllTeasByVendorId(pagedRequest.getVendorId());
			return responses;
		}
		return null;

	}

	private List<Tea> getAllTeas(PagedRequest pagedRequest) {
		List<Tea> findAll = new ArrayList<>();
		if (pagedRequest.getPage() != null) {

			findAll = teaRepository.findAll();
		}
		return findAll;
	}

	private List<Tea> getAllTeasOrderedByIdDesc() {
		List<Tea> findAllOrderedByIdDesc = teaRepository.findAllOrderedByIdDesc();
		return findAllOrderedByIdDesc;
	}

	private List<Tea> getAllTeas() {
		List<Tea> findAll = teaRepository.findAll();
		return findAll;
	}

	public List<TeaResponse> convertListTeasToTeaResponses() {

		List<TeaResponse> findAllTeas = convertTeaToTeaResponseByDate(getAllTeas());

		List<TeaResponse> list = new ArrayList<>();

		return findAllTeas;
	}

	public TeaResponse saveTea(HttpServletRequest servletRequest) {

		TeaRequest request = convertHttpServletRequestToTeaRequest(servletRequest);

		Tea saveTea = save(convertTeaRequestToTea(request));

		return convertTeaToTeaResponse(saveTea);
	}

	public TeaResponse convertTeaToTeaResponse(Tea tea) {
		TeaResponse response = new TeaResponse();
		response.setTime(tea.getTime());
		response.setCreateDate(tea.getCreateDate());
		response.setPrice(tea.getPrice());
		response.setQuantity(tea.getQuantity());
		response.setVendor(tea.getVendor());

		return response;
	}

	private void addTableHeader(PdfPTable table) {
		PdfPCell cell = new PdfPCell();

		cell.setPhrase(new Paragraph("Vendor Name"));
		table.addCell(cell);

		cell.setPhrase(new Paragraph("Price"));
		table.addCell(cell);

		cell.setPhrase(new Paragraph("Morning Quantity"));
		table.addCell(cell);

		cell.setPhrase(new Paragraph("Afternoon Quantity"));
		table.addCell(cell);

		cell.setPhrase(new Paragraph("Evening Quantity"));
		table.addCell(cell);

		cell.setPhrase(new Paragraph("Total Days Quantity"));
		table.addCell(cell);

		cell.setPhrase(new Paragraph("Total Price"));
		table.addCell(cell);

		cell.setPhrase(new Paragraph("Create Date"));
		table.addCell(cell);

	}

	private byte[] sendInvoicesToDownloadAsPdfFormat(List<TeaResponse> listTeas) throws DocumentException, IOException {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Document document = new Document();
		PdfWriter.getInstance(document, outputStream);

		document.open();

		document.add(new Paragraph("Invoice Details"));

		PdfPTable table = new PdfPTable(8);
		table.setWidthPercentage(100);
		table.setSpacingBefore(10);

		addTableHeader(table);

		listTeas.forEach(p -> {
			fixed_price = p.getPrice();
		});

		Long grandTotalDaysQty = listTeas.stream().mapToLong(q -> q.getTotalDaysQty()).sum();

		Double grandTotal = listTeas.stream().mapToDouble(t -> t.getTotalPrice()).sum();

		listTeas.stream().forEach(listTeasDetails -> {
			table.addCell(listTeasDetails.getVendor().getVName());
			table.addCell(String.valueOf(listTeasDetails.getPrice()));

			if (listTeasDetails.getMorningTimeQty() != null) {

				table.addCell(String.valueOf(listTeasDetails.getMorningTimeQty()));
			} else {
				table.addCell(String.valueOf(0));
			}
			if (listTeasDetails.getAfternoonTimeQty() != null) {
				table.addCell(String.valueOf(listTeasDetails.getAfternoonTimeQty()));
			} else {
				table.addCell(String.valueOf(0));
			}
			if (listTeasDetails.getEveningTimeQty() != null) {

				table.addCell(String.valueOf(listTeasDetails.getEveningTimeQty()));
			} else {
				table.addCell(String.valueOf(0));
			}

			table.addCell(String.valueOf(listTeasDetails.getTotalDaysQty()));
			table.addCell(String.valueOf(listTeasDetails.getTotalPrice()));
			table.addCell(String.valueOf(listTeasDetails.getCreateDate()));
		});

		document.add(table);
		document.add(new Paragraph("Fixed Price : " + fixed_price + " INR"));
		document.add(new Paragraph("Grand Total Quantity : " + grandTotalDaysQty));
		document.add(new Paragraph("Grand Total : " + grandTotal + " INR"));

		document.close();

//		System.err.println("before pdf ...");
//
//		FileOutputStream fileOut = new FileOutputStream("/src/main/resources/static/data.pdf");
//
//		System.err.println("after pdf ...");
//
//		fileOut.write(outputStream.toByteArray());

		return outputStream.toByteArray();

	}

	private byte[] sendInvoicesToEmailAsPdfFormat(List<TeaResponse> listTeas, String email)
			throws DocumentException, IOException {

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Document document = new Document();
		PdfWriter.getInstance(document, outputStream);

		document.open();

		document.add(new Paragraph("Invoice Details"));

		PdfPTable table = new PdfPTable(8);
		table.setWidthPercentage(100);
		table.setSpacingBefore(10);

		addTableHeader(table);

		listTeas.forEach(p -> {
			fixed_price = p.getPrice();
		});

		Long grandTotalDaysQty = listTeas.stream().mapToLong(q -> q.getTotalDaysQty()).sum();

		Double grandTotal = listTeas.stream().mapToDouble(t -> t.getTotalPrice()).sum();

		listTeas.stream().forEach(listTeasDetails -> {
			table.addCell(listTeasDetails.getVendor().getVName());
			table.addCell(String.valueOf(listTeasDetails.getPrice()));

			if (listTeasDetails.getMorningTimeQty() != null) {

				table.addCell(String.valueOf(listTeasDetails.getMorningTimeQty()));
			} else {
				table.addCell(String.valueOf(0));
			}
			if (listTeasDetails.getAfternoonTimeQty() != null) {
				table.addCell(String.valueOf(listTeasDetails.getAfternoonTimeQty()));
			} else {
				table.addCell(String.valueOf(0));
			}
			if (listTeasDetails.getEveningTimeQty() != null) {

				table.addCell(String.valueOf(listTeasDetails.getEveningTimeQty()));
			} else {
				table.addCell(String.valueOf(0));
			}
			table.addCell(String.valueOf(listTeasDetails.getTotalDaysQty()));
			table.addCell(String.valueOf(listTeasDetails.getTotalPrice()));
			table.addCell(String.valueOf(listTeasDetails.getCreateDate()));
		});

		document.add(table);
		document.add(new Paragraph("Fixed Price : " + fixed_price + " INR"));
		document.add(new Paragraph("Grand Total Quantity : " + grandTotalDaysQty));
		document.add(new Paragraph("Grand Total : " + grandTotal + " INR"));

		document.close();

//		FileOutputStream fileOut = new FileOutputStream("src/main/resources/static/data.pdf");
//
//		fileOut.write(outputStream.toByteArray());

		byte[] attachEmail = outputStream.toByteArray();

		String subject = "Tea Inventory Report !!!";

		if (!email.isEmpty()) {
			emailConfig.sendEmail(email, subject, attachEmail);
		}
		return outputStream.toByteArray();

	}

	public void sendToEmailFileForVendorName(String email, Long vendorName) throws DocumentException, IOException {
		if (!email.isEmpty()) {

			List<Tea> listTeasByVendorName = teaRepository.findByVendorId(vendorName);

			List<TeaResponse> emailResponse = convertTeaToTeaResponseByDate(listTeasByVendorName);

			sendInvoicesToEmailAsPdfFormat(emailResponse, email);
		}

	}

	public BaseResponse sendToEmailFileForDate(String email, String startDate, String endDate, Long vendorId)
			throws DocumentException, IOException {
		BaseResponse response = new BaseResponse();

		if (vendorId == null && email != null && startDate.equals("") && endDate.equals("")) {

			List<TeaResponse> convertListTeasToTeaResponses = new ArrayList<>();

			List<Tea> allTeasRes = getAllTeas();

			convertListTeasToTeaResponses = convertTeaToTeaResponseByDate(allTeasRes);
			convertListTeasToTeaResponses.sort(Comparator.comparing(TeaResponse::getCreateDate).reversed());
			sendInvoicesToEmailAsPdfFormat(convertListTeasToTeaResponses, email);

			response.setMsg("Email send with attached list of Teas data !!");
			return response;
		}

		if (startDate.equals("") && endDate.equals("")) {
			startDate = null;
			endDate = null;
		}

		List<Tea> emptyListTea = new ArrayList<>();
		if (!email.isEmpty()) {

			if (vendorId != null && startDate != null && endDate != null) {
				emptyListTea = teaRepository.findByVendorIdStartDateEndDate(vendorId, startDate, endDate);

				response.setMsg("Email Send Successfully !!");
			} else if (startDate != null && endDate != null) {
				emptyListTea = teaRepository.findByDate(startDate, endDate);
				response.setMsg("Email Send Successfully !!");

			} else if (vendorId != null) {
				emptyListTea = teaRepository.findByVendorId(vendorId);
				response.setMsg("Email Send Successfully !!");
			}

			List<TeaResponse> emailResponse = convertTeaToTeaResponseByDate(emptyListTea);

			emailResponse.sort(Comparator.comparing(TeaResponse::getCreateDate).reversed());

			sendInvoicesToEmailAsPdfFormat(emailResponse, email);
		}
		return response;

	}

	public byte[] downloadToPdfByFilters(TestRequest testRequest) throws DocumentException, IOException {
		List<TeaResponse> convertListTeasToTeaResponses = new ArrayList<>();

		if (testRequest.getVendorId() == null && testRequest.getEmail() == null && testRequest.getStartDate().equals("")
				&& testRequest.getEndDate().equals("")) {

			List<Tea> allTeasRes = getAllTeas();
			convertListTeasToTeaResponses = convertTeaToTeaResponseByDate(allTeasRes);
			convertListTeasToTeaResponses.sort(Comparator.comparing(TeaResponse::getCreateDate).reversed());
			return sendInvoicesToDownloadAsPdfFormat(convertListTeasToTeaResponses);
		}

		if (testRequest.getStartDate() != null && testRequest.getEndDate() != null) {
			if (testRequest.getStartDate().equals("") && testRequest.getEndDate().equals("")
					&& testRequest.getVendorId() != null) {
				testRequest.setStartDate(null);
				testRequest.setEndDate(null);
			}
		}

		if (testRequest.getStartDate() == null && testRequest.getStartDate() == null
				&& testRequest.getVendorId() != null) {
			if (testRequest.getVendorId() != null) {
				convertListTeasToTeaResponses = getAllTeasByVendorName(Long.valueOf(testRequest.getVendorId()));
			}

		}

		if (testRequest.getStartDate() != null && testRequest.getEndDate() != null
				&& testRequest.getVendorId() == null) {
			if (testRequest.getStartDate() != null && testRequest.getEndDate() != null) {

				convertListTeasToTeaResponses = getAllTeasByDate(testRequest.getStartDate(), testRequest.getEndDate());
			}

		}
		if (testRequest.getVendorId() != null && testRequest.getStartDate() != null
				&& testRequest.getEndDate() != null) {

			String startDate = testRequest.getStartDate();
			String endDate = testRequest.getEndDate();
			Long vendorID = testRequest.getVendorId();
			List<Tea> listTea = teaRepository.findByVendorIdStartDateEndDate(vendorID, startDate, endDate);
			convertListTeasToTeaResponses = convertTeaToTeaResponseByDate(listTea);

		}

		convertListTeasToTeaResponses.sort(Comparator.comparing(TeaResponse::getCreateDate).reversed());

		if (testRequest.getEmail() != null) {
			return sendInvoicesToEmailAsPdfFormat(convertListTeasToTeaResponses, testRequest.getEmail());
		}

		return sendInvoicesToDownloadAsPdfFormat(convertListTeasToTeaResponses);

	}

	public Tea convertTeaRequestToTea(TeaRequest teaRequest) {
		Vendor vendor = new Vendor();
		Tea tea = new Tea();
		vendor.setVId(Long.valueOf(teaRequest.getVendorName()));
		if (teaRequest.getTime().equals(TimeEnum.MORNING)) {
			tea.setTime(TimeEnum.MORNING.toString());
		} else if (teaRequest.getTime().equals(TimeEnum.AFTERNOON)) {
			tea.setTime(TimeEnum.AFTERNOON.toString());
		} else if (teaRequest.getTime().equals(TimeEnum.EVENING)) {
			tea.setTime(TimeEnum.EVENING.toString());
		}
		double totalPrice = teaRequest.getQuantity() * teaRequest.getPrice();

		tea.setActive(true);
		tea.setTotalPrice(totalPrice);
		tea.setPrice(teaRequest.getPrice());
		tea.setCreateDate(teaRequest.getCreateDate());
		tea.setQuantity(teaRequest.getQuantity());
		tea.setVendor(vendor);

		return tea;
	}

	public Page<TeaResponse> findPaginatedForPageResponse(Pageable pageable, PagedRequest pagedRequest) {

		List<TeaResponse> listTeasResponse = getAllTeasByDateByVendorByVendorAndDate(pagedRequest);

		listTeasResponse.sort(Comparator.comparing(TeaResponse::getCreateDate).reversed());

		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();

		int startItem = currentPage * pageSize;

		List<TeaResponse> list = new ArrayList<>();

		if (listTeasResponse.size() < startItem) {
			list = Collections.emptyList();
		} else {
			int toIndex = Math.min(startItem + pageSize, listTeasResponse.size());
			list = listTeasResponse.subList(startItem, toIndex);
		}

		PageImpl<TeaResponse> teaPage = new PageImpl<TeaResponse>(list, PageRequest.of(currentPage, pageSize),
				listTeasResponse.size());

		return teaPage;

	}

	public Page<TeaResponse> findPaginatedForVendorName(Pageable pageable, List<Tea> listTeas) {

		List<TeaResponse> teas = convertTeaToTeaResponseByDate(listTeas);

		teas.sort(Comparator.comparing(TeaResponse::getCreateDate).reversed());

//		teas.stream().forEach(e -> {
//			System.err.println("All Data total price::" + e.getTotalPrice());
//			System.out.println("Vendor name ::" + e.getVendor().getVName());
//
//		});
		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();

		int startItem = currentPage * pageSize;

		List<TeaResponse> list = new ArrayList<>();

		if (teas.size() < startItem) {
			list = Collections.emptyList();
		} else {
			int toIndex = Math.min(startItem + pageSize, teas.size());
			list = teas.subList(startItem, toIndex);
		}

		PageImpl<TeaResponse> teaPage = new PageImpl<TeaResponse>(list, PageRequest.of(currentPage, pageSize),
				teas.size());

		return teaPage;

	}

	public Page<TeaResponse> getAllTeasByVendorNamePagination(Pageable pageable, Long vendorName) {

		List<Tea> listTeasByVendorId = teaRepository.findByVendorId(vendorName);

		Page<TeaResponse> teasPages = findPaginatedForVendorName(pageable, listTeasByVendorId);

		return teasPages;

	}

	public List<Tea> getAllTeasByVendorId(Long vendorId) {

		List<Tea> listTeasByVendorId = teaRepository.findByVendorId(vendorId);

		return listTeasByVendorId;

	}

	public List<TeaResponse> getAllTeasByVendorName(Long vendorName) {

		List<Tea> listTeasByVendorId = teaRepository.findByVendorId(vendorName);

		List<TeaResponse> teasResponse = convertTeaToTeaResponseByDate(listTeasByVendorId);

		return teasResponse;

	}

	public List<TeaResponse> getAllTeasByDateByVendorByVendorAndDate(PagedRequest pagedRequest) {
		List<TeaResponse> responses = new ArrayList<>();

		if (pagedRequest.getPage() != null && pagedRequest.getStartDate().equals("")
				&& pagedRequest.getEndDate().equals("") && pagedRequest.getVendorId() == null) {
			List<Tea> listTeasByPage = getAllTeas(pagedRequest);
			responses = convertTeaToTeaResponseByDate(listTeasByPage);
			return responses;

		} else

		if (pagedRequest.getStartDate() != null && pagedRequest.getEndDate() != null) {
			if (pagedRequest.getStartDate().equals("") && pagedRequest.getEndDate().equals("")) {
				pagedRequest.setStartDate(null);
				pagedRequest.setEndDate(null);
			}
		}

//		if (pagedRequest.getVendorId() != 0) {
		if (pagedRequest.getVendorId() != null && pagedRequest.getStartDate() != null
				&& pagedRequest.getEndDate() != null) {

			String startDate = pagedRequest.getStartDate();
			String endDate = pagedRequest.getEndDate();
			Long vendorID = pagedRequest.getVendorId();
			List<Tea> listTea = teaRepository.findByVendorIdStartDateEndDate(vendorID, startDate, endDate);
			responses = convertTeaToTeaResponseByDate(listTea);
			return responses;
		}
//		}

		if (pagedRequest.getStartDate() != null && pagedRequest.getEndDate() != null) {
			String startDate = pagedRequest.getStartDate();
			String endDate = pagedRequest.getEndDate();
			List<Tea> listTea = teaRepository.findByDate(startDate, endDate);
			responses = convertTeaToTeaResponseByDate(listTea);
			return responses;
		}

		if (pagedRequest.getVendorId() != null) {
			Long vendorID = pagedRequest.getVendorId();
			responses = getAllTeasByVendorName(vendorID);
			return responses;
		}

		return responses;
	}

	public List<TeaResponse> getAllTeasByDate(String startDate, String endDate) {
		List<Tea> listTea = teaRepository.findByDate(startDate, endDate);

		List<TeaResponse> responseByDate = convertTeaToTeaResponseByDate(listTea);
		return responseByDate;
	}

	public Page<TeaResponse> convertTeaToTeaResponseByDate_Pageable(List<Tea> listTea, Pageable pageable) {

		List<TeaResponse> convert_Tea_To_TeaResponse = convertTeaToTeaResponseByDate(listTea);

		int start = (int) pageable.getOffset();
		int end = Math.min((start + pageable.getPageSize()), convert_Tea_To_TeaResponse.size());

		return new PageImpl<>(convert_Tea_To_TeaResponse.subList(start, end), pageable,
				convert_Tea_To_TeaResponse.size());

	}

	public List<TeaResponse> convertTeaToTeaResponseByDate(List<Tea> listTea) {

		Map<String, TeaResponse> vendorMap = new HashMap<>();
		if (!listTea.isEmpty()) {

			Double grandTotal = null;
			listTea.stream().forEach(t -> {

				String createDate = t.getCreateDate();
				TeaResponse response = vendorMap.computeIfAbsent(createDate, k -> new TeaResponse());

				response.setVendor(t.getVendor());
				response.setPrice(t.getPrice());
				response.setCreateDate(t.getCreateDate());
				response.setPrice(t.getPrice());

				switch (t.getTime()) {
				case "MORNING":
					response.setMorningTimeQty(t.getQuantity());
					break;
				case "AFTERNOON":
					response.setAfternoonTimeQty(t.getQuantity());
					break;
				case "EVENING":
					response.setEveningTimeQty(t.getQuantity());
					break;
				}

			});

			vendorMap.values().forEach(response -> {
				Long totalTimesQty = 0L;

				if (response.getMorningTimeQty() != null) {
					totalTimesQty += response.getMorningTimeQty();
				}
				if (response.getAfternoonTimeQty() != null) {
					totalTimesQty += response.getAfternoonTimeQty();
				}
				if (response.getEveningTimeQty() != null) {
					totalTimesQty += response.getEveningTimeQty();
				}

				Double totalPrice = totalTimesQty * response.getPrice();

				response.setTotalDaysQty(totalTimesQty);
				response.setTotalPrice(totalPrice);
			});

		}

		return new ArrayList<>(vendorMap.values());
	}

	public TeaRequest convertHttpServletRequestToTeaRequest(HttpServletRequest servletRequest) {
		TeaRequest request = new TeaRequest();

		String vendorName = servletRequest.getParameter("vendorName");
		String date = servletRequest.getParameter("createDate");
		String time = servletRequest.getParameter("time");
		String quantity = servletRequest.getParameter("quantity");
		String price = servletRequest.getParameter("price");

		TimeEnum timeEnum = TimeEnum.valueOf(time);

		request.setVendorName(vendorName);
		request.setTime(timeEnum);
		request.setQuantity(Long.valueOf(quantity));
		request.setPrice(Double.valueOf(price));
		request.setCreateDate(date);

		return request;
	}

}
