package roomescape.payment.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import roomescape.payment.dto.TossErrorResponse;

public class PaymentErrorDecoder implements ErrorDecoder {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            TossErrorResponse tossErrorResponse = mapper.readValue(response.body().asInputStream(),
                    TossErrorResponse.class);
            return new PaymentException(tossErrorResponse);
        } catch (IOException e) {
            return new PaymentException(
                    new TossErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(), "필드 값 매핑을 할 수 없습니다"));
        }
    }
}
