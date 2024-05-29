package roomescape.client;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.Response;
import feign.codec.ErrorDecoder;

public class PaymentErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            TossErrorResponse tossErrorResponse = mapper.readValue(response.body().asInputStream(),
                    TossErrorResponse.class);
            return new PaymentException(tossErrorResponse);
        } catch (IOException e) {
            return defaultErrorDecoder.decode(methodKey, response);
        }
    }
}
