package org.example.repository;

import org.example.entities.Contract;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ContractRepository extends MongoRepository<Contract, UUID> {
}
