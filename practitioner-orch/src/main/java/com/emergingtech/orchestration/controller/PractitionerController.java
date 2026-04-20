package com.emergingtech.orchestration.controller;

import com.emergingtech.PractitionersResource;
import com.emergingtech.beans.Incident;
import com.emergingtech.orchestration.db.service.IncidentDbService;
import com.emergingtech.orchestration.mapper.IncidentMapper;
import jakarta.inject.Inject;

import java.util.List;

public class PractitionerController implements PractitionersResource {

    @Inject
    IncidentDbService incidentDbService;

    @Inject
    IncidentMapper incidentMapper;

    public List<Incident> getIncidentsByPractitionerId(Long practitionerId) {

        return incidentDbService.getIncidentsByPractitionerId(practitionerId).stream()
                .map(incidentMapper::mapIncidentEntityToIncidentDto)
                .toList();

    }

}
