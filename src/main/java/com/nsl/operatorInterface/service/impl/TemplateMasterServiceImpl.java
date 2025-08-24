package com.nsl.operatorInterface.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nsl.operatorInterface.entity.TemplateMaster;
import com.nsl.operatorInterface.repository.TemplateMasterRepository;
import com.nsl.operatorInterface.service.TemplateMasterService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TemplateMasterServiceImpl implements TemplateMasterService {

    @Autowired
    private TemplateMasterRepository repository;

    @Override
    public TemplateMaster createTemplate(TemplateMaster template) {
        log.info("Creating Template: {}", template);
        return repository.save(template);
    }

    @Override
    public List<TemplateMaster> getAllTemplates() {
        return repository.findAll();
    }

    @Override
    public TemplateMaster getTemplateById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found with id: " + id));
    }

    @Override
    public TemplateMaster updateTemplate(Long id, TemplateMaster template) {
        TemplateMaster existing = getTemplateById(id);
        existing.setTemplateName(template.getTemplateName()); // add other fields as needed
        return repository.save(existing);
    }

    @Override
    public void deleteTemplate(Long id) {
        TemplateMaster existing = getTemplateById(id);
        repository.delete(existing);
    }
}
