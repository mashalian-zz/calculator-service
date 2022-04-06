package se.tele2.calculator.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.tele2.calculator.model.Operation;
import se.tele2.calculator.model.Result;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static se.tele2.calculator.model.Operation.DIVISION;


@ExtendWith(SpringExtension.class)
@Import(DataBaseDataStore.class)
class DataBaseDataStoreTest {

    @MockBean
    private ResultRepository repository;

    @Autowired
    private DataBaseDataStore dataStore;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Captor
    private ArgumentCaptor<Operation> operationArgumentCaptor;

    @Captor
    private ArgumentCaptor<Integer> integerArgumentCaptor;

    @Test
    void shouldReturnSaveResult() {
        String numbers = "30.0,2.0,3.0";
        Result result = Result.builder()
                .id(1)
                .operation(DIVISION)
                .result(5.0)
                .numbers(numbers)
                .build();
        given(repository.save(eq(result))).willReturn(result);

        Result save = dataStore.save(result);

        assertThat(save).isNotNull();
        assertThat(save)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("operation", Operation.DIVISION)
                .hasFieldOrPropertyWithValue("result", 5.0)
                .hasFieldOrPropertyWithValue("numbers", numbers);
        verify(repository).save(eq(result));
    }

    @Test
    void shouldReturnResultWithNumbersAndOperation() {
        String numbers = "30.0,2.0,3.0";
        Result result = Result.builder()
                .operation(DIVISION)
                .result(5.0)
                .numbers(numbers)
                .build();
        given(repository.findByNumbersAndOperation(anyString(), any())).willReturn(Optional.of(result));

        Optional<Result> resultOptional = dataStore.findByNumbersAndOperation(numbers, Operation.DIVISION);

        assertThat(resultOptional.isPresent()).isTrue();
        verify(repository).findByNumbersAndOperation(stringArgumentCaptor.capture(), operationArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).isEqualTo(numbers);
        assertThat(operationArgumentCaptor.getValue()).isEqualTo(Operation.DIVISION);
    }

    @Test
    void shouldFindResultById() {
        String numbers = "30.0,2.0,3.0";
        Result result = Result.builder()
                .operation(DIVISION)
                .result(5.0)
                .numbers(numbers)
                .build();
        given(repository.findById(eq(1))).willReturn(Optional.of(result));

        Optional<Result> optionalResult = dataStore.findById(1);

        assertThat(optionalResult.isPresent()).isTrue();
        verify(repository).findById(integerArgumentCaptor.capture());
        assertThat(integerArgumentCaptor.getValue()).isEqualTo(1);
    }
}