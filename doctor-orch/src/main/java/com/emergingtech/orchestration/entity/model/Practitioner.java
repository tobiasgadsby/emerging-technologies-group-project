package com.emergingtech.orchestration.entity.model;

import jakarta.persistence.*;

@Entity
@Table(name = "practitioners")
public class Practitioner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long practitionerId;
}
