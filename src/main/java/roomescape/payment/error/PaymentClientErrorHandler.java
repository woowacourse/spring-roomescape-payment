package roomescape.payment.error;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;
import roomescape.global.error.exception.BadRequestException;
import roomescape.payment.dto.response.PaymentErrorResponse;

public class PaymentClientErrorHandler implements ErrorHandler {

    @Override
    public void handle(HttpRequest request, ClientHttpResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        PaymentErrorResponse paymentErrorResponse = objectMapper.readValue(
                response.getBody(),
                PaymentErrorResponse.class
        );
        throw new BadRequestException(paymentErrorResponse.message());
    }
}
