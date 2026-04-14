package com.emergingtech.orchestration.mapper;

import com.emergingtech.orchestration.db.model.Incident;
import com.emergingtech.proto.Incident.IncidentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface IncidentMapper {

    @Mapping(target = "incidentId", ignore = true)
    @Mapping(target = "practitionerAction", ignore = true)
    Incident mapIncidentRequestToIncidentEntity(IncidentRequest incidentRequest);

}
