package roomescape.web.controller.response;

import roomescape.service.response.PaymentDto;

public record PaymentResponse(Long id, String paymentKey, String orderId, Long totalAmount) {

    public PaymentResponse(PaymentDto paymentDto) {
        this(paymentDto.id(), paymentDto.paymentKey(), paymentDto.orderId(), paymentDto.totalAmount());
    }
}
