package roomescape.payment.exception.handler;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.payment.domain.ConfidentialApproveExceptionCode;
import roomescape.payment.exception.PaymentApproveException;

@Component
public class PaymentApproveExceptionHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is5xxServerError() ||
                response.getStatusCode().is4xxClientError();
    }

    @Override
    public void handleError(final URI url, final HttpMethod method, final ClientHttpResponse response)
            throws IOException {
        String body = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(body);
        String code = jsonNode.path("code").asText();
        String message = jsonNode.path("message").asText();

        if (ConfidentialApproveExceptionCode.isConfidential(code)) {
            throw new PaymentApproveException("결제 도중 오류가 발생했습니다. 관리자에게 문의하세요.", response.getStatusCode());
        }
        throw new PaymentApproveException(message, response.getStatusCode());
    }
}
