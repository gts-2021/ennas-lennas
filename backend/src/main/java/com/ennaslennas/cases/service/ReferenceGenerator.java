package com.ennaslennas.cases.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class ReferenceGenerator {

    @PersistenceContext
    private final EntityManager entityManager;

    @Transactional
    public String generateNextReference() {
        Number seqValue = (Number) entityManager
                .createNativeQuery("SELECT nextval('case_reference_seq')")
                .getSingleResult();

        int currentYear = Year.now().getValue();
        return String.format("ENL-%d-%06d", currentYear, seqValue.longValue());
    }
}
