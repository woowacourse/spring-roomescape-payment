package roomescape.service.dto.response;


public record PaymentCancelRequest(
        String paymentKey,
        PaymentCancelRequestBody cancelRequestBody
) {

    public static PaymentCancelRequest from(PaymentResponse paymentResponse) {
        return new PaymentCancelRequest(
                paymentResponse.paymentKey(),
                new PaymentCancelRequestBody("서버 에러")
        );
    }

    private static String extractPaymentKey(String paymentKey) {
        return paymentKey.substring("tgen_".length());
    }
}
