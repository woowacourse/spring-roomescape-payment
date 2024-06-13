package roomescape.reservation.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String paymentKey;
    @NotNull
    private String orderId;
    @NotNull
    private String orderName;
    @NotNull
    private String method;
    @NotNull
    private Long totalAmount;
    @NotNull
    private String status;
    @NotNull
    private String requestedAt;
    @NotNull
    private String approvedAt;

    public Payment() {
    }

    public Payment(Long id, String paymentKey, String orderId, String orderName, String method, Long totalAmount,
                   String status, String requestedAt, String approvedAt) {
        this.id = id;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.orderName = orderName;
        this.method = method;
        this.totalAmount = totalAmount;
        this.status = status;
        this.requestedAt = requestedAt;
        this.approvedAt = approvedAt;
    }

    public Payment(String paymentKey, String orderId, String orderName, String method, Long totalAmount,
                   String status, String requestedAt, String approvedAt) {
        this(null, paymentKey, orderId, orderName, method, totalAmount, status, requestedAt, approvedAt);
    }

    public static Payment admin() {
        return new Payment("adminPaymentKey",
                "adminOrderId",
                "adminOrderName",
                "adminMethod",
                1000L,
                "adminStatus",
                "adminRequestedAt",
                "adminApprovedAt");
    }

    public Long getId() {
        return id;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public String getMethod() {
        return method;
    }

    public Long getTotalAmount() {
        return totalAmount;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", paymentKey='" + paymentKey + '\'' +
                ", orderId='" + orderId + '\'' +
                ", orderName='" + orderName + '\'' +
                ", method='" + method + '\'' +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", requestedAt='" + requestedAt + '\'' +
                ", approvedAt='" + approvedAt + '\'' +
                '}';
    }

    public boolean hasSameAmount(Long amount) {
        return Objects.equals(totalAmount, amount);
    }
}
