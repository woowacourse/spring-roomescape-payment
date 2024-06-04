package roomescape.client;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ExchangeFunction;

import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.reservation.dto.request.PaymentRequest;

@Component
public class PaymentRestClient {
    private final RestClient client;
    private final ObjectMapper mapper;

    public PaymentRestClient(ObjectMapper mapper) {
        this.mapper = mapper;
        this.client = RestClient.builder()
                .baseUrl("https://api.tosspayments.com/")
                .build();
    }

    public void payForReservation(String authorization, PaymentRequest paymentRequest) {
        client.post()
                .uri("/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorization)
                .body(paymentRequest)
                .exchange(invokeErrorCheck());
    }

    private ExchangeFunction<Object> invokeErrorCheck() {
        return (request, response) -> {
            if (response.getStatusCode().isError()) {
                return new PaymentException(mapper.readValue(response.getBody(), TossErrorResponse.class));
            }
            return response;
        };
    }
}
