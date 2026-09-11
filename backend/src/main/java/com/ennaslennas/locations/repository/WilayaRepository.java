package com.ennaslennas.locations.repository;

import com.ennaslennas.locations.domain.Wilaya;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WilayaRepository extends JpaRepository<Wilaya, Long> {
    List<Wilaya> findAllByOrderByCodeAsc();
    Optional<Wilaya> findByCode(String code);
}
