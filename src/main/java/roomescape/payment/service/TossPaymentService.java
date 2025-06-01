package roomescape.payment.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.common.exception.BadRequestException;
import roomescape.common.exception.CustomException;
import roomescape.common.exception.vo.ErrorCode;
import roomescape.payment.repository.PaymentRepository;
import roomescape.payment.service.dto.ConfirmPaymentRequest;
import roomescape.payment.service.dto.ConfirmPaymentResponse;
import roomescape.payment.service.dto.PaymentFailure;

import java.util.List;

@Service
public class TossPaymentService {
    private static final List<String> IGNORE_CODES = List.of(
            "INCORRECT_BASIC_AUTH_FORMAT",
            "INVALID_API_KEY",
            "INVALID_AUTHORIZE_AUTH"
    );

    private final RestClient restClient;
    private final PaymentRepository paymentRepository;

    public TossPaymentService(RestClient restClient, PaymentRepository paymentRepository) {
        this.restClient = restClient;
        this.paymentRepository = paymentRepository;
    }

    // TODO: 결제 실패시 환불
    public ConfirmPaymentResponse postConfirmPayment(ConfirmPaymentRequest paymentRequest) {
        ConfirmPaymentResponse paymentResponse = restClient.post()
                .uri("/confirm")
                .body(paymentRequest)
                .retrieve()
                .body(ConfirmPaymentResponse.class);

        handlePaymentResponse(paymentResponse);

        paymentRepository.save(paymentResponse.toEntity());
        return paymentResponse;
    }

    private void handlePaymentResponse(ConfirmPaymentResponse response) {
        if (response.failure() == null) {
            return ;
        }
        PaymentFailure failure = response.failure();
        if (IGNORE_CODES.contains(failure.code())) {
            // TODO: failure.message() Logging
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }
        throw new BadRequestException(failure.message());
    }
}
