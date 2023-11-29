package com.Prince.TeaManagmentSystem.request;

import lombok.Data;

@Data
public class PagedRequest {

	private Long vendorId;
	private String startDate;
	private String endDate;
	private Integer page;
	

}
