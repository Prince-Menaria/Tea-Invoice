package com.Prince.TeaManagmentSystem.request;

import lombok.Data;

@Data
public class TestRequest {

	private String startDate;
	private String endDate;
//	private String vendorName;
	private Long vendorId;
	private String email;

}
