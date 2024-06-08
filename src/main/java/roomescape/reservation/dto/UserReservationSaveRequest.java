package roomescape.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import roomescape.member.domain.Member;
import roomescape.payment.dto.PaymentRequest;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;

public record UserReservationSaveRequest(
        @Schema(description = "예약 날짜 (현재 날짜 이후)", example = "2024-06-07")
        LocalDate date,

        @Schema(description = "예약 테마 id", example = "1")
        Long themeId,

        @Schema(description = "예약 시간 id", example = "1")
        Long timeId,

        @Schema(description = "결제 key", example = "테스트_결제키")
        String paymentKey,

        @Schema(description = "주문번호", example = "테스트_주문번호")
        String orderId,

        @Schema(description = "주문금액", example = "1000")
        BigDecimal amount
) {

    public Reservation toEntity(Member member, Theme theme, ReservationTime reservationTime, ReservationStatus reservationStatus) {
        return new Reservation(member, date, theme, reservationTime, reservationStatus);
    }

    public PaymentRequest extractPaymentRequest() {
        return new PaymentRequest(orderId, amount, paymentKey);
    }
}
