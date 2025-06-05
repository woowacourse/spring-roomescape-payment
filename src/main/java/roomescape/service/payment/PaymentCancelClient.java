package roomescape.service.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.dto.response.TossPaymentResponse;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Base64;
import java.util.Map;
import java.util.Queue;

@Component
public class PaymentCancelClient {

    private final static Queue<CancelData> cancelQueue = new ArrayDeque<>();

    private final RestClient restClient;
    private final String widgetSecretKey;
    private final String paymentCancelUrl;

    public PaymentCancelClient(
            @Value("${toss.payments.base-url}") String baseUrl,
            @Value("${toss.payments.widget-secret-key}") String widgetSecretKey,
            @Value("${toss.payments.payment-cancel-url}") String paymentCancelUrl,
            TimeoutClientHttpRequestFactory requestFactory
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
        this.widgetSecretKey = widgetSecretKey;
        this.paymentCancelUrl = paymentCancelUrl;
    }

    public void cancelIdempotently(String paymentKey, String idempotencyKey) {
        restClient.post().uri(paymentCancelUrl, paymentKey)
                .header("Authorization", getAuthorizations())
                .header("Idempotency-Key", idempotencyKey)
                .accept(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "cancelReason", "클라이언트 로직 문제"
                ))
                .retrieve()
                .body(TossPaymentResponse.class);
    }

    private String getAuthorizations() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }

    public void addToCancelSchedule(String paymentKey, String idempotencyKey) {
        cancelQueue.add(new CancelData(paymentKey, idempotencyKey));
    }

    @Scheduled(cron = "0 */5 * * * *")
    private void cancelCronJob() {
        if (cancelQueue.isEmpty()) {
            return;
        }
        
        final Queue<CancelData> remainQueue = new ArrayDeque<>();
        for (CancelData cancelData : cancelQueue) {
            try {
                cancelIdempotently(cancelData.paymentKey, cancelData.idempotencyKey);
            } catch (Exception e) {
                remainQueue.add(cancelData);
            }
        }
        cancelQueue.clear();
        cancelQueue.addAll(remainQueue);
    }

    private record CancelData(
            String paymentKey,
            String idempotencyKey
    ) {
    }
}
