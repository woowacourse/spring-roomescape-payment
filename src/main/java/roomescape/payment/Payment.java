package roomescape.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import roomescape.reservation.Reservation;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long reservationId;
    private String paymentKey;
    private String orderId;
    private String orderName;
    private long totalAmount;
    private String method;
    private String status;
    private LocalDateTime requestedAt;
    private LocalDateTime approvedAt;
    private Long memberId;

    public Payment(PaymentConfirmResponse response,
                   Reservation reservation) {
        this.paymentKey = response.getPaymentKey();
        this.orderId = response.getOrderId();
        this.orderName = response.getOrderName();
        this.totalAmount = response.getTotalAmount();
        this.method = response.getMethod();
        this.status = response.getStatus();
        this.requestedAt = response.getRequestedAt();
        this.approvedAt = response.getApprovedAt();
        this.reservationId = reservation.getId();
        this.memberId = reservation.getMember().getId();
    }

    public Payment() {

    }
}
