package roomescape.exception;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.exception.customexception.api.ApiException;
import roomescape.exception.dto.ThirdPartyErrorResponse;

import java.io.IOException;

@Component
public class ApiExceptionHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) {
        try {
            return response.getStatusCode().is4xxClientError() ||
                    response.getStatusCode().is5xxServerError();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void handleError(@NonNull ClientHttpResponse response) {
        ThirdPartyErrorResponse error =getResponseBody(response);

        if(ApiBadRequestException.isBadRequest(error.code())) {
            throw new roomescape.exception.customexception.api.ApiBadRequestException(error.message());
        }

        throw new ApiException("결제 과정에서 문제가 발생했습니다.");
    }

    private ThirdPartyErrorResponse getResponseBody(ClientHttpResponse response) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(
                    response.getBody(),
                    ThirdPartyErrorResponse.class);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
