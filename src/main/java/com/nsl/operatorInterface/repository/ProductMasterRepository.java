package com.nsl.operatorInterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nsl.operatorInterface.entity.ProductMaster;

public interface ProductMasterRepository extends JpaRepository<ProductMaster, Long> {

    ProductMaster findByProductNameAndpackSize(String productName,);
    
    ProductMaster findByProductName(String productName);


}
