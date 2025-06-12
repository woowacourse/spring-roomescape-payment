package roomescape.infrastructure.payment.toss;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;
import roomescape.application.support.TossPaymentWithHttpClient;
import roomescape.dto.request.TossPaymentConfirmDto;
import roomescape.dto.response.TossPaymentConfirmResponseDto;

@Slf4j
public class TossPaymentWithRestClient implements TossPaymentWithHttpClient {

    public static final String IDEMPOTENCY_KEY = "Idempotency-Key";
    private final RestClient restClient;

    public TossPaymentWithRestClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public TossPaymentConfirmResponseDto requestConfirmation(
            TossPaymentConfirmDto tossPaymentConfirmDto,
            String requestKey) {

        long start = System.currentTimeMillis();
        log.info("[EXTERNAL API] [TOSS PAYMENT REQUEST] POST /confirm - body: {}, idempotencyKey: {}",
                tossPaymentConfirmDto,
                requestKey);

        try {
            TossPaymentConfirmResponseDto response = restClient.post()
                    .uri("/confirm")
                    .header(IDEMPOTENCY_KEY, requestKey)
                    .body(tossPaymentConfirmDto)
                    .retrieve()
                    .body(TossPaymentConfirmResponseDto.class);

            long end = System.currentTimeMillis();
            log.info("[EXTERNAL API] [TOSS PAYMENT RESPONSE] status=200, body={}, took={}ms", response, end - start);

            return response;

        } catch (Exception e) {
            long end = System.currentTimeMillis();
            log.info("[EXTERNAL API] [TOSS PAYMENT RESPONSE - ERROR] POST /confirm failed - took={}ms", end - start);

            throw e;
        }
    }
}


