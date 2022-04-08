package se.tele2.calculator.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import se.tele2.calculator.model.InputRequest;
import se.tele2.calculator.model.Operation;
import se.tele2.calculator.model.ResultResponse;
import se.tele2.calculator.service.CalculatorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@Validated
public class CalculatorController {

    private CalculatorService service;

    @PostMapping("/addition")
    public ResponseEntity<ResultResponse> addition(@Valid @RequestBody InputRequest request) {
        return ResponseEntity.ok(service.calculate(request,Operation.ADDITION));
    }

    @PostMapping("/subtraction")
    public ResponseEntity<ResultResponse> subtraction(@Valid @RequestBody InputRequest request) {
        return ResponseEntity.ok(service.calculate(request,Operation.SUBTRACTION));
    }

    @PostMapping("/multiplication")
    public ResponseEntity<ResultResponse> multiplication(@Valid @RequestBody InputRequest request) {
        return ResponseEntity.ok(service.calculate(request,Operation.MULTIPLICATION));
    }

    @PostMapping("/division")
    public ResponseEntity<ResultResponse> division(@Valid @RequestBody InputRequest request) {
        return ResponseEntity.ok(service.calculate(request,Operation.DIVISION));
    }

    @GetMapping("/existingresult/{id}")
    public ResponseEntity<ResultResponse> getExistingResult(@PathVariable int id) {
        return ResponseEntity.ok(service.getExistingResultById(id));
    }

    @GetMapping("/existingresult/{numbers}/{operation}")
    public ResponseEntity<ResultResponse> getExistingResultByNumbersAndOperation(@PathVariable List<Double> numbers, @PathVariable Operation operation) {
        return ResponseEntity.ok(service.getExistingResultByInputsAndOperation(numbers, operation));
    }
}
