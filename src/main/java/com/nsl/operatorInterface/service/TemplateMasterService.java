package com.nsl.operatorInterface.service;

import java.util.List;
import com.nsl.operatorInterface.entity.TemplateMaster;

public interface TemplateMasterService {

    TemplateMaster createTemplate(TemplateMaster template);

    List<TemplateMaster> getAllTemplates();

    TemplateMaster getTemplateById(Long id);

    TemplateMaster updateTemplate(Long id, TemplateMaster template);

    void deleteTemplate(Long id);
}
