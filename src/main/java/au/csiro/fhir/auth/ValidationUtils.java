/*
 * Copyright 2023 Commonwealth Scientific and Industrial Research
 * Organisation (CSIRO) ABN 41 687 119 230.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package au.csiro.fhir.auth;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

/**
 * Utility classes to facilitate JSR-380 based validation.
 *
 * @author Piotr Szul
 */
public final class ValidationUtils {

  private ValidationUtils() {
    // Utility class.
  }

  // We use the ParameterMessageInterpolator rather than the default one which depends on EL
  // implementation for message interpolation as it causes library conflicts in Databricks
  // environments.
  private static final ValidatorFactory DEFAULT_VALIDATION_FACTORY =
      Validation.byDefaultProvider()
          .configure()
          .messageInterpolator(new ParameterMessageInterpolator())
          .buildValidatorFactory();

  /**
   * Validates a bean annotated with JSR-380 constraints using the default validation factory.
   *
   * @param bean the bean to validate
   * @param <T> the type of the bean.
   * @return the set of violated constraints, empty if the bean is valid.
   */
  @Nonnull
  public static <T> Set<ConstraintViolation<T>> validate(@Nonnull final T bean) {
    final Validator validator = DEFAULT_VALIDATION_FACTORY.getValidator();
    return validator.validate(bean);
  }

  /**
   * Ensures that a bean annotated with JSR-380 constraints is valid. If validation with the default
   * validation factory results in any violation throws the {@link ConstraintViolationException}.
   *
   * @param bean the bean to validate
   * @param message the message to use as the title of the exception message.
   * @param <T> the type of the bean.
   * @return the valid bean.
   * @throws ConstraintViolationException if any constraints are violated.
   */
  @SuppressWarnings("UnusedReturnValue")
  @Nonnull
  public static <T> T ensureValid(@Nonnull final T bean, @Nonnull final String message)
      throws ConstraintViolationException {
    final Set<ConstraintViolation<T>> constraintViolations = validate(bean);
    if (!constraintViolations.isEmpty()) {
      failValidation(constraintViolations, message);
    }
    return bean;
  }

  /**
   * Fails with the {@link ConstraintViolationException} that includes the violated constraints and
   * the human-readable representation of them.
   *
   * @param constraintViolations the violation to include in the exception.
   * @param messageTitle the title of the error message.
   */
  public static void failValidation(
      @Nonnull final Set<? extends ConstraintViolation<?>> constraintViolations,
      @Nullable final String messageTitle)
      throws ConstraintViolationException {
    final String exceptionMessage =
        nonNull(messageTitle)
            ? messageTitle + "\n" + formatViolations(constraintViolations)
            : formatViolations(constraintViolations);
    throw new ConstraintViolationException(exceptionMessage, constraintViolations);
  }

  /**
   * Formats a set of {@link ConstraintViolation} to a human-readable string.
   *
   * @param constraintViolations the violations to include.
   * @return the human-readable representation of the violations.
   */
  @Nonnull
  public static String formatViolations(
      @Nonnull final Set<? extends ConstraintViolation<?>> constraintViolations) {
    return constraintViolations.stream()
        .filter(Objects::nonNull)
        .map(
            cv ->
                isNull(cv.getPropertyPath()) || cv.getPropertyPath().toString().isBlank()
                    ? cv.getMessage()
                    : cv.getPropertyPath() + ": " + cv.getMessage())
        .sorted()
        .collect(Collectors.joining("\n"));
  }

  /** Accumulates constraint violations and provides a fluent API to add violations. */
  @AllArgsConstructor(staticName = "of")
  public static class ViolationAccumulator {

    @Nonnull private final ConstraintValidatorContext context;

    @Getter private boolean valid = true;

    private ViolationAccumulator(@Nonnull final ConstraintValidatorContext context) {
      this.context = context;
    }

    /**
     * Checks the assertion and adds a violation if it is false.
     *
     * @param assertion the assertion to check.
     * @param message the message to add to the violation.
     * @param property the property to add to the violation.
     * @return the accumulator.
     */
    public ViolationAccumulator checkThat(
        final boolean assertion, @Nonnull final String message, @Nonnull final String property) {
      return checkThat(assertion, message, Optional.of(property));
    }

    /**
     * Checks the assertion and adds a violation if it is false.
     *
     * @param assertion the assertion to check.
     * @param message the message to add to the violation.
     * @return the accumulator.
     */
    public ViolationAccumulator checkThat(final boolean assertion, @Nonnull final String message) {
      return checkThat(assertion, message, Optional.empty());
    }

    /**
     * Checks the assertion and adds a violation if it is false.
     *
     * @param assertion the assertion to check.
     * @param message the message to add to the violation.
     * @param maybeProperty the optional maybeProperty to add to the violation.
     * @return the accumulator.
     */
    public ViolationAccumulator checkThat(
        boolean assertion,
        @Nonnull final String message,
        @Nonnull final Optional<String> maybeProperty) {
      if (!assertion) {
        return addViolation(message, maybeProperty);
      } else {
        return this;
      }
    }

    /**
     * Adds a violation with the given message and property.
     *
     * @param message the message to add to the violation.
     * @param property the property to add to the violation.
     * @return the accumulator.
     */
    public ViolationAccumulator addViolation(
        @Nonnull final String message, @Nonnull final Optional<String> property) {
      final ConstraintViolationBuilder builder =
          context.buildConstraintViolationWithTemplate(message);
      property.ifPresentOrElse(
          p -> builder.addPropertyNode(p).addConstraintViolation(),
          builder::addConstraintViolation);
      valid = false;
      return this;
    }

    /**
     * Creates new accumulator with the default violation disabled in the given context.
     *
     * @param context the context to add the violation to.
     * @return the accumulator.
     */
    public static ViolationAccumulator withNoDefault(
        @Nonnull final ConstraintValidatorContext context) {
      context.disableDefaultConstraintViolation();
      return new ViolationAccumulator(context);
    }
  }
}
