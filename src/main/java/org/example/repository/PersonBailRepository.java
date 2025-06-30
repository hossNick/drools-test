package org.example.repository;

import org.example.entities.PersonBail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PersonBailRepository extends JpaRepository<PersonBail, UUID> {
}