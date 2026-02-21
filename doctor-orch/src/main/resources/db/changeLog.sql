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
    practitionerAction VARCHAR(100),
    CONSTRAINT fk_practitioner FOREIGN KEY (practitionerId) REFERENCES practitioners (practitionerId),
    CONSTRAINT fk_patient FOREIGN KEY (patientId) REFERENCES patients (patientId)
);

--rollback drop table if exists practitioners;
--rollback drop table if exists patients;
--rollback drop table if exists incidents;