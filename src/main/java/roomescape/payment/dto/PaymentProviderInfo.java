package roomescape.payment.dto;

public record PaymentProviderInfo(
        String secretKey,
        String baseUrl,
        int connectTimeout,
        int readTimeout
) {
}
