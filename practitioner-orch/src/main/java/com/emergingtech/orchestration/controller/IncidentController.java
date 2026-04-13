package com.emergingtech.orchestration.controller;

import com.emergingtech.IncidentsResource;
import com.emergingtech.beans.Incident;
import com.emergingtech.orchestration.common.PractitionerAction;
import com.emergingtech.orchestration.db.model.Patient;
import com.emergingtech.orchestration.db.model.Practitioner;
import com.emergingtech.orchestration.mapper.IncidentMapper;
import com.emergingtech.orchestration.db.service.IncidentDbService;
import com.emergingtech.proto.ResourceMapper.PractitionerActionRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

public class IncidentController implements IncidentsResource {

    @Inject
    IncidentDbService incidentDbService;

    @Inject
    IncidentMapper incidentMapper;

    @Inject
    @Channel("action-request")
    Emitter<PractitionerActionRequest> practitionerActionRequestEmitter;

    @Override
    public Incident getIncident(Long incidentId) {
        return null;
    }

    @Override
    public Incident updateIncident(Incident incident) {

        com.emergingtech.orchestration.db.model.Incident updatedIncident = incidentDbService.updateIncident(incidentMapper.mapIncidentDtoToIncidentEntity(incident));

        if (updatedIncident.getPractitionerAction().equals(PractitionerAction.TRANSFER_TO_HOSPITAL_URGENT.name())) {
            PractitionerActionRequest practitionerActionRequest = PractitionerActionRequest.newBuilder()
                    .setPractitionerId(updatedIncident.getPractitionerId())
                    .setIncidentId(updatedIncident.getIncidentId())
                    .setPatientId(updatedIncident.getPatientId()).build();

            practitionerActionRequestEmitter.send(practitionerActionRequest);
        }

        return incident;

    }

    @Override
    public Incident deleteIncident(Long incidentId) {
        return null;
    }

    @Override
    public Incident createIncident(Incident incident) {

        if (incidentDbService.exists(Practitioner.class, Long.valueOf(incident.getPractitionerId()))) {
            throw new NotFoundException("Practitioner with id " + incident.getPractitionerId() + " not found");
        }

        if (incidentDbService.exists(Patient.class, Long.valueOf(incident.getPatientId()))) {
            throw new NotFoundException("Patient with id " + incident.getPatientId() + " not found");
        }

        com.emergingtech.orchestration.db.model.Incident response = incidentDbService.createIncident(incidentMapper.mapIncidentDtoToIncidentEntity(incident));

        return incidentMapper.mapIncidentEntityToIncidentDto(response);

    }
}
