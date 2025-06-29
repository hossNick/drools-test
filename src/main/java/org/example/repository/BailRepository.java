package org.example.repository;

import org.example.entities.Bail;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface BailRepository extends MongoRepository<Bail, UUID> {
}
