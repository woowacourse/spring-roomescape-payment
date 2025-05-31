package roomescape.client;

import roomescape.client.dto.PaymentsConfirmRequest;
import roomescape.client.dto.PaymentsConfirmResponse;

public interface PaymentClient {
    PaymentsConfirmResponse confirmPayments(PaymentsConfirmRequest request);
}
