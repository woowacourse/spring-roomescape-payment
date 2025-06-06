package roomescape.payment.service;

import roomescape.payment.dto.PaymentRequestDto;
import roomescape.payment.dto.PaymentResponseDto;

public interface PaymentService {

    PaymentResponseDto approve(PaymentRequestDto request);
}
