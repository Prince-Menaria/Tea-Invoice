package com.Prince.TeaManagmentSystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.Prince.TeaManagmentSystem.entity.Tea;

@Repository
public interface TeaRepository extends JpaRepository<Tea, Long> {
	
	@Query(value = "SELECT * FROM teas WHERE create_date BETWEEN :startDate AND :endDate ", nativeQuery = true)
	List<Tea> findByDate(String startDate, String endDate);

	@Query(value = "SELECT * FROM teas WHERE vendor_id = :vendorId ", nativeQuery = true)
	List<Tea> findByVendorId(Long vendorId);

	@Query(value = "SELECT * FROM teas WHERE vendor_id = :vendorId AND create_date BETWEEN :startDate AND :endDate ", nativeQuery = true)
	List<Tea> findByVendorIdStartDateEndDate(Long vendorId, String startDate, String endDate);

	@Query(value = "SELECT * FROM teas f order by f.t_id desc ", nativeQuery = true)
	List<Tea> findAllOrderedByIdDesc();

//	@Query(value = "SELECT * FROM TeaInventoryManagment.teas WHERE create_date BETWEEN :startDate AND :endDate ", nativeQuery = true)
//	List<Tea> findByDate(String startDate, String endDate);
//
//	@Query(value = "SELECT * FROM TeaInventoryManagment.teas WHERE vendor_id = :vendorId ", nativeQuery = true)
//	List<Tea> findByVendorId(Long vendorId);
//
//	@Query(value = "SELECT * FROM TeaInventoryManagment.teas WHERE vendor_id = :vendorId AND create_date BETWEEN :startDate AND :endDate ", nativeQuery = true)
//	List<Tea> findByVendorIdStartDateEndDate(Long vendorId, String startDate, String endDate);
//
//	@Query(value = "SELECT * FROM TeaInventoryManagment.teas f order by f.t_id desc ", nativeQuery = true)
//	List<Tea> findAllOrderedByIdDesc();

}
