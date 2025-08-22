package com.nsl.operatorInterface.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nsl.operatorInterface.entity.ProductMaster;

public interface ProductMasterRepository extends JpaRepository<ProductMaster, Long> {

    ProductMaster findByProductNameAndPackSize(String productName, BigDecimal packSize);
    
    ProductMaster findByProductName(String productName);


}
