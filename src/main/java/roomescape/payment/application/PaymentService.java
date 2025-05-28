package roomescape.payment.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.common.exception.impl.BadRequestException;
import roomescape.payment.application.dto.PaymentDataRequest;
import roomescape.payment.application.dto.PaymentRequest;
import roomescape.payment.application.dto.PaymentResponse;
import roomescape.reservation.application.dto.MemberReservationRequest;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentClient paymentClient;

    public void createOrder(PaymentDataRequest paymentDataRequest, MemberReservationRequest request) {
        final PaymentRequest paymentRequest = new PaymentRequest(request.amount(), request.orderId(),
                request.paymentKey());
        validatePaymentData(request, paymentDataRequest);
        PaymentResponse paymentResponse = paymentClient.pay(paymentRequest);
    }

    private void validatePaymentData(final MemberReservationRequest request,
                                     final PaymentDataRequest paymentDataRequest) {
        if (!paymentDataRequest.orderId().equals(request.orderId())) {
            throw new BadRequestException("결제 주문번호가 일치하지 않습니다.");
        }
        if (!paymentDataRequest.amount().equals(request.amount())) {
            throw new BadRequestException("결제 금액이 일치하지 않습니다.");
        }
    }
}
