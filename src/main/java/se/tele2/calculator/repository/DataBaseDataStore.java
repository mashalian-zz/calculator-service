package se.tele2.calculator.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import se.tele2.calculator.model.Operation;
import se.tele2.calculator.model.Result;

import java.util.Optional;

@Component
@AllArgsConstructor
public class DataBaseDataStore implements DataStore {

    private ResultRepository repository;

    @Override
    public Result save(Result result) {
        return repository.save(result);
    }

    @Override
    public Optional<Result> findByNumbersAndOperation(String numbers, Operation operation) {
        return repository.findByNumbersAndOperation(numbers, operation);
    }

    @Override
    public Optional<Result> findById(int id) {
        return repository.findById(id);
    }
}
