package roomescape.reservation.controller.request;

import jakarta.validation.constraints.NotNull;

public record PaymentInfoRequest(@NotNull(message = "결제 키값이 필요합니다.") String paymentKey,
                                 @NotNull(message = "주문 번호가 필요합니다.") String orderId,
                                 @NotNull(message = "금액이 필요합니다.") Long amount,
                                 @NotNull(message = "결제 타입이 필요합니다.") String paymentType) {
}
