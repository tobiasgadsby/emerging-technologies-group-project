--liquibase formatted sql

--changeset orchestration:1


CREATE TABLE practitioners
(
    practitionerId BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY
);

CREATE TABLE patients
(
    patientId BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY
);

CREATE TABLE incidents
(
    incidentId         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    practitionerId     BIGINT NOT NULL,
    patientId          BIGINT NOT NULL,
    location           GEOMETRY(Point, 4326),
    status             VARCHAR(100),
    practitionerAction VARCHAR(100),
    CONSTRAINT fk_practitioner FOREIGN KEY (practitionerId) REFERENCES practitioners (practitionerId),
    CONSTRAINT fk_patient FOREIGN KEY (patientId) REFERENCES patients (patientId)
);

CREATE TABLE resource
(
    resourceId         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    resourceType       varchar(100)
);

CREATE TABLE resource_mapping
(
    mappingId          BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    incidentId         BIGINT NOT NULL,
    resourceId         BIGINT NOT NULL,
    estimatedArrivalTime    TIMESTAMP WITH TIME ZONE,
    status             VARCHAR(100) NOT NULL
);

--rollback drop table if exists practitioners;
--rollback drop table if exists patients;
--rollback drop table if exists incidents;