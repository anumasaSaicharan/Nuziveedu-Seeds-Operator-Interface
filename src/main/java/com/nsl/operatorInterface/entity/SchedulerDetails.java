package com.nsl.operatorInterface.entity;

import java.sql.Timestamp;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "SCHEDULER_DETAILS")
public class SchedulerDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false, updatable = false)
	private Long id;

	@Column(name = "JOB_NAME", nullable = false, length = 100)
	private String jobName;

	@Column(name = "STARTED_AT", nullable = false)
	private Timestamp startedAt;

	@Column(name = "ENDED_AT")
	private Timestamp endedAt;

	@Column(name = "STATUS", nullable = false, length = 20)
	private String status; // STARTED, SUCCESS, FAILED

	@Column(name = "DETAILS", length = 255)
	private String details;
}
