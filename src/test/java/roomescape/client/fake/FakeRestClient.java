package roomescape.client.fake;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.StreamingHttpOutputMessage.Body;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.RequestHeadersSpec.ConvertibleClientHttpResponse;
import org.springframework.web.util.UriBuilder;

import roomescape.client.PaymentException;
import roomescape.client.TossErrorResponse;
import roomescape.reservation.dto.request.PaymentRequest;

public class FakeRestClient implements RestClient {
    private static final FakeRequestBodyUriSpec fakeRequestBodyUriSpec = new FakeRequestBodyUriSpec();
    private static boolean validResponse = true;

    @Override
    public RequestHeadersUriSpec<?> get() {
        return null;
    }

    @Override
    public RequestHeadersUriSpec<?> head() {
        return null;
    }

    @Override
    public RequestBodyUriSpec post() {
        return fakeRequestBodyUriSpec;
    }

    @Override
    public RequestBodyUriSpec put() {
        return null;
    }

    @Override
    public RequestBodyUriSpec patch() {
        return null;
    }

    @Override
    public RequestHeadersUriSpec<?> delete() {
        return null;
    }

    @Override
    public RequestHeadersUriSpec<?> options() {
        return null;
    }

    @Override
    public RequestBodyUriSpec method(HttpMethod method) {
        return null;
    }

    @Override
    public Builder mutate() {
        return null;
    }

    static class FakeRequestBodyUriSpec implements RequestBodyUriSpec {

        @Override
        public RequestBodySpec contentLength(long contentLength) {
            return this;
        }

        @Override
        public RequestBodySpec contentType(MediaType contentType) {
            return this;
        }

        @Override
        public RequestBodySpec body(Object body) {
            PaymentRequest request = (PaymentRequest) body;
            if (request.amount() < 0 || request.paymentKey().isBlank() || request.orderId().isBlank()) {
                validResponse = false;
            }
            return this;
        }

        @Override
        public <T> RequestBodySpec body(T body, ParameterizedTypeReference<T> bodyType) {
            return this;
        }

        @Override
        public RequestBodySpec body(Body body) {
            return this;
        }

        @Override
        public RequestBodySpec accept(MediaType... acceptableMediaTypes) {
            return null;
        }

        @Override
        public RequestBodySpec acceptCharset(Charset... acceptableCharsets) {
            return this;
        }

        @Override
        public RequestBodySpec ifModifiedSince(ZonedDateTime ifModifiedSince) {
            return this;
        }

        @Override
        public RequestBodySpec ifNoneMatch(String... ifNoneMatches) {
            return this;
        }

        @Override
        public RequestBodySpec header(String headerName, String... headerValues) {
            validResponse = Arrays.stream(headerValues)
                    .anyMatch(checkLegalAuth(headerName));
            return this;
        }

        private Predicate<String> checkLegalAuth(String headerName) {
            return headerValue -> headerName.equals(FakeHeaderConstant.AUTHORIZATION_HEADER.getName())
                    && headerValue.equals(
                    FakeHeaderConstant.AUTHORIZATION_HEADER.getValue());
        }

        @Override
        public RequestBodySpec headers(Consumer<HttpHeaders> headersConsumer) {
            return this;
        }

        @Override
        public RequestBodySpec httpRequest(Consumer<ClientHttpRequest> requestConsumer) {
            return this;
        }

        @Override
        public ResponseSpec retrieve() {
            return null;
        }

