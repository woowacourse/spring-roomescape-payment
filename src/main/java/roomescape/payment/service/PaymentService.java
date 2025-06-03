package roomescape.payment.service;

import roomescape.payment.domain.dto.PaymentRequestDto;
import roomescape.payment.domain.dto.PaymentResponseDto;

public interface PaymentService {

    PaymentResponseDto approve(PaymentRequestDto request);
}
