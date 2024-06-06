package roomescape.payment.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.exception.custom.BadRequestException;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.infra.PaymentClient;

import java.util.Objects;

@Service
public class PaymentService {

    private final PaymentClient paymentClient;

    public PaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    @Transactional
    public void purchase(PaymentRequest paymentRequest, Long amount) {
        PaymentResponse paymentResponse = paymentClient.confirm(paymentRequest);

        if (!Objects.equals(paymentResponse.totalAmount(), amount)) {
            throw new BadRequestException("결제 금액이 잘못되었습니다.");
        }
    }
}
