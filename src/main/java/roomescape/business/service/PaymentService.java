package roomescape.business.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.business.dto.PaymentApproveRequestDto;
import roomescape.business.model.entity.Payment;
import roomescape.business.model.entity.Reservation;
import roomescape.business.model.repository.PaymentRepository;
import roomescape.infrastructure.payment.PaymentApproveResponseDto;
import roomescape.infrastructure.payment.TossPaymentClient;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final TossPaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public void pay(Reservation reservation, PaymentApproveRequestDto paymentApproveRequestDto) {
        PaymentApproveResponseDto paymentApproveResponseDto = paymentClient.approvePayment(paymentApproveRequestDto);
        Payment payment = Payment.create(reservation, paymentApproveResponseDto);
        paymentRepository.save(payment);
    }
}
