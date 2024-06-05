package roomescape.client.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import roomescape.member.domain.Member;
import roomescape.payment.domain.Payment;
import roomescape.registration.domain.reservation.domain.Reservation;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentConfirmationFromTossDto(
        String paymentKey,
        String orderId,
        Long totalAmount,
        String status,
        LocalDateTime approvedAt
) {

    public Payment toPayment(Member member, Reservation reservation) {
        return new Payment(
                this.paymentKey,
                this.orderId,
                this.approvedAt,
                member,
                reservation
        );
    }
}
