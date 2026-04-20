package com.emergingtech.orchestration.db.service;

import com.emergingtech.orchestration.db.model.Incident;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;

import java.math.BigInteger;
import java.util.List;

@ApplicationScoped
public class IncidentDbService {

    @Inject
    EntityManager entityManager;

    @Transactional
    public Incident createIncident(Incident incident) {
        entityManager.persist(incident);
        return incident;
    }

    @Transactional
    public Incident updateIncident(Incident incident) {
        return entityManager.merge(incident);
    }

    public List<BigInteger> getIncidentIds() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Incident> incident = query.from(Incident.class);
        query.select(incident.get("incidentId"));
        return entityManager.createQuery(query).getResultList().stream().map(
                BigInteger::valueOf
        ).toList();
    }

    @Transactional
    public Incident getIncidentById(Long id) {
        return entityManager.find(Incident.class, id);
    }

    public List<Incident> getIncidentsByPractitionerId(Long practitionerId) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Incident> query = builder.createQuery(Incident.class);
        Root<Incident> incident = query.from(Incident.class);
        query.select(incident).where(
                builder.equal(incident.get("practitionerId"),  practitionerId)
        );

        return entityManager.createQuery(query).getResultList();
    }

    public <T> boolean exists(Class<T> clazz, Long id) {
        return entityManager.find(clazz, id) == null;
    }

}
