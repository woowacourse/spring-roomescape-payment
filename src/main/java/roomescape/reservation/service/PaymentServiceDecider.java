package roomescape.reservation.service;

import roomescape.reservation.domain.PaymentType;
import roomescape.reservation.service.dto.PaymentRequest;

public interface PaymentServiceDecider {
    PaymentService<PaymentRequest> decide(PaymentType type);
}
