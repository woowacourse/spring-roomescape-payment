package roomescape.payment.infrastructure;

public record PaymentGatewayProperty(
        String company,
        String secretKey,
        String baseUrl,
        long readTimeout
) {
}
