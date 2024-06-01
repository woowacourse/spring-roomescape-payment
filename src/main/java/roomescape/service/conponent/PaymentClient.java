package roomescape.service.conponent;

import org.springframework.stereotype.Component;

@Component
public interface PaymentClient {

    void requestPayment(Object body);
}
