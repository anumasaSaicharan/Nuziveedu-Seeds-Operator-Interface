package com.nsl.operatorInterface.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nsl.operatorInterface.dto.ApiResponse;
import com.nsl.operatorInterface.entity.TemplateMaster;
import com.nsl.operatorInterface.service.TemplateMasterService;

@RestController
@RequestMapping("/rest/nsl/operatorInterface/printers/templates/")
public class TemplateMasterController {

	@Autowired
	private TemplateMasterService templateMasterService;

	// Create
	@PostMapping("add-template")
	public ResponseEntity<ApiResponse> createTemplate(@RequestBody TemplateMaster template) {
		TemplateMaster savedTemplate = templateMasterService.createTemplate(template);
		return ResponseEntity.ok(new ApiResponse(201, "Template created successfully", savedTemplate));
	}

	// Read All
	@GetMapping("view-all-templates")
	public ResponseEntity<ApiResponse> getAllTemplates() {
		List<TemplateMaster> templates = templateMasterService.getAllTemplates();
		return ResponseEntity.ok(new ApiResponse(200, "Templates fetched successfully", templates));
	}

	// Read by ID
	@GetMapping("/view-template/{id}")
	public ResponseEntity<ApiResponse> getTemplateById(@PathVariable Long id) {
	    TemplateMaster template = templateMasterService.getTemplateById(id);
	    return ResponseEntity.ok(new ApiResponse(200, "Template fetched successfully", template));
	}

	// Update
	@PutMapping("/update-template/{id}")
	public ResponseEntity<ApiResponse> updateTemplate(@PathVariable Long id, @RequestBody TemplateMaster template) {
	    TemplateMaster updatedTemplate = templateMasterService.updateTemplate(id, template);
	    return ResponseEntity.ok(new ApiResponse(200, "Template updated successfully", updatedTemplate));
	}

	// Delete
	@DeleteMapping("/delete-template/{id}")
	public ResponseEntity<ApiResponse> deleteTemplate(@PathVariable Long id) {
	    templateMasterService.deleteTemplate(id);
	    return ResponseEntity.ok(new ApiResponse(200, "Template deleted successfully", null));
	}

}
