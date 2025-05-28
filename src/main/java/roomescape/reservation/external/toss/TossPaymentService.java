package roomescape.reservation.external.toss;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import roomescape.global.api.CustomRequestUri;
import roomescape.global.api.CustomRestClient;
import roomescape.global.api.TossApiErrorResponse;
import roomescape.global.api.TossAuthToken;
import roomescape.reservation.domain.PaymentInfo;
import roomescape.reservation.service.PaymentService;

@Service
public class TossPaymentService implements PaymentService {

    private final String tossSecretKey;
    private final String confirmUri;
    private final CustomRestClient customRestClient;

    public TossPaymentService(
            @Value("${api.toss.secret-key}") String tossSecretKey,
            @Value("${api.toss.payment.uri.confirm}") String confirmUri,
            final CustomRestClient customRestClient
    ) {
        this.tossSecretKey = tossSecretKey;
        this.confirmUri = confirmUri;
        this.customRestClient = customRestClient;
    }

    @Override
    public PaymentInfo paymentReservation(final PaymentConfirmRequest request) {
        CustomRequestUri customRequestUri = new CustomRequestUri(confirmUri);
        TossAuthToken tossAuthToken = new TossAuthToken(tossSecretKey);
        return customRestClient.post(
                customRequestUri,
                tossAuthToken,
                request,
                PaymentInfo.class,
                TossApiErrorResponse.class
        );
    }
}
