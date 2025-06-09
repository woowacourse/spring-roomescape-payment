package roomescape.payment.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;
import roomescape.payment.exception.TossErrorResponse;
import roomescape.payment.exception.TossPaymentException;

@Component
@RequiredArgsConstructor
@Slf4j
public class TossErrorHandler implements ErrorHandler {

    private static final List<String> SERVER_ERROR_CODE_LIST = List.of(
            "INVALID_API_KEY",
            "UNAUTHORIZED_KEY",
            "INCORRECT_BASIC_AUTH_FORMAT",
            "INVALID_AUTHORIZE_AUTH"
    );

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpRequest request, ClientHttpResponse response) throws IOException {
        TossErrorResponse tossErrorResponse = extractResponseFrom(response.getBody());
        boolean isServerError = isServerError(tossErrorResponse.code());
        log.error("토스 API error: {}", tossErrorResponse);
        throw new TossPaymentException(
                response.getStatusCode(), tossErrorResponse.message(), isServerError);
    }

    private TossErrorResponse extractResponseFrom(InputStream errorStream) {
        try (errorStream) {
            String errorBody = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
            return objectMapper.readValue(errorBody, TossErrorResponse.class);
        } catch (IOException e) {
            boolean isServerError = true;
            throw new TossPaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "토스 오류 응답을 파싱할 수 없습니다.", isServerError);
        }
    }

    private boolean isServerError(String code) {
        return SERVER_ERROR_CODE_LIST.contains(code);
    }
}
