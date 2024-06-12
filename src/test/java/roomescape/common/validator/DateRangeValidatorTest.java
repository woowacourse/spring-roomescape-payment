package roomescape.common.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import roomescape.reservation.controller.dto.request.ReservationSearchCondRequest;

class DateRangeValidatorTest {

    @DisplayName("기간이 30일을 넘지 않으면 true, 넘으면 false를 반환한다.")
    @ParameterizedTest
    @CsvSource({"'2024-05-01', '2024-05-31', true", "'2024-05-01', '2024-06-01', false"})
    void isValid(LocalDate dateFrom, LocalDate dateTo, boolean expected) {
        ReservationSearchCondRequest reservationSearchCondRequest = new ReservationSearchCondRequest(
                1L, 1L, dateFrom, dateTo
        );

        DateSearchPeriodValidator validator = new DateSearchPeriodValidator();
        boolean valid = validator.isValid(
                reservationSearchCondRequest,
                mock(ConstraintValidatorContext.class)
        );

        assertThat(valid).isEqualTo(expected);
    }
}