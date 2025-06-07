package roomescape.application.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import roomescape.application.request.PaymentInfo;

@Getter
public class PaymentRequestedEvent extends ApplicationEvent {

    private final Long paymentId;
    private final PaymentInfo paymentInfo;

    public PaymentRequestedEvent(Object source, Long paymentId, PaymentInfo paymentInfo) {
        super(source);
        this.paymentId = paymentId;
        this.paymentInfo = paymentInfo;
    }
}
