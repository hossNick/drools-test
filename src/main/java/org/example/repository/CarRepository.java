package org.example.repository;

import org.example.entities.Car;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface CarRepository extends MongoRepository<Car, UUID> {
}
