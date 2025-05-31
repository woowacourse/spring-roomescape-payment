package roomescape.common.interceptor;

import static roomescape.exception.code.RestClientErrorCode.CONNECTION_ERROR;
import static roomescape.exception.code.RestClientErrorCode.EXTERNAL_API_ERROR;
import static roomescape.exception.code.RestClientErrorCode.SOCKET_TIMEOUT;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import roomescape.exception.ExternalApiException;

public class RestClientInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
                                        final ClientHttpRequestExecution execution) throws IOException {

        try {
            return execution.execute(request, body);
        } catch (ConnectException e) {
            throw new ExternalApiException(CONNECTION_ERROR);
        } catch (SocketTimeoutException e) {
            throw new ExternalApiException(SOCKET_TIMEOUT);
        } catch (Exception e) {
            throw new ExternalApiException(EXTERNAL_API_ERROR, e.getMessage());
        }
    }
}
