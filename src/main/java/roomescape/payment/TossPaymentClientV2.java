package roomescape.payment;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.net.http.HttpClient;
import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import roomescape.LoggerUtil;
import roomescape.exception.RoomEscapeErrorCode;
import roomescape.exception.RoomEscapeException;

@Service
public class TossPaymentClientV2 {
    private final static String TOSS_SECRET_KEY = "Basic dGVzdF9nc2tfZG9jc19PYVB6OEw1S2RtUVhrelJ6M3k0N0JNdzY6";
    private final static String TOSS_PAYMENT_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private final static Logger log = LoggerUtil.getLogger(TossPaymentClientV2.class);
    private final RestTemplate restTemplate;

    public TossPaymentClientV2(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PaymentConfirmResponse requestPayment(PaymentConfirmRequest paymentConfirmRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        return RestClient
                .create()
                .post()
                .uri("https://api.tosspayments.com/v1/payments/confirm")
                .contentType(APPLICATION_JSON)
                .header("Authorization", TOSS_SECRET_KEY)
                .body(paymentConfirmRequest)
                .exchange((request, response) -> {
                    InputStream body = response.getBody();
                    if (response.getStatusCode().isError()) {
                        TossError tossError = objectMapper.readValue(body, TossError.class);
                        throw new RoomEscapeException(tossError, HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                    return objectMapper.readValue(response.getBody(), PaymentConfirmResponse.class);
                });
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        headers.set("Authorization", TOSS_SECRET_KEY);
        return headers;
    }

}
