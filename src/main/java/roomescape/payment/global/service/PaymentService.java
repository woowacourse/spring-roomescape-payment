package roomescape.payment.global.service;

import roomescape.payment.global.domain.dto.PaymentRequestDto;
import roomescape.payment.global.domain.dto.PaymentResponseDto;

public interface PaymentService {

    PaymentResponseDto approve(PaymentRequestDto request);
}