        @Override
        public <T> T exchange(ExchangeFunction<T> exchangeFunction, boolean close) {
            HttpRequest request = new FakeHttpRequest();
            ConvertibleClientHttpResponse response = new FakeConvertibleClientHttpResponse();
            try {
                return exchangeFunction.exchange(request, response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public RequestBodySpec uri(URI uri) {
            return this;
        }

        @Override
        public RequestBodySpec uri(String uri, Object... uriVariables) {
            return this;
        }

        @Override
        public RequestBodySpec uri(String uri, Map<String, ?> uriVariables) {
            return this;
        }

        @Override
        public RequestBodySpec uri(String uri, Function<UriBuilder, URI> uriFunction) {
            return this;
        }

        @Override
        public RequestBodySpec uri(Function<UriBuilder, URI> uriFunction) {
            return this;
        }
    }

    static class FakeRequestBodySpec implements RequestBodySpec {

        @Override
        public RequestBodySpec contentLength(long contentLength) {
            return this;
        }

        @Override
        public RequestBodySpec contentType(MediaType contentType) {
            return this;
        }

        @Override
        public RequestBodySpec body(Object body) {
            PaymentRequest request = (PaymentRequest) body;
            if (request.amount() < 0 || request.paymentKey().isBlank() || request.orderId().isBlank()) {
                validResponse = false;
            }
            return this;
        }

        @Override
        public <T> RequestBodySpec body(T body, ParameterizedTypeReference<T> bodyType) {
            return this;
        }

        @Override
        public RequestBodySpec body(Body body) {
            return this;
        }

        @Override
        public RequestBodySpec accept(MediaType... acceptableMediaTypes) {
            return this;
        }

        @Override
        public RequestBodySpec acceptCharset(Charset... acceptableCharsets) {
            return this;
        }

        @Override
        public RequestBodySpec ifModifiedSince(ZonedDateTime ifModifiedSince) {
            return this;
        }

        @Override
        public RequestBodySpec ifNoneMatch(String... ifNoneMatches) {
            return this;
        }

        @Override
        public RequestBodySpec header(String headerName, String... headerValues) {
            return this;
        }

        @Override
        public RequestBodySpec headers(Consumer<HttpHeaders> headersConsumer) {
            return this;
        }

        @Override
        public RequestBodySpec httpRequest(Consumer<ClientHttpRequest> requestConsumer) {
            return this;
        }

        @Override
        public ResponseSpec retrieve() {
            return null;
        }

        @Override
        public <T> T exchange(ExchangeFunction<T> exchangeFunction, boolean close) {
            HttpRequest request = new FakeHttpRequest();
            ConvertibleClientHttpResponse response = new FakeConvertibleClientHttpResponse();
            try {
                return exchangeFunction.exchange(request, response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class FakeHttpRequest implements HttpRequest {
        @Override
        public HttpMethod getMethod() {
            return null;
        }

        @Override
        public URI getURI() {
            return null;
        }

        @Override
        public HttpHeaders getHeaders() {
            return null;
        }
    }

    static class FakeRequestHeadersSpec implements RequestHeadersSpec {
        @Override
        public RequestHeadersSpec accept(MediaType... acceptableMediaTypes) {
            return this;
        }

        @Override
        public RequestHeadersSpec acceptCharset(Charset... acceptableCharsets) {
            return this;
        }

        @Override
        public RequestHeadersSpec ifModifiedSince(ZonedDateTime ifModifiedSince) {
            return this;
        }

        @Override
        public RequestHeadersSpec ifNoneMatch(String... ifNoneMatches) {
            return this;
        }

        @Override
        public RequestHeadersSpec header(String headerName, String... headerValues) {
            return this;
        }

        @Override
        public RequestHeadersSpec headers(Consumer consumer) {
            return this;
        }

        @Override
        public RequestHeadersSpec httpRequest(Consumer consumer) {
            return this;
        }

        @Override
        public ResponseSpec retrieve() {
            return null;
        }

        @Override
        public Object exchange(ExchangeFunction exchangeFunction, boolean close) {
            HttpRequest request = new FakeHttpRequest();
            ConvertibleClientHttpResponse response = new FakeConvertibleClientHttpResponse();
            try {
                return exchangeFunction.exchange(request, response);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class FakeConvertibleClientHttpResponse implements ConvertibleClientHttpResponse {
        @Override
        public <T> T bodyTo(Class<T> bodyType) {
            return null;
        }

        @Override
        public <T> T bodyTo(ParameterizedTypeReference<T> bodyType) {
            return null;
        }

        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return validResponse ? HttpStatusCode.valueOf(200) : HttpStatusCode.valueOf(400);
        }

        @Override
        public String getStatusText() throws IOException {
            return null;
        }

        @Override
        public void close() {

        }

        @Override
        public InputStream getBody() throws IOException {
            throw new PaymentException(new TossErrorResponse("400", "", ""), HttpStatusCode.valueOf(400));
        }

        @Override
        public HttpHeaders getHeaders() {
            return null;
        }
    }
}
