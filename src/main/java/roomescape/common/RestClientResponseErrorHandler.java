package roomescape.common;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class RestClientResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        final HttpStatusCode statusCode = response.getStatusCode();
        return statusCode.is4xxClientError() || statusCode.is5xxServerError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
            throw new RuntimeException(Arrays.toString(response.getBody().readAllBytes()));
        }
        throw new RuntimeException(Arrays.toString(response.getBody().readAllBytes()));
    }
}
