package com.nsl.operatorInterface.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.nsl.operatorInterface.entity.TemplateMaster;

public interface TemplateMasterRepository extends JpaRepository<TemplateMaster, Long> {

}
