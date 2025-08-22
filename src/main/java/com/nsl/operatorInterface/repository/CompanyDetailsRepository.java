package com.nsl.operatorInterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nsl.operatorInterface.entity.CompanyDetails;

public interface CompanyDetailsRepository extends JpaRepository<CompanyDetails, Long> {

	CompanyDetails findByCompanyId(String companyId);

}
