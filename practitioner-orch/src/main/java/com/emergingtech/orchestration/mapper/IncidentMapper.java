package com.emergingtech.orchestration.mapper;

import com.emergingtech.beans.Incident;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "cdi")
public interface IncidentMapper {

    @Mapping(source = "status", target = "incidentStatus")
    Incident mapIncidentEntityToIncidentDto(com.emergingtech.orchestration.db.model.Incident incidentEntity);

    com.emergingtech.orchestration.db.model.Incident mapIncidentDtoToIncidentEntity(Incident incidentDto);

}
