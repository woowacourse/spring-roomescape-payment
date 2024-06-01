package roomescape.payment.client.toss;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.common.exception.ClientException;

public class TossPaymentErrorHandler implements ResponseErrorHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        TossClientErrorResponse tossClientErrorResponse = objectMapper.readValue(response.getBody(),
                TossClientErrorResponse.class);

        if (TossErrorCodeNotForUser.hasContains(tossClientErrorResponse.code())) {
            throw new ClientException("결제 오류입니다. 같은 문제가 반복된다면 문의해주세요.");
        }

        throw new ClientException(tossClientErrorResponse.message());
    }
}
