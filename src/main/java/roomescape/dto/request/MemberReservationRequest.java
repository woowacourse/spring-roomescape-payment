package roomescape.dto.request;

import roomescape.domain.*;

import java.time.LocalDate;

public record MemberReservationRequest(LocalDate date, Long timeId, Long themeId, String paymentKey, String orderId,
                                       Long amount) {

    public MemberReservationRequest {
        isValid(date, timeId, themeId, paymentKey, orderId, amount);
    }

    public Reservation toEntity(Member member, TimeSlot time, Theme theme) {
        return new Reservation(member, date, time, theme, ReservationStatus.BOOKING);
    }

    private void isValid(LocalDate date, Long timeId, Long themeId, String paymentKey, String orderId, Long amount) {
        if (date == null || date.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 예약 날짜입니다.");
        }

        if (timeId == null) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 예약 시간입니다.");
        }

        if (themeId == null) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 테마 입니다.");
        }

        if (paymentKey == null) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 paymentKey입니다.");
        }

        if (orderId == null) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 orderId입니다.");
        }

        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("[ERROR] 올바르지 않은 amount입니다.");
        }
    }
}
