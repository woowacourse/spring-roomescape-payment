package roomescape.infrastructure;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

public class BufferingClientHttpResponseWrapper implements ClientHttpResponse {

    private final ClientHttpResponse response;
    private final byte[] buffer;

    public BufferingClientHttpResponseWrapper(ClientHttpResponse response) throws IOException {
        this.response = response;
        this.buffer = StreamUtils.copyToByteArray(response.getBody());
    }

    @Override
    public HttpStatusCode getStatusCode() throws IOException {
        return response.getStatusCode();
    }

    @Override
    public String getStatusText() throws IOException {
        return response.getStatusText();
    }

    @Override
    public void close() {
        response.close();
    }

    @Override
    public InputStream getBody() {
        return new ByteArrayInputStream(buffer);
    }

    @Override
    public HttpHeaders getHeaders() {
        return response.getHeaders();
    }
}
