package se.tele2.calculator.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import se.tele2.calculator.exception.EmptyInputsException;
import se.tele2.calculator.exception.NotFoundException;
import se.tele2.calculator.model.InputRequest;
import se.tele2.calculator.model.Operation;
import se.tele2.calculator.model.ResultResponse;
import se.tele2.calculator.service.CalculatorService;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CalculatorController.class)
class CalculatorControllerTest {

    @MockBean
    private CalculatorService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCalculateAdditionSuccessfully() throws Exception {
        InputRequest request = new InputRequest(List.of(10.0, 20.0));
        given(service.calculate(eq(request), eq(Operation.ADDITION))).willReturn(new ResultResponse(30.0));

        mockMvc.perform(post("/addition")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", equalTo(30.0)));
    }

    @Test
    void shouldCalculateSubtractionSuccessfully() throws Exception {
        InputRequest request = new InputRequest(List.of(20.0, 10.0));
        given(service.calculate(eq(request), eq(Operation.SUBTRACTION))).willReturn(new ResultResponse(10.0));

        mockMvc.perform(post("/subtraction")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", equalTo(10.0)));
    }

    @Test
    void shouldCalculateMultiplicationSuccessfully() throws Exception {
        InputRequest request = new InputRequest(List.of(2.0, 3.0));
        given(service.calculate(eq(request), eq(Operation.MULTIPLICATION))).willReturn(new ResultResponse(6.0));

        mockMvc.perform(post("/multiplication")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", equalTo(6.0)));
    }

    @Test
    void shouldCalculateDivisionSuccessfully() throws Exception {
        InputRequest request = new InputRequest(List.of(10.0, 2.0));
        given(service.calculate(eq(request), eq(Operation.DIVISION))).willReturn(new ResultResponse(5.0));

        mockMvc.perform(post("/division")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", equalTo(5.0)));
    }

    @Test
    void shouldResponse403WhenDivisorIsZeroInDivision() throws Exception {
        InputRequest request = new InputRequest(List.of(10.0, 0.0));
        given(service.calculate(eq(request), eq(Operation.DIVISION))).willThrow(new ArithmeticException("Divide by zero."));

        mockMvc.perform(post("/division")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", equalTo("FORBIDDEN")))
                .andExpect(jsonPath("$.message", equalTo("Divide by zero.")));
    }

    @Test
    void shouldResponse403WhenInputIsEmpty() throws Exception {
        InputRequest request = new InputRequest(List.of());
        given(service.calculate(eq(request), eq(Operation.DIVISION))).willThrow(new EmptyInputsException("No number to do the operation"));

        mockMvc.perform(post("/division")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", equalTo("FORBIDDEN")))
                .andExpect(jsonPath("$.message", equalTo("No number to do the operation")));
    }

    @Test
    void shouldReturnExistingResultById() throws Exception {
        given(service.getExistingResultById(eq(1))).willReturn(new ResultResponse(5.0));

        mockMvc.perform(get("/existingresult/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", equalTo(5.0)));
    }

    @Test
    void shouldReturnExistingResultByNumbersAndOperation() throws Exception {
        given(service.getExistingResultByInputsAndOperation(anyList(), any())).willReturn(new ResultResponse(60.0));

        String numbers = Arrays.stream(new Double[]{10.0, 20.0, 30.0})
                .map(Objects::toString)
                .collect(Collectors.joining(","));

        mockMvc.perform(get("/existingresult/{numbers}/{operation}", numbers, Operation.ADDITION)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", equalTo(60.0)));
    }

    @Test
    void shouldResponse404WhenResultDoesNotExist() throws Exception {
        given(service.getExistingResultById(eq(1))).willThrow(new NotFoundException("Result does not exist"));

        mockMvc.perform(get("/existingresult/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", equalTo("NOT_FOUND")))
                .andExpect(jsonPath("$.message", equalTo("Result does not exist")));
    }
}