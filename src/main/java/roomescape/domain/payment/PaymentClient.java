package roomescape.domain.payment;

import java.util.Base64;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.domain.Member;
import roomescape.dto.PaymentApproveRequest;

@Component
public class PaymentClient {
    private final String apiUri;
    private final String approveSecretKey;
    private final RestClient restClient;
    private final PaymentApiResponseErrorHandler errorHandler;


    public PaymentClient(
            @Value("${payment.approve.base-url}") String baseUrl,
            @Value("${payment.approve.api-uri}") String apiUri,
            @Value("${payment.approve.key}") String approveSecretKey,
            PaymentApiResponseErrorHandler errorHandler) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.apiUri = apiUri;
        this.approveSecretKey = approveSecretKey;
        this.errorHandler = errorHandler;
    }

    public Payment approve(PaymentApproveRequest paymentApproveRequest, Member member) {
        String encryptedKey = Base64.getEncoder().encodeToString(approveSecretKey.getBytes());
        ApproveApiResponse response = Optional.ofNullable(restClient.post()
                        .uri(apiUri)
                        .header(HttpHeaders.AUTHORIZATION, "Basic " + encryptedKey)
                        .body(paymentApproveRequest)
                        .retrieve()
                        .onStatus(errorHandler)
                        .body(ApproveApiResponse.class))
                .orElseThrow(() -> new ApiCallException("알 수 없는 오류가 발생했습니다."));
        return new Payment(response.orderId(), response.paymentKey(), response.amount(), member);
    }
}
