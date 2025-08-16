package com.nsl.operatorInterface.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "PACKING_ORDER_DETAILS")
public class PackingOrderDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CREATED_ON", updatable = false)
    private LocalDateTime createdOn;

    @Column(name = "MODIFIED_ON")
    private LocalDateTime modifiedOn;

    @Column(name = "ACTIVE", nullable = false)
    private boolean active;

    @Column(name = "PLANT_CODE", nullable = false, length = 20)
    private String plantCode;

    @Column(name = "PRODUCTION_ORDER_NO", nullable = false, length = 50)
    private String productionOrderNo;

    @Column(name = "LOT_NO", nullable = false, length = 50)
    private String lotNo;

    @Column(name = "QTY", nullable = false, length = 50)
    private String qty;

    @Column(name = "INDENT_NO", nullable = false, length = 50)
    private String indentNo;

    @Column(name = "SAP_STATUS", length = 50)
    private String sapStatus;
    
	@Column(name = "UID", unique = true, nullable = false, length = 12)
	private String uid;
}
