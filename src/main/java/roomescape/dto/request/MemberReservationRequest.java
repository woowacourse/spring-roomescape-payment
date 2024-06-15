package roomescape.dto.request;

import jakarta.validation.constraints.Max;
import java.math.BigDecimal;
import java.time.LocalDate;
import roomescape.domain.Member;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationStatus;
import roomescape.domain.Theme;
import roomescape.domain.TimeSlot;

public record MemberReservationRequest(
        LocalDate date, 
        Long timeId, 
        Long themeId, 
        String paymentKey, 
        String orderId,
        @Max(1000000000)
        BigDecimal amount) {

    public MemberReservationRequest {
        isValid(date, timeId, themeId);
    }

    public Reservation toEntity(Member member, TimeSlot time, Theme theme) {
        return new Reservation(null, member, date, time, theme, ReservationStatus.BOOKING, paymentKey);
    }

    private void isValid(LocalDate date, Long timeId, Long themeId) {
        if (date == null || date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 예약 날짜입니다.");
        }

        if (timeId == null) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 예약 시간입니다.");
        }

        if (themeId == null) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 테마 입니다.");
        }
    }
}
