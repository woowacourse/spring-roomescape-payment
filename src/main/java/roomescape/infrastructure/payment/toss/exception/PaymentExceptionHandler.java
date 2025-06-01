package roomescape.infrastructure.payment.toss.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.common.exception.PaymentClientException;
import roomescape.common.exception.PaymentServerException;

@RequiredArgsConstructor
public class PaymentExceptionHandler implements ResponseErrorHandler {

    private final ObjectMapper mapper;

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response)
            throws IOException {
        InputStream inputStream = response.getBody();
        TossPaymentErrorResponse tossPaymentErrorResponse = parseToTossPaymentErrorResponse(
                inputStream);

        checkClientError(tossPaymentErrorResponse);
        checkServerError(tossPaymentErrorResponse);
        throwUnDefinedError(tossPaymentErrorResponse);
    }

    private void throwUnDefinedError(TossPaymentErrorResponse tossPaymentErrorResponse) {
        throw new PaymentServerException(tossPaymentErrorResponse.message);
    }

    private static void checkServerError(TossPaymentErrorResponse tossPaymentErrorResponse) {
        Optional<TossPaymentErrorCodeForServer> tossPaymentErrorCodeForServer = TossPaymentErrorCodeForServer.of(
                tossPaymentErrorResponse.code);
        if (tossPaymentErrorCodeForServer.isPresent()) {
            throw new PaymentServerException(
                    tossPaymentErrorCodeForServer.get().getMessage());
        }
    }

    private static void checkClientError(TossPaymentErrorResponse tossPaymentErrorResponse) {
        Optional<TossPaymentErrorCodeForClient> tossPaymentErrorCodeForClient = TossPaymentErrorCodeForClient.of(
                tossPaymentErrorResponse.code);
        if (tossPaymentErrorCodeForClient.isPresent()) {
            throw new PaymentClientException(
                    tossPaymentErrorCodeForClient.get().getMessage());
        }
    }

    private TossPaymentErrorResponse parseToTossPaymentErrorResponse(InputStream bodyStream)
            throws IOException {
        return mapper.readValue(bodyStream, TossPaymentErrorResponse.class);
    }

    private record TossPaymentErrorResponse(
            String message,
            String code
    ) {

    }
}
