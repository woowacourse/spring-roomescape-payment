package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;
import roomescape.member.domain.Member;
import roomescape.payment.PaymentType;
import roomescape.payment.dto.PaymentCreateRequest;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.theme.domain.Theme;
import roomescape.time.domain.ReservationTime;

public record ReservationCreateRequest(
        @JsonFormat(pattern = "yyyy-MM-dd")
        @Schema(description = "예약 날짜", example = "2024-06-22")
        LocalDate date,
        @Schema(description = "예약 시간 id", example = "1")
        Long timeId,
        @Schema(description = "예약 테마 id", example = "1")
        Long themeId,
        @Schema(description = "결제 키", example = "tgen_20240528211")
        String paymentKey,
        @Schema(description = "주문 번호", example = "MC40MTMwMTk0ODU0ODU4")
        String orderId,
        @Schema(description = "결제 금액", example = "128000")
        BigDecimal amount,
        @Schema(description = "결제 타입", example = "NORMAL")
        PaymentType paymentType
) {
    public Reservation createReservation(Member member, ReservationTime time, Theme theme) {
        return new Reservation(member, date, time, theme, ReservationStatus.RESERVED);
    }

    public PaymentCreateRequest createPaymentRequest(Reservation reservation) {
        return new PaymentCreateRequest(paymentKey, orderId, amount, reservation);
    }
}
