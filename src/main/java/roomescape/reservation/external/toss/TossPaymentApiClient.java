package roomescape.reservation.external.toss;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import roomescape.global.api.AuthToken;
import roomescape.global.api.CustomRequestUri;
import roomescape.global.api.CustomRestClient;
import roomescape.reservation.external.toss.dto.PaymentConfirmRequest;
import roomescape.reservation.external.toss.dto.TossApiErrorResponse;
import roomescape.reservation.domain.PaymentInfo;
import roomescape.reservation.service.PaymentApiClient;

@Service
public class TossPaymentApiClient implements PaymentApiClient {

    private final String tossSecretKey;
    private final String confirmUri;
    private final CustomRestClient customRestClient;

    public TossPaymentApiClient(
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
        AuthToken tossAuthToken = new TossAuthToken(tossSecretKey);
        return customRestClient.post(
                customRequestUri,
                tossAuthToken,
                request,
                PaymentInfo.class,
                TossApiErrorResponse.class
        );
    }
}
