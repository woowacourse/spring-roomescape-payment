package roomescape.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import roomescape.dto.payment.PaymentRequest;

public record UserReservationRequest(
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        Long timeId,
        Long themeId,
        String paymentKey,
        String orderId,
        BigDecimal amount,
        String paymentType
) {

    public UserReservationRequest {
        Objects.requireNonNull(timeId);
        Objects.requireNonNull(themeId);
        Objects.requireNonNull(paymentKey);
        Objects.requireNonNull(orderId);
        Objects.requireNonNull(paymentType);
    }

    public ReservationRequest toReservationRequest(Long memberId) {
        return new ReservationRequest(
                date,
                timeId,
                themeId,
                memberId
        );
    }

    public PaymentRequest toPaymentRequest() {
        return new PaymentRequest(
                paymentKey,
                orderId,
                amount,
                paymentType
        );
    }
}
