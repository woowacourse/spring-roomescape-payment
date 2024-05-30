package roomescape.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import roomescape.controller.dto.PaymentErrorMessageResponse;
import roomescape.global.exception.RoomescapeException;
import roomescape.service.config.TossPaymentConfigProperties;
import roomescape.service.dto.PaymentRequestDto;

@Service
public class TossPaymentService {

    private static final String AUTHORIZATION_PREFIX = "Basic ";

    private final TossPaymentConfigProperties properties;
    private final RestClient restClient;

    public TossPaymentService(TossPaymentConfigProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
            .baseUrl(properties.getPaymentApprovalUrl())
            .build();
    }

    @Transactional
    public void pay(String orderId, long amount, String paymentKey) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(properties.getTestSecretKey().getBytes(StandardCharsets.UTF_8));
        String authorizations = AUTHORIZATION_PREFIX + new String(encodedBytes);

        try {
            restClient.post()
                .uri(properties.getPaymentApprovalUrl())
                .body(new PaymentRequestDto(orderId, amount, paymentKey))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizations)
                .retrieve()
                .toBodilessEntity();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            PaymentErrorMessageResponse response = e.getResponseBodyAs(PaymentErrorMessageResponse.class);
            if (response == null) {
                throw new RoomescapeException("결제가 승인되지 않았습니다.");
            }
            throw new RoomescapeException(response.message());
        } catch (RestClientResponseException e) {
            throw new RoomescapeException("결제가 승인되지 않았습니다.");
        }
    }
}
