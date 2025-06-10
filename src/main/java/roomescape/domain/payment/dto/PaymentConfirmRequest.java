package roomescape.domain.payment.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TossPaymentConfirmRequest.class, name = "TOSS"),
})
@Schema(description = "결제 확인 요청 DTO")
public interface PaymentConfirmRequest {
}
