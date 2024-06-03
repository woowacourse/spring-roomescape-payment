package roomescape.client;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Response;
import feign.codec.ErrorDecoder;

public class PaymentErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new Default();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            TossErrorResponse tossErrorResponse = mapper.readValue(response.body().asInputStream(),
                    TossErrorResponse.class);
            return new PaymentException(tossErrorResponse);
        } catch (IOException e) {
            return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}
