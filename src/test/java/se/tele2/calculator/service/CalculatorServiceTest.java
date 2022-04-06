package se.tele2.calculator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.tele2.calculator.exception.EmptyInputsException;
import se.tele2.calculator.exception.NotFoundException;
import se.tele2.calculator.model.InputRequest;
import se.tele2.calculator.model.Operation;
import se.tele2.calculator.model.Result;
import se.tele2.calculator.model.ResultResponse;
import se.tele2.calculator.repository.DataBaseDataStore;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static se.tele2.calculator.model.Operation.ADDITION;
import static se.tele2.calculator.model.Operation.DIVISION;
import static se.tele2.calculator.model.Operation.MULTIPLICATION;
import static se.tele2.calculator.model.Operation.SUBTRACTION;

@ExtendWith(SpringExtension.class)
@Import(CalculatorService.class)
class CalculatorServiceTest {

    @MockBean
    private DataBaseDataStore dataStore;

    @Autowired
    private CalculatorService service;

    @Captor
    private ArgumentCaptor<Result> resultArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Captor
    private ArgumentCaptor<Operation> operationArgumentCaptor;

    @Test
    void shouldAddNumbers() {
        given(dataStore.findByNumbersAndOperation(anyString(), any())).willReturn(Optional.empty());
        String numbers = "10.0,20.0,30.0";
        Result result = getResultWithTestData(ADDITION, 60.0, numbers);
        given(dataStore.save(any())).willReturn(result);
        InputRequest inputRequest = new InputRequest(List.of(10.0, 20.0, 30.0));
        ResultResponse response = service.addition(inputRequest);

        assertThat(response).isNotNull();
        assertThat(response.result().doubleValue()).isEqualTo(60.0);

        verify(dataStore).save(resultArgumentCaptor.capture());

        assertThat(resultArgumentCaptor.getValue())
                .hasFieldOrPropertyWithValue("operation", Operation.ADDITION)
                .hasFieldOrPropertyWithValue("result", 60.0)
                .hasFieldOrPropertyWithValue("numbers", numbers);
    }

    @Test
    void shouldNotCalculateAdditionIfItCalculatedBefore() {
        String numbers = "10.0,20.0,30.0";
        given(dataStore.findByNumbersAndOperation(anyString(), any())).willReturn(Optional.of(getResultWithTestData(ADDITION, 60.0, numbers)));

        InputRequest inputRequest = new InputRequest(List.of(10.0, 20.0, 30.0));
        ResultResponse response = service.addition(inputRequest);

        assertThat(response).isNotNull();
        assertThat(response.result().doubleValue()).isEqualTo(60.0);
        verify(dataStore, times(0)).save(any());
    }

    @Test
    void shouldThrowEmptyInputsExceptionIfInputRequestHasNoNumber() {
        InputRequest inputRequest = new InputRequest(List.of());

        assertThatExceptionOfType(EmptyInputsException.class)
                .isThrownBy(() -> service.addition(inputRequest))
                .withMessage("No number to do the operation");
    }

    @Test
    void shouldDoSubtraction() {
        given(dataStore.findByNumbersAndOperation(anyString(), any())).willReturn(Optional.empty());
        String numbers = "10.0,20.0,30.0";
        Result result = getResultWithTestData(SUBTRACTION, -40.0, numbers);
        given(dataStore.save(any())).willReturn(result);
        InputRequest inputRequest = new InputRequest(List.of(10.0, 20.0, 30.0));
        ResultResponse response = service.subtraction(inputRequest);

        assertThat(response).isNotNull();
        assertThat(response.result().doubleValue()).isEqualTo(-40.0);

        verify(dataStore).save(resultArgumentCaptor.capture());

        assertThat(resultArgumentCaptor.getValue())
                .hasFieldOrPropertyWithValue("operation", Operation.SUBTRACTION)
                .hasFieldOrPropertyWithValue("result", -40.0)
                .hasFieldOrPropertyWithValue("numbers", numbers);
    }

    @Test
    void shouldNotCalculateSubtractionIfItCalculatedBefore() {
        String numbers = "10.0,20.0,30.0";

        given(dataStore.findByNumbersAndOperation(anyString(), any())).willReturn(Optional.of(getResultWithTestData(SUBTRACTION, -40.0, numbers)));

        InputRequest inputRequest = new InputRequest(List.of(10.0, 20.0, 30.0));
        ResultResponse response = service.subtraction(inputRequest);

        assertThat(response).isNotNull();
        assertThat(response.result().doubleValue()).isEqualTo(-40.0);
        verify(dataStore, times(0)).save(any());
    }

    @Test
    void shouldThrowEmptyInputsExceptionIfInputRequestHasNoNumberForSubtractionOperation() {
        InputRequest inputRequest = new InputRequest(List.of());

        assertThatExceptionOfType(EmptyInputsException.class)
                .isThrownBy(() -> service.subtraction(inputRequest))
                .withMessage("No number to do the operation");
    }

