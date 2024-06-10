package roomescape.payment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import roomescape.client.payment.dto.TossPaymentConfirmResponse;
import roomescape.registration.domain.reservation.domain.Reservation;

@Entity
public class Payment {

    private static final int NULL_ID = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Reservation reservation;

    @Column(nullable = false)
    private String paymentKey;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String orderName;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String requestedAt;

    @Column(nullable = false)
    private String approvedAt;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private String method;

    private String cancels;

    public Payment() {
    }

    public Payment(long id, Reservation reservation, String paymentKey, String type, String orderId, String orderName, String status,
                   String requestedAt, String approvedAt, BigDecimal totalAmount, String method, String cancels) {
        this.id = id;
        this.reservation = reservation;
        this.paymentKey = paymentKey;
        this.type = type;
        this.orderId = orderId;
        this.orderName = orderName;
        this.status = status;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
        this.totalAmount = totalAmount;
        this.method = method;
        this.cancels = cancels;
    }

    public Payment(TossPaymentConfirmResponse paymentResponse, Reservation reservation) {
        this.id = NULL_ID;
        this.reservation = reservation;
        this.paymentKey = paymentResponse.paymentKey();
        this.type = paymentResponse.type();
        this.orderId = paymentResponse.orderId();
        this.orderName = paymentResponse.orderName();
        this.status = paymentResponse.status();
        this.requestedAt = paymentResponse.requestedAt();
        this.approvedAt = paymentResponse.approvedAt();
        this.totalAmount = paymentResponse.totalAmount();
        this.method = paymentResponse.method();
        this.cancels = paymentResponse.cancels();
    }

    public List<PaymentCancelDto> convertJsonToCancelsDto() throws JsonProcessingException {
        return Arrays.asList(new ObjectMapper().readValue(cancels, PaymentCancelDto[].class));
    }

    public long getId() {
        return id;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getType() {
        return type;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public String getStatus() {
        return status;
    }

    public String getRequestedAt() {
        return requestedAt;
    }

    public String getApprovedAt() {
        return approvedAt;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public String getMethod() {
        return method;
    }

    public String getCancels() {
        return cancels;
    }
}
