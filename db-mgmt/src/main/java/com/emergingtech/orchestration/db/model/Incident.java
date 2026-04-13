package com.emergingtech.orchestration.db.model;

import com.emergingtech.orchestration.db.common.IncidentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "incidents")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long incidentId;

    private Long practitionerId;

    private Long patientId;

    private String status;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;

    @Column(nullable = true)
    private String practitionerAction;

}
