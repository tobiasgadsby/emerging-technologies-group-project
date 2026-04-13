package com.emergingtech.orchestration.mapper;

import com.emergingtech.beans.Incident;
import org.mapstruct.Mapper;

@Mapper(componentModel = "cdi")
public interface IncidentMapper {

    Incident mapIncidentEntityToIncidentDto(com.emergingtech.orchestration.db.model.Incident incidentEntity);

    com.emergingtech.orchestration.db.model.Incident mapIncidentDtoToIncidentEntity(Incident incidentDto);

}
