package roomescape.exception;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.exception.customexception.api.ApiBadRequestException;
import roomescape.exception.customexception.api.ApiException;
import roomescape.exception.customexception.api.ApiTimeOutException;
import roomescape.exception.dto.ThirdPartyErrorResponse;

import java.io.IOException;
import java.net.SocketTimeoutException;

@Component
public class ApiExceptionHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) {
        try {
            return response.getStatusCode().is4xxClientError() ||
                    response.getStatusCode().is5xxServerError();
        } catch (SocketTimeoutException exception) {
            throw new ApiTimeOutException("결제 승인 요청 시간이 초과되었습니다.");
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void handleError(@NonNull ClientHttpResponse response) {
        ThirdPartyErrorResponse error = getResponseBody(response);

        if (ApiBadRequestExceptions.isBadRequest(error.code())) {
            throw new ApiBadRequestException(error.message());
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
