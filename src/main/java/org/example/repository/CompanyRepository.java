package org.example.repository;

import org.example.entities.Company;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface CompanyRepository extends MongoRepository<Company, UUID> {
}
