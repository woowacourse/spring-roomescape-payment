package roomescape.reservation.service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import roomescape.reservation.domain.PaymentType;
import roomescape.reservation.service.dto.PaymentRequest;

@Component
public class PaymentServiceDeciderImpl implements PaymentServiceDecider {
    private final Map<PaymentType, PaymentService<? extends PaymentRequest>> paymentServices;

    public PaymentServiceDeciderImpl(List<PaymentService<? extends PaymentRequest>> paymentServices) {
        this.paymentServices = paymentServices.stream()
                .collect(Collectors.toMap(PaymentService::getType, Function.identity()));
    }

    public PaymentService decide(PaymentType type) {
        return paymentServices.get(type);
    }
}
