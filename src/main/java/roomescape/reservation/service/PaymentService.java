package roomescape.reservation.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import roomescape.exception.PaymentException;
import roomescape.reservation.client.TossRestClient;
import roomescape.reservation.dto.PaymentApiResponse;
import roomescape.reservation.dto.PaymentRequest;
import roomescape.reservation.model.Payment;
import roomescape.reservation.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TossRestClient tossRestClient;

    public PaymentService(PaymentRepository paymentRepository, TossRestClient tossRestClient) {
        this.paymentRepository = paymentRepository;
        this.tossRestClient = tossRestClient;
    }

    public Payment requestTossPayment(PaymentRequest paymentRequest) {
        PaymentApiResponse paymentApiResponse = tossRestClient.confirmPayment(paymentRequest);
        if (paymentApiResponse == null) {
            throw new PaymentException("응답 정보가 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        Payment payment = paymentApiResponse.toEntity();

        return paymentRepository.save(payment);
    }
}
