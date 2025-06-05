package roomescape.domain.reservation.exception;

import java.time.LocalDate;
import roomescape.infrastructure.exception.DomainRuleException;

public class PastDateException extends DomainRuleException {
    public PastDateException(final LocalDate date) {
        super("과거 시간은 예약 등록을 할 수 없습니다. date = " + date);
    }
}
