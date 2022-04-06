package se.tele2.calculator.model;

import javax.validation.constraints.NotNull;
import java.util.List;

public record InputRequest(@NotNull List<Double> inputs) {
}
