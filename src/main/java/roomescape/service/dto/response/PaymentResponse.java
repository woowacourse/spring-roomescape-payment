package roomescape.service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigInteger;
import roomescape.domain.reservation.Payment;
import roomescape.domain.reservation.Reservation;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentResponse {

    private String paymentKey;
    private String orderId;
    private String totalAmount;

    protected PaymentResponse() {
    }

    public PaymentResponse(String paymentKey, String orderId, String totalAmount) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.totalAmount = totalAmount;
    }

    public void setPaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Payment toPayment(Reservation reservation) {
        return new Payment(reservation, paymentKey, orderId, new BigInteger(totalAmount));
    }
}
