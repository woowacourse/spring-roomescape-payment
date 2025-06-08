package roomescape.payment.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.common.exception.impl.DeserializationException;
import roomescape.common.exception.impl.ExternalApiException;
import roomescape.payment.application.dto.TossErrorResponse;

public class TossPaymentErrorHandler implements ResponseErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(TossPaymentErrorHandler.class);

    private final ObjectMapper objectMapper;

    public TossPaymentErrorHandler(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(final URI url, final HttpMethod method, final ClientHttpResponse response) throws IOException {
        HttpStatus status = (HttpStatus) response.getStatusCode();
        TossErrorResponse error = parseErrorResponse(response.getBody());

        if (TossErrorCodesTreatedAsServerError.contains(error.code())) {
            logger.error("Toss API Unexpected error occurred: Code: {}, Message: {}", error.code(), error.message());
            throw new ExternalApiException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부에 오류가 발생했습니다.");
        }
        throw new ExternalApiException(status, error.message());
    }

    private TossErrorResponse parseErrorResponse(final InputStream bodyStream) {
        try (InputStream errorBody = bodyStream) {
            return objectMapper.readValue(errorBody, TossErrorResponse.class);
        } catch (Exception e) {
            throw new DeserializationException("Error parsing Toss error", e);
        }
    }

    public enum TossErrorCodesTreatedAsServerError {
        UNAUTHORIZED_KEY,
        INCORRECT_BASIC_AUTH_FORMAT,
        NOT_FOUND_TERMINAL_ID,
        BELOW_MINIMUM_AMOUNT,
        INVALID_AUTHORIZE_AUTH,
        INVALID_UNREGISTERED_SUBMALL,
        NOT_REGISTERED_BUSINESS;

        private static final Set<String> CODE_SET = Stream.of(values())
            .map(Enum::name)
            .collect(Collectors.toSet());

        public static boolean contains(final String code) {
            return CODE_SET.contains(code);
        }
    }
}
