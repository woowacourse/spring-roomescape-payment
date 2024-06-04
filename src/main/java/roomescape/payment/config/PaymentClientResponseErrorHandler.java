package roomescape.payment.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.exception.PaymentException;
import roomescape.exception.response.PaymentExceptionResponse;

@Component
public class PaymentClientResponseErrorHandler implements ResponseErrorHandler {

    private static final String ERROR_MESSAGE = "message";
    private static final String ERROR_CODE = "code";
    private static final String DEFAULT_ERROR_MESSAGE = "알 수 없는 에러가 발생하였습니다.";
    private static final String DEFAULT_ERROR_CODE = "Unknown Error";
    private final ObjectMapper objectMapper;

    public PaymentClientResponseErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return httpResponse.getStatusCode().isError();
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getBody()));
        String responseBody = reader.lines().collect(Collectors.joining(""));
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String errorCode = jsonNode.path(ERROR_CODE).asText(DEFAULT_ERROR_CODE);
        String errorMessage = jsonNode.path(ERROR_MESSAGE).asText(DEFAULT_ERROR_MESSAGE);
        throw new PaymentException(
                PaymentExceptionResponse.of(
                        httpResponse.getStatusCode(),
                        errorCode,
                        errorMessage)
        );
    }
}
