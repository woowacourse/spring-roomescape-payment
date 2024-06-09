package roomescape.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import roomescape.domain.payment.Payment;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TossPaymentResponse(
        String paymentKey,

        @JsonProperty("totalAmount")
        int amount
) {

    public Payment toPayment() {
        return new Payment(paymentKey, amount);
    }
}
