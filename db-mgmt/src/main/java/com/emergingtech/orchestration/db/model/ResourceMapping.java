package com.emergingtech.orchestration.db.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "resource_mapping")
public class ResourceMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mappingId;

    private Long incidentId;

    private Long resourceId;

    private LocalDateTime estimatedArrivalTime;

    private String status;

}