    @Test
    void shouldDoMultiplication() {
        given(dataStore.findByNumbersAndOperation(anyString(), any())).willReturn(Optional.empty());
        String numbers = "2.0,3.0,4.0";
        Result result = getResultWithTestData(MULTIPLICATION, 24.0, numbers);
        given(dataStore.save(any())).willReturn(result);
        InputRequest inputRequest = new InputRequest(List.of(2.0, 3.0, 4.0));
        ResultResponse response = service.multiplication(inputRequest);

        assertThat(response).isNotNull();
        assertThat(response.result().doubleValue()).isEqualTo(24.0);

        verify(dataStore).save(resultArgumentCaptor.capture());

        assertThat(resultArgumentCaptor.getValue())
                .hasFieldOrPropertyWithValue("operation", Operation.MULTIPLICATION)
                .hasFieldOrPropertyWithValue("result", 24.0)
                .hasFieldOrPropertyWithValue("numbers", numbers);
    }

    @Test
    void shouldNotCalculateMultiplicationIfItCalculatedBefore() {
        String numbers = "2.0,3.0,4.0";
        given(dataStore.findByNumbersAndOperation(anyString(), any())).willReturn(Optional.of(getResultWithTestData(MULTIPLICATION, 24.0, numbers)));

        InputRequest inputRequest = new InputRequest(List.of(2.0, 3.0, 4.0));
        ResultResponse response = service.multiplication(inputRequest);

        assertThat(response).isNotNull();
        assertThat(response.result().doubleValue()).isEqualTo(24.0);
        verify(dataStore, times(0)).save(any());
    }

    @Test
    void shouldThrowEmptyInputsExceptionIfInputRequestHasNoNumberForMultiplicationOperation() {
        InputRequest inputRequest = new InputRequest(List.of());

        assertThatExceptionOfType(EmptyInputsException.class)
                .isThrownBy(() -> service.multiplication(inputRequest))
                .withMessage("No number to do the operation");
    }

    @Test
    void shouldDoDivision() {
        given(dataStore.findByNumbersAndOperation(anyString(), any())).willReturn(Optional.empty());
        String numbers = "30.0,2.0,3.0";
        Result result = getResultWithTestData(DIVISION, 5.0, numbers);
        given(dataStore.save(any())).willReturn(result);
        InputRequest inputRequest = new InputRequest(List.of(30.0, 2.0, 3.0));
        ResultResponse response = service.division(inputRequest);

        assertThat(response).isNotNull();
        assertThat(response.result().doubleValue()).isEqualTo(5.0);

        verify(dataStore).save(resultArgumentCaptor.capture());

        assertThat(resultArgumentCaptor.getValue())
                .hasFieldOrPropertyWithValue("operation", DIVISION)
                .hasFieldOrPropertyWithValue("result", 5.0)
                .hasFieldOrPropertyWithValue("numbers", numbers);
    }

    @Test
    void shouldNotCalculateDivisionIfItCalculatedBefore() {
        String numbers = "30.0,2.0,3.0";
        given(dataStore.findByNumbersAndOperation(anyString(), any())).willReturn(Optional.of(getResultWithTestData(DIVISION, 5.0, numbers)));

        InputRequest inputRequest = new InputRequest(List.of(30.0, 2.0, 3.0));
        ResultResponse response = service.division(inputRequest);

        assertThat(response).isNotNull();
        assertThat(response.result().doubleValue()).isEqualTo(5.0);
        verify(dataStore, times(0)).save(any());
    }

    @Test
    void shouldThrowNotArithmeticExceptionIfDivisorIsZero() {
        InputRequest inputRequest = new InputRequest(List.of(30.0, 2.0, 0.0));
        given(dataStore.findByNumbersAndOperation(anyString(), any())).willReturn(Optional.empty());

        assertThatExceptionOfType(ArithmeticException.class)
                .isThrownBy(() -> service.division(inputRequest))
                .withMessage("Divide by zero.");
    }

    @Test
    void shouldGetExistingResultById() {
        String numbers = "30.0,2.0,3.0";
        given(dataStore.findById(eq(1))).willReturn(Optional.of(getResultWithTestData(DIVISION, 5.0, numbers)));

        ResultResponse existingResult = service.getExistingResultById(1);

        assertThat(existingResult).isNotNull();
        verify(dataStore).findById(eq(1));
    }

    @Test
    void shouldGetExistingResultByNumbersAndOperation() {
        String numbers = "10.0,20.0,30.0";
        given(dataStore.findByNumbersAndOperation(eq(numbers), eq(ADDITION))).willReturn(Optional.of(getResultWithTestData(ADDITION, 60.0, numbers)));

        ResultResponse response = service.getExistingResultByInputsAndOperation(List.of(20.0, 10.0, 30.0), ADDITION);

        assertThat(response).isNotNull();
        assertThat(response.result()).isEqualTo(60.0);
        verify(dataStore).findByNumbersAndOperation(stringArgumentCaptor.capture(), operationArgumentCaptor.capture());
        assertThat(stringArgumentCaptor.getValue()).isEqualTo(numbers);
        assertThat(operationArgumentCaptor.getValue()).isEqualTo(ADDITION);
    }

    @Test
    void shouldThrowNotFoundExceptionIfResultDoesNotExist() {
        given(dataStore.findById(eq(1))).willReturn(Optional.empty());

        assertThatExceptionOfType(NotFoundException.class)
                .isThrownBy(() -> service.getExistingResultById(eq(1)))
                .withMessage("Result does not exist");
    }

    private Result getResultWithTestData(Operation operation, Double result, String numbers) {
        return Result.builder()
                .operation(operation)
                .result(result)
                .numbers(numbers)
                .build();
    }
}