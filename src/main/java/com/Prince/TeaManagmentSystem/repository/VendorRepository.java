package com.Prince.TeaManagmentSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Prince.TeaManagmentSystem.entity.Vendor;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

//	@Query(value = "SELECT * FROM TeaInventoryManagment.vendors WHERE number = :vendorNumber ", nativeQuery = true)
	Vendor findByNumber(String vendorNumber);

}
