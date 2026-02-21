package com.emergingtech.orchestration.mapper;

import com.emergingtech.orchestration.entity.model.Incident;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface IncidentMapper {

    Incident mapIncidentDtoToIncidentEntity(com.emergingtech.beans.Incident incidentDto);

    com.emergingtech.beans.Incident mapIncidentEntityToIncidentDto(Incident incident);

}
