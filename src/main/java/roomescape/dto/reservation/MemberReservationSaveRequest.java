package roomescape.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import roomescape.dto.MemberResponse;
import roomescape.dto.payment.PaymentRequest;

import java.time.LocalDate;

public record MemberReservationSaveRequest(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate date,
        Long timeId,
        Long themeId,
        String paymentKey,
        String orderId,
        Integer amount
) {

    public ReservationSaveRequest generateReservationSaveRequest(MemberResponse memberResponse) {
        return new ReservationSaveRequest(memberResponse.id(), date, timeId, themeId);
    }

    public PaymentRequest toPaymentRequest() {
        return new PaymentRequest(orderId, amount, paymentKey);
    }
}
