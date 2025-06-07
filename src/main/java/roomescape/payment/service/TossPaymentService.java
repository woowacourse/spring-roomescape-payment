package roomescape.payment.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.PaymentRequestDto;
import roomescape.payment.dto.PaymentResponseDto;
import roomescape.payment.external.TossRestClient;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;

@Service
@AllArgsConstructor
public class TossPaymentService implements PaymentService {

    private final TossRestClient tossApiClient;
    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResponseDto approve(PaymentRequestDto request, Reservation reservation) {
        PaymentResponseDto paymentResponseDto = tossApiClient.confirmPayment(request);
        Payment payment = Payment.of(paymentResponseDto, reservation);
        Payment savedPayment = paymentRepository.save(payment);
        return PaymentResponseDto.of(savedPayment);
    }
}
