package roomescape.reservation.service;

import org.springframework.stereotype.Service;
import roomescape.reservation.external.toss.TossApiClient;
import roomescape.reservation.external.toss.TossPaymentRequest;
import roomescape.reservation.external.toss.TossPaymentResponse;
import roomescape.reservation.repository.TossPaymentRepository;

@Service
public class TossPaymentCommandService {
    private final TossPaymentRepository tossPaymentRepository;
    private final TossApiClient tossApiClient;

    public TossPaymentCommandService(final TossPaymentRepository tossPaymentRepository,
                                     final TossApiClient tossApiClient) {
        this.tossPaymentRepository = tossPaymentRepository;
        this.tossApiClient = tossApiClient;
    }

    public void createTossPayment(final TossPaymentRequest tossPaymentRequest) {
        TossPaymentResponse tossPaymentResponse = tossApiClient.requestPayment(tossPaymentRequest);
        tossPaymentRepository.save(tossPaymentResponse.toEntity());
    }
}
