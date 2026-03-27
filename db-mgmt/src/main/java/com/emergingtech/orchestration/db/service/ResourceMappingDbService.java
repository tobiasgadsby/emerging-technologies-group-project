package com.emergingtech.orchestration.db.service;

import com.emergingtech.orchestration.db.model.ResourceMapping;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

@ApplicationScoped
public class ResourceMappingDbService {

    @Inject
    EntityManager entityManager;

    public ResourceMapping createResourceMapping(ResourceMapping resourceMapping) {
        entityManager.persist(resourceMapping);
        return resourceMapping;
    }

    public ResourceMapping updateResourceMapping(ResourceMapping resourceMapping) {
        return entityManager.merge(resourceMapping);
    }

    public ResourceMapping getResourceMappingById(Long resourceMappingId) {
        return entityManager.find(ResourceMapping.class, resourceMappingId);
    }

}
