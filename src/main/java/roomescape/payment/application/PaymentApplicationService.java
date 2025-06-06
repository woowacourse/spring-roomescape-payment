package roomescape.payment.application;

import org.springframework.stereotype.Service;
import roomescape.payment.application.client.PaymentClient;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.ProductType;
import roomescape.payment.presentation.dto.request.PaymentApproveRequest;
import roomescape.payment.presentation.dto.response.PaymentApproveResponse;

import java.time.LocalDateTime;

@Service
public class PaymentApplicationService {

    private final PaymentDataService paymentDataService;
    private final PaymentClient paymentClient;

    public PaymentApplicationService(PaymentDataService paymentDataService, final PaymentClient paymentClient) {
        this.paymentDataService = paymentDataService;
        this.paymentClient = paymentClient;
    }

    public Payment approveReservationPayment(final PaymentApproveRequest paymentApproveRequest, Long reservationId) {
        PaymentApproveResponse paymentApproveResponse = paymentClient.approvePayment(paymentApproveRequest);
        Payment payment = new Payment(paymentApproveResponse.paymentKey(), paymentApproveResponse.orderId(), paymentApproveResponse.totalAmount(),
                ProductType.RESERVATION, reservationId, LocalDateTime.now());
        return paymentDataService.save(payment);
    }

    public Payment findReservationPayment(Long reservationId) {
        return paymentDataService.findByProductTypeAndProductId(ProductType.RESERVATION, reservationId);
    }
}
