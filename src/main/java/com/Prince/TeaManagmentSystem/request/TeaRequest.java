package com.Prince.TeaManagmentSystem.request;

import com.Prince.TeaManagmentSystem.entity.TimeEnum;

import lombok.Data;

@Data
public class TeaRequest {

	private String vendorName;

//	private Long tId;
	private TimeEnum time;
	private Long quantity;
//	private Long totalDaysQty;

	private double price;
//	private double totalPrice;

	private boolean isActive;

	private String createDate;

}
