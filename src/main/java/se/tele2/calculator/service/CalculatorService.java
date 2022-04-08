package se.tele2.calculator.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.tele2.calculator.exception.EmptyInputsException;
import se.tele2.calculator.exception.NotFoundException;
import se.tele2.calculator.model.InputRequest;
import se.tele2.calculator.model.Operation;
import se.tele2.calculator.model.Result;
import se.tele2.calculator.model.ResultResponse;
import se.tele2.calculator.repository.DataBaseDataStore;

import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import static se.tele2.calculator.model.Operation.ADDITION;
import static se.tele2.calculator.model.Operation.MULTIPLICATION;

@Service
@AllArgsConstructor
@Slf4j
public class CalculatorService {

    private DataBaseDataStore dataStore;

    public ResultResponse calculate(InputRequest request, Operation operation) {
        String inputs = "";
        if (List.of(ADDITION, MULTIPLICATION).contains(operation)) {
            inputs = getInputs(request.inputs(), true);
        }
        final String fInputs = inputs.length() > 0 ? inputs : getInputs(request.inputs(), false);
        Result result = dataStore.findByNumbersAndOperation(fInputs, operation)
                .orElseGet(() -> {
                    log.info("Calculating {} operation for {}", operation.name(), fInputs);
                    double aDouble = doCalculation(request, operation);
                    return dataStore.save(Result.builder()
                            .operation(operation)
                            .result(aDouble)
                            .numbers(fInputs)
                            .build());
                });
        return new ResultResponse(result.getResult());
    }

    private Double doCalculation(InputRequest request, Operation operation) {
        DoubleStream doubleStream = request.inputs()
                .stream()
                .mapToDouble(Double::doubleValue);
        OptionalDouble optionalDouble = OptionalDouble.empty();
        switch (operation) {
            case ADDITION -> optionalDouble = doubleStream.reduce(Double::sum);
            case SUBTRACTION -> optionalDouble = doubleStream.reduce((e1, e2) -> e1 - e2);
            case MULTIPLICATION -> optionalDouble = doubleStream.reduce((e1, e2) -> e1 * e2);
            case DIVISION -> optionalDouble = doubleStream.reduce((dividend, divisor) -> {
                if (divisor == 0)
                    throw new ArithmeticException("Divide by zero.");
                return dividend / divisor;
            });
        }
        return optionalDouble.orElseThrow(() -> {
            log.error("No number to do the operation");
           throw  new EmptyInputsException("No number to do the operation");
        });
    }

    public ResultResponse getExistingResultById(int id) {
        return dataStore.findById(id)
                .map(result -> new ResultResponse(result.getResult()))
                .orElseThrow(() -> new NotFoundException("Result does not exist"));
    }

    public ResultResponse getExistingResultByInputsAndOperation(List<Double> inputs, Operation operation) {
        String numbers;
        switch (operation) {
            case ADDITION, MULTIPLICATION -> numbers = getInputs(inputs, true);
            default -> numbers = getInputs(inputs, false);
        }
        return dataStore.findByNumbersAndOperation(numbers, operation)
                .map(result -> new ResultResponse(result.getResult()))
                .orElseThrow(() -> new NotFoundException("Result does not exist"));
    }

    private String getInputs(List<Double> inputs, boolean sort) {
        Stream<Double> stream = inputs.stream();
        if (sort) {
            stream = stream.sorted();
        }
        return stream.map(Object::toString)
                .collect(Collectors.joining(","));
    }
}
