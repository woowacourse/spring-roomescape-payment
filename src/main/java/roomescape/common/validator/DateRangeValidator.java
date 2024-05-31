package roomescape.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.temporal.ChronoUnit;
import roomescape.reservation.controller.dto.request.ReservationSearchCondRequest;

public class DateRangeValidator implements ConstraintValidator<ValidDateSearchPeriod, ReservationSearchCondRequest> {

    public static final int LIMIT_DAY = 30;

    @Override
    public boolean isValid(ReservationSearchCondRequest request, ConstraintValidatorContext context) {
        return ChronoUnit.DAYS.between(request.dateFrom(), request.dateTo()) <= LIMIT_DAY;
    }
}
