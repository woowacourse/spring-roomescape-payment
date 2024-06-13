package roomescape.infrastructure.tosspayments;

import io.netty.handler.timeout.TimeoutException;
import java.util.Base64;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import roomescape.dto.payment.PaymentCancelRequest;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.dto.payment.TossError;
import roomescape.exception.ExternalApiTimeoutException;
import roomescape.exception.TossClientException;
import roomescape.exception.TossServerException;

@Component
public class TossPaymentsClient {

    private static final String BASIC = "Basic ";
    private static final String API_DELIMITER = ":";

    private final String secretKey;
    private final String confirmUrl;
    private final String cancelUrl;
    private final WebClient webClient;
    private final Logger logger;

    public TossPaymentsClient(@Value("${api.toss.secret-key}") String secretKey,
                              @Value("${api.toss.url.confirm}") String confirmUrl,
                              @Value("${api.toss.url.cancel}") String cancelUrl,
                              WebClient webClient
    ) {
        this.secretKey = secretKey;
        this.confirmUrl = confirmUrl;
        this.cancelUrl = cancelUrl;
        this.webClient = webClient;
        this.logger = Logger.getLogger(getClass().getName());
    }

    public PaymentResponse requestPayment(PaymentRequest request) {
        try {
            return webClient.post()
                    .uri(confirmUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", createAuthorizationHeader())
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(PaymentResponse.class)
                    .doOnNext(response -> logger.severe(response.toString()))
                    .block();
        } catch (HttpClientErrorException e) {
            TossError tossError = e.getResponseBodyAs(TossError.class);
            throw new TossClientException(tossError);

        } catch (HttpServerErrorException e) {
            TossError tossError = e.getResponseBodyAs(TossError.class);
            throw new TossServerException(tossError);

        } catch (TimeoutException e) {
            throw new ExternalApiTimeoutException(e.getMessage());
        }
    }

    public void requestPaymentCancel(PaymentCancelRequest request) {
        String requestUrl = UriComponentsBuilder.fromUriString(cancelUrl)
                .buildAndExpand(request.paymentKey())
                .toUriString();

        try {
            webClient.post()
                    .uri(requestUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", createAuthorizationHeader())
                    .bodyValue(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException e) {
            TossError tossError = e.getResponseBodyAs(TossError.class);
            throw new TossClientException(tossError);

        } catch (HttpServerErrorException e) {
            TossError tossError = e.getResponseBodyAs(TossError.class);
            throw new TossServerException(tossError);

        } catch (TimeoutException e) {
            throw new ExternalApiTimeoutException(e.getMessage());
        }
    }

    private String createAuthorizationHeader() {
        return BASIC + Base64.getEncoder().encodeToString((secretKey + API_DELIMITER).getBytes());
    }
}
