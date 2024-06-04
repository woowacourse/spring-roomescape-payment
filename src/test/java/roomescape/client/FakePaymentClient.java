package roomescape.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.reservation.dto.request.PaymentRequest;

public class FakePaymentClient extends PaymentRestClient {

    public FakePaymentClient(ObjectMapper mapper) {
        super(mapper);
    }

    @Override
    public void payForReservation(String authorization, PaymentRequest paymentRequest) {
        if (authorization.isBlank()) {
            throw new PaymentException(new TossErrorResponse("TEST_EXCEPTION", "빈값은 올 수 없다.", ""));
        }
    }

}
