package roomescape.payment.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.common.exception.impl.BadRequestException;
import roomescape.payment.application.dto.PaymentDataRequest;
import roomescape.payment.application.dto.PaymentRequest;
import roomescape.payment.application.dto.PaymentResponse;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.repository.PaymentRepository;
import roomescape.reservation.application.dto.MemberReservationRequest;
import roomescape.reservation.domain.Reservation;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public void pay(
            final PaymentDataRequest paymentDataRequest,
            final MemberReservationRequest request,
            final Reservation reservation
    ) {
        validatePaymentData(request, paymentDataRequest);
        final PaymentRequest paymentRequest = new PaymentRequest(
                request.amount(), request.orderId(), request.paymentKey());
        final PaymentResponse paymentResponse = paymentClient.requestPayment(paymentRequest);
        final Payment payment = new Payment(
                paymentResponse.orderId(),
                paymentResponse.paymentKey(),
                paymentResponse.totalAmount(),
                reservation
        );
        paymentRepository.save(payment);
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
