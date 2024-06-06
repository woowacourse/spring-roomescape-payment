package roomescape.client;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ExchangeFunction;

import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.reservation.dto.request.PaymentRequest;

@Component
public class PaymentClient {
    private final RestClient client;
    private final ObjectMapper mapper;

    public PaymentClient(ObjectMapper mapper, RestClient client) {
        this.mapper = mapper;
        this.client = client;
    }

    public HttpStatusCode confirm(String authorization, PaymentRequest paymentRequest) {
        return client.post()
                .uri("/v1/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorization)
                .body(paymentRequest)
                .exchange(invokeErrorCheck());
    }

    private ExchangeFunction<HttpStatusCode> invokeErrorCheck() {
        return (request, response) -> {
            if (response.getStatusCode().isError()) {
                throw new PaymentException(mapper.readValue(response.getBody(), TossErrorResponse.class));
            }
            return response.getStatusCode();
        };
    }
}
