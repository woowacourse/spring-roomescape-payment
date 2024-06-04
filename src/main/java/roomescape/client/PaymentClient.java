package roomescape.client;

import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ExchangeFunction;

import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.reservation.dto.request.PaymentRequest;

@Component
public class PaymentClient {
    private static final int CONNECT_TIME_VALUE = 1000;
    private static final int READ_TIME_OUT_VALUE = 10000;

    private final RestClient client;
    private final ObjectMapper mapper;

    public PaymentClient(ObjectMapper mapper) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECT_TIME_VALUE);
        requestFactory.setReadTimeout(READ_TIME_OUT_VALUE);
        this.mapper = mapper;
        this.client = RestClient.builder()
                .baseUrl("https://api.tosspayments.com/")
                .build();
    }

    public Object payForReservation(String authorization, PaymentRequest paymentRequest) {
        return client.post()
                .uri("/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorization)
                .body(paymentRequest)
                .exchange(invokeErrorCheck());
    }

    private ExchangeFunction<Object> invokeErrorCheck() {
        return (request, response) -> {
            if (response.getStatusCode().isError()) {
                throw new PaymentException(mapper.readValue(response.getBody(), TossErrorResponse.class));
            }
            return response.getStatusCode();
        };
    }
}
