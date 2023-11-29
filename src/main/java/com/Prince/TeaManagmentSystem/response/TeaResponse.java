package com.Prince.TeaManagmentSystem.response;

import com.Prince.TeaManagmentSystem.entity.Vendor;

import lombok.Data;

@Data
public class TeaResponse {

	private Long morningTimeQty;
	private Long afternoonTimeQty;
	private Long eveningTimeQty;

	private String time;
	private Long quantity;
	private Long totalDaysQty;
	private Double grandTotal;

	private double price;
	private double totalPrice;

	private boolean isActive;

	private String createDate;

	private Vendor vendor;

}
