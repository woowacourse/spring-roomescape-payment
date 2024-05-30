package roomescape.infra.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.infra.payment.dto.PaymentErrorResponse;
import roomescape.infra.payment.exception.PaymentClientException;
import roomescape.infra.payment.exception.PaymentServerException;

@Component
public class TossPaymentResponseErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    public TossPaymentResponseErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        PaymentErrorResponse paymentErrorResponse = objectMapper
                .readValue(response.getBody(), PaymentErrorResponse.class);

        if (response.getStatusCode().is4xxClientError()) {
            throw new PaymentClientException(paymentErrorResponse.message(), response.getStatusCode());
        }

        throw new PaymentServerException(paymentErrorResponse.message());
    }

}
