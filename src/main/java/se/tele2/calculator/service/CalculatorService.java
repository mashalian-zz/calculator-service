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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static se.tele2.calculator.model.Operation.ADDITION;
import static se.tele2.calculator.model.Operation.DIVISION;
import static se.tele2.calculator.model.Operation.MULTIPLICATION;
import static se.tele2.calculator.model.Operation.SUBTRACTION;

@Service
@AllArgsConstructor
@Slf4j
public class CalculatorService {

    private DataBaseDataStore dataStore;

    public ResultResponse addition(InputRequest request) {
        String inputs = getInputs(request.inputs(), true);
        Result result = dataStore.findByNumbersAndOperation(inputs, ADDITION)
                .orElseGet(() -> {
                    log.info("Calculating addition operation for {}", inputs);
                    double sum = request.inputs()
                            .stream()
                            .mapToDouble(Double::doubleValue)
                            .reduce(Double::sum)
                            .orElseThrow(() -> new EmptyInputsException("No number to do the operation"));
                    return dataStore.save(Result.builder()
                            .operation(ADDITION)
                            .result(sum)
                            .numbers(inputs)
                            .build());
                });
        return new ResultResponse(result.getResult());
    }

    public ResultResponse subtraction(InputRequest request) {
        String inputs = getInputs(request.inputs(), false);
        Result result = dataStore.findByNumbersAndOperation(inputs, SUBTRACTION)
                .orElseGet(() -> {
                    log.info("Calculating subtraction operation for {}", inputs);
                    double resultSub = request.inputs()
                            .stream()
                            .mapToDouble(Double::doubleValue)
                            .reduce((e1, e2) -> e1 - e2)
                            .orElseThrow(() -> new EmptyInputsException("No number to do the operation"));
                    return dataStore.save(Result.builder()
                            .operation(SUBTRACTION)
                            .result(resultSub)
                            .numbers(inputs)
                            .build());

                });
        return new ResultResponse(result.getResult());
    }

    public ResultResponse multiplication(InputRequest request) {
        String inputs = getInputs(request.inputs(), true);
        Result result = dataStore.findByNumbersAndOperation(inputs, MULTIPLICATION)
                .orElseGet(() -> {
                    log.info("Calculating multiplication operation for {}", inputs);
                    double resultMul = request.inputs()
                            .stream()
                            .mapToDouble(Double::doubleValue)
                            .reduce((e1, e2) -> e1 * e2)
                            .orElseThrow(() -> new EmptyInputsException("No number to do the operation"));
                    return dataStore.save(Result.builder()
                            .operation(MULTIPLICATION)
                            .result(resultMul)
                            .numbers(inputs)
                            .build());
                });
        return new ResultResponse(result.getResult());
    }

    public ResultResponse division(InputRequest request) {
        String inputs = getInputs(request.inputs(), false);
        Result result = dataStore.findByNumbersAndOperation(inputs, DIVISION)
                .orElseGet(() -> {
                    log.info("Calculating division operation for {}", inputs);
                    double resultDiv = request.inputs()
                            .stream()
                            .mapToDouble(Double::doubleValue)
                            .reduce((dividend, divisor) -> {
                                if (divisor == 0)
                                    throw new ArithmeticException("Divide by zero.");
                                return dividend / divisor;
                            })
                            .orElseThrow(() -> new EmptyInputsException("No number to do the operation"));
                    return dataStore.save(Result.builder()
                            .operation(DIVISION)
                            .result(resultDiv)
                            .numbers(inputs)
                            .build());

                });
        return new ResultResponse(result.getResult());
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
