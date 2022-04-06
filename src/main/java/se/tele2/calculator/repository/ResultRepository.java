package se.tele2.calculator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se.tele2.calculator.model.Operation;
import se.tele2.calculator.model.Result;

import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Integer> {
    Optional<Result> findByNumbersAndOperation(String numbers, Operation operation);
}
