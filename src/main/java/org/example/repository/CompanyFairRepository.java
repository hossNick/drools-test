package org.example.repository;

import org.example.entities.CompanyFare;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface CompanyFairRepository extends MongoRepository<CompanyFare, UUID> {
}
