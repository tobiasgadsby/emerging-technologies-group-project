package com.emergingtech.orchestration.db.model;

import jakarta.persistence.*;

@Entity
@Table(name = "practitioners")
public class Practitioner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long practitionerId;
}
