package roomescape.core.dto.auth;

public class PaymentAuthorizationResponse {

    private String paymentAuthorization;

    public PaymentAuthorizationResponse(String paymentAuthorization) {
        this.paymentAuthorization = paymentAuthorization;
    }

    public String getPaymentAuthorization() {
        return paymentAuthorization;
    }
}
