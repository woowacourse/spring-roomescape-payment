package roomescape.reservation.model.vo;

import lombok.Builder;

//TODO : 클래스명 변경
@Builder
public record PaymentInfo(
        String paymentKey,
        String orderId,
        Long amount
) {
}
