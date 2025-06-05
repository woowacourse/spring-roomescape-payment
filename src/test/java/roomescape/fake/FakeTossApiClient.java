package roomescape.fake;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestClient;
import roomescape.reservation.external.toss.TossPaymentRequest;
import roomescape.reservation.external.toss.TossPaymentResponse;
import roomescape.reservation.external.toss.TossApiClient;

public class FakeTossApiClient extends TossApiClient {

    public FakeTossApiClient() {
        super(new ObjectMapper(), RestClient.create());
    }

    public TossPaymentResponse requestPayment(final TossPaymentRequest tossPaymentRequest) {
        return new TossPaymentResponse(tossPaymentRequest.orderId(), tossPaymentRequest.amount(),
                tossPaymentRequest.paymentKey());
    }
}
