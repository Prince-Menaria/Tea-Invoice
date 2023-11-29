package com.Prince.TeaManagmentSystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "teas")
public class Tea {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long tId;
	private String time;
	private Long quantity;
	private Long totalDaysQty;

	private double price;
	private double totalPrice;

	private boolean isActive;

	private String createDate;

//	private Date updateDate;

	@ManyToOne
	@JoinColumn(name = "vendor_id")
	private Vendor vendor;

}
