package com.nsl.operatorInterface.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "REQUEST_RESPONSE_LOG_ENTITY")
public class RequestResponseLog {
	public RequestResponseLog() {
	}

	@Id
	@GeneratedValue
	@Column(name = "ID", columnDefinition = "bigint")
	protected Long id;

	@Column(name = "CREATED_ON", updatable = false)
	private LocalDateTime createdOn;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "REQUESTED_USER_ID")
	private String requestedUserId;

	@Column(name = "REQUESTED_USER_NAME")
	private String requestedUserName;

	@Column(name = "REQUESTED_ON")
	private LocalDateTime requestedOn;

	@Column(name = "REQUEST_API", length = 4000)
	private String requestAPI;

	@Column(name = "REQUEST", length = 4000)
	private String request;

	@Column(name = "RESPONSE_ON")
	private LocalDateTime responseOn;

	@Column(name = "RESPONSE", length = 4000)
	private String response;

	@Column(name = "RESPONSE_STATUS")
	private String responseStatus;

}
