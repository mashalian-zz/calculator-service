package se.tele2.calculator.repository;

import se.tele2.calculator.model.Operation;
import se.tele2.calculator.model.Result;

import java.util.Optional;

public interface DataStore {
    Result save(Result result);
    Optional<Result> findByNumbersAndOperation(String numbers, Operation operation);
    Optional<Result> findById(int id);
}
