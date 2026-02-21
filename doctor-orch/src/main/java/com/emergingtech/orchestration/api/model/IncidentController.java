package com.emergingtech.orchestration.api.model;

import com.emergingtech.IncidentsResource;
import com.emergingtech.beans.Incident;
import com.emergingtech.orchestration.common.PractitionerAction;
import com.emergingtech.orchestration.entity.model.Patient;
import com.emergingtech.orchestration.entity.model.Practitioner;
import com.emergingtech.orchestration.mapper.IncidentMapper;
import com.emergingtech.orchestration.service.database.DatabaseService;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;

import java.math.BigInteger;

@Path("/incidents")
public class IncidentController implements IncidentsResource {

    private final DatabaseService databaseService;

    @Inject IncidentMapper incidentMapper;

    public IncidentController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * @param data
     * @return
     */
    @Override
    public Incident updateIncident(Incident data) {
        return null;
    }

    /**
     * @param data
     * @return
     */
    @Override
    public Incident createIncident(Incident incident) {

        if (!databaseService.exists(Practitioner.class, Long.valueOf(incident.getPractitionerId()))) {
            throw new NotFoundException("Practitioner with id " + incident.getPractitionerId() + " not found");
        }

        if (!databaseService.exists(Patient.class, Long.valueOf(incident.getPatientId()))) {
            throw new NotFoundException("Patient with id " + incident.getPatientId() + " not found");
        }

        com.emergingtech.orchestration.entity.model.Incident response = databaseService.createIncident(incidentMapper.mapIncidentDtoToIncidentEntity(incident));

        return incidentMapper.mapIncidentEntityToIncidentDto(response);

    }

    /**
     * @param incidentId
     * @return
     */
    @Override
    public Incident deleteIncident(BigInteger incidentId) {
        return null;
    }
}
