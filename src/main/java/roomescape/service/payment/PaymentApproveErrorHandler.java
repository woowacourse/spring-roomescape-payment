package roomescape.service.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.global.exception.payment.PaymentException;
import roomescape.global.exception.payment.TossPaymentErrorCode;

@Slf4j
public class PaymentApproveErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        byte[] bodyBytes = StreamUtils.copyToByteArray(response.getBody());
        JsonNode jsonNode = objectMapper.readTree(new ByteArrayInputStream(bodyBytes));
        final String code = extractMessage(jsonNode, "code");
        final String errorMessage = extractMessage(jsonNode, "message");
        log.error("[TOSS_API_ERROR] {} {} | Status: {}, Code: {}, Message: {}",
                method.name(), url, response.getStatusCode(), code, errorMessage);

        final TossPaymentErrorCode tossPaymentErrorCode = TossPaymentErrorCode.findTossPaymentErrorCode(code);
        throw new PaymentException(tossPaymentErrorCode, errorMessage);
    }

    private String extractMessage(JsonNode jsonNode, String key) {
        return jsonNode.get(key).asText();
    }
}
