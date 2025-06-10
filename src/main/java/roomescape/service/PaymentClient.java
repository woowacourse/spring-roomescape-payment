package roomescape.service;

import roomescape.infrastructure.dto.PaymentConfirmResultDto;
import roomescape.service.dto.PaymentConfirmDto;

public interface PaymentClient {

    PaymentConfirmResultDto confirmPayment(PaymentConfirmDto requestDto);
}
