package roomescape.client;

import roomescape.client.dto.PaymentConfirmResultDto;
import roomescape.service.dto.PaymentConfirmDto;

public interface PaymentClient {

    PaymentConfirmResultDto confirmPayment(PaymentConfirmDto requestDto);
}
