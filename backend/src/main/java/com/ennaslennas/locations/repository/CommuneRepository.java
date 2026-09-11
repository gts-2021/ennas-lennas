package com.ennaslennas.locations.repository;

import com.ennaslennas.locations.domain.Commune;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommuneRepository extends JpaRepository<Commune, Long> {
    List<Commune> findByWilayaIdOrderByNameFrAsc(Long wilayaId);
}
