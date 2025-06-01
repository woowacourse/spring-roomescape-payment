package roomescape.payment.processor;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import roomescape.payment.processor.toss.TossPaymentConfirmRequest;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TossPaymentConfirmRequest.class, name = "TOSS"),
})
public interface PaymentConfirmRequest {
}
