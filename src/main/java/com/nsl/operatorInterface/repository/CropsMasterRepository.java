package com.nsl.operatorInterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nsl.operatorInterface.entity.CropMaster;

public interface CropsMasterRepository extends JpaRepository<CropMaster, Long> {


}
