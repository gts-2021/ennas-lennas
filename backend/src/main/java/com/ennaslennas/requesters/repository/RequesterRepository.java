package com.ennaslennas.requesters.repository;

import com.ennaslennas.requesters.domain.Requester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequesterRepository extends JpaRepository<Requester, Long> {
    Optional<Requester> findByPhone(String phone);
}
