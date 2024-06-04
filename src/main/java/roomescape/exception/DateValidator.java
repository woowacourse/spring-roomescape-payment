package roomescape.exception;

import java.time.temporal.ChronoUnit;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import roomescape.reservation.dto.request.ReservationSearchRequest;

public class DateValidator implements ConstraintValidator<ValidDate, ReservationSearchRequest> {
    public static final int LIMIT_DAY = 30;

    @Override
    public boolean isValid(ReservationSearchRequest request, ConstraintValidatorContext context) {
        return ChronoUnit.DAYS.between(request.dateFrom(), request.dateTo()) <= LIMIT_DAY;
    }
}
