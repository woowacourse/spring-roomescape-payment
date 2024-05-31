package roomescape.utils;

import java.net.URI;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.StreamingHttpOutputMessage.Body;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;
import roomescape.core.domain.Payment;
import roomescape.core.dto.payment.PaymentConfirmRequest;
import roomescape.core.dto.payment.PaymentConfirmResponse;

@Component
@Profile("test")
public class TestClient implements RestClient {
    public static String RESPONSE_4XX_KEY = "Payment key for response 4XX";
    public static String RESPONSE_5XX_KEY = "Payment key for response 5XX";

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
        return new TestRequestBodyUriSpec();
    }

    @Override
    public RequestBodyUriSpec put() {
        return new TestRequestBodyUriSpec();
    }

    @Override
    public RequestBodyUriSpec patch() {
        return new TestRequestBodyUriSpec();
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
        return new TestRequestBodyUriSpec();
    }

    @Override
    public Builder mutate() {
        return null;
    }

    static class TestRequestBodyUriSpec implements RequestBodyUriSpec {
        private PaymentConfirmRequest paymentConfirmRequest;

        private TestRequestBodyUriSpec() {
        }

        private TestRequestBodyUriSpec(PaymentConfirmRequest paymentConfirmRequest) {
            this.paymentConfirmRequest = paymentConfirmRequest;
        }

        @Override
        public RequestBodySpec body(Object body) {
            PaymentConfirmRequest request = (PaymentConfirmRequest) body;
            if (request.getPaymentKey().equals(RESPONSE_4XX_KEY)) {
                HttpClientErrorException e = new HttpClientErrorException(HttpStatus.BAD_REQUEST);
                e.setBodyConvertFunction(obj -> null);
                throw e;
            }
            if (request.getPaymentKey().equals(RESPONSE_5XX_KEY)) {
                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new TestRequestBodyUriSpec(request);
        }

        @Override
        public RequestBodySpec contentLength(long contentLength) {
            return this;
        }

        @Override
        public RequestBodySpec contentType(MediaType contentType) {
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
            return new TestResponseSpec(paymentConfirmRequest);
        }

        @Override
        public <T> T exchange(ExchangeFunction<T> exchangeFunction, boolean close) {
            return null;
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

    static class TestResponseSpec implements ResponseSpec {
        private final PaymentConfirmRequest paymentConfirmRequest;

        public TestResponseSpec(PaymentConfirmRequest paymentConfirmRequest) {
            this.paymentConfirmRequest = paymentConfirmRequest;
        }

        @Override
        public ResponseSpec onStatus(Predicate<HttpStatusCode> statusPredicate,
                                     ErrorHandler errorHandler) {
            return this;
        }

        @Override
        public ResponseSpec onStatus(ResponseErrorHandler errorHandler) {
            return this;
        }

        @Override
        public <T> T body(Class<T> bodyType) {
            return (T) new PaymentConfirmResponse(new Payment(
                    paymentConfirmRequest.getPaymentKey(),
                    paymentConfirmRequest.getOrderId(),
                    paymentConfirmRequest.getAmount()
            ));
        }

        @Override
        public <T> T body(ParameterizedTypeReference<T> bodyType) {
            return null;
        }

        @Override
        public <T> ResponseEntity<T> toEntity(Class<T> bodyType) {
            return null;
        }

        @Override
        public <T> ResponseEntity<T> toEntity(ParameterizedTypeReference<T> bodyType) {
            return null;
        }

        @Override
        public ResponseEntity<Void> toBodilessEntity() {
            return null;
        }
    }
}
