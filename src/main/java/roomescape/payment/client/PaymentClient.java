package roomescape.payment.client;

import org.springframework.web.bind.annotation.RequestBody;
import roomescape.payment.dto.request.ConfirmPaymentRequest;
import roomescape.payment.model.Payment;

public interface PaymentClient {

    Payment confirm(@RequestBody ConfirmPaymentRequest confirmPaymentRequest);
}
