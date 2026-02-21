package com.emergingtech.orchestration.service.database;

import com.emergingtech.orchestration.entity.model.Incident;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class DatabaseService {

    @Inject
    EntityManager entityManager;

    @Transactional
    public Incident createIncident(Incident incident) {
        entityManager.persist(incident);
        return incident;
    }

    public <T> T getReferenceFromId(Class<T> clazz, Long id) {
        return entityManager.getReference(clazz, id);
    }

    public <T> boolean exists(Class<T> clazz, Long id) {
        return entityManager.find(clazz, id) != null;
    }

}
