package roomescape.reservation.external.toss;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TossPaymentServiceImpl implements TossPaymentService {

    private final String tossSecretKey;
    private final TossPaymentRestClient tossPaymentRestClient;

    public TossPaymentServiceImpl(
            @Value("${api.toss.secret-key}") String tossSecretKey,
            final TossPaymentRestClient tossPaymentRestClient
    ) {
        this.tossSecretKey = tossSecretKey;
        this.tossPaymentRestClient = tossPaymentRestClient;
    }

    public TossPaymentResponse paymentReservation(final TossPaymentRequest tossPaymentRequest) {
        TossAuthToken tossAuthToken = new TossAuthToken(tossSecretKey);
        return tossPaymentRestClient.post(tossAuthToken, tossPaymentRequest);
    }
}
