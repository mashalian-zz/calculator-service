package se.tele2.calculator.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import se.tele2.calculator.model.Operation;
import se.tele2.calculator.model.Result;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static se.tele2.calculator.model.Operation.DIVISION;


@DataJpaTest
class ResultRepositoryTest {

    @Autowired
    private ResultRepository repository;

    @Test
    void shouldFindResultByNumbersAndOperation() {
        String numbers = "30.0,2.0,3.0";
        Result result = Result.builder()
                .operation(DIVISION)
                .result(5.0)
                .numbers(numbers)
                .build();
        repository.save(result);
        Optional<Result> byNumbersAndOperation = repository.findByNumbersAndOperation(numbers, Operation.DIVISION);

        assertThat(byNumbersAndOperation.isPresent()).isTrue();
        assertThat(byNumbersAndOperation.get().getNumbers()).isEqualTo(numbers);
        assertThat(byNumbersAndOperation.get().getOperation()).isEqualTo(Operation.DIVISION);
        assertThat(byNumbersAndOperation.get().getResult()).isEqualTo(5.0);
    }
}