package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.payment.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
}
