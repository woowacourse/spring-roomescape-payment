package roomescape.payment.tosspayment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.ResponseSpec.ErrorHandler;
import roomescape.payment.global.PgPaymentRestClient;
import roomescape.payment.global.domain.PgPayment;
import roomescape.payment.global.domain.dto.PgPaymentRequestDto;
import roomescape.payment.global.domain.dto.PgPaymentDataDto;
import roomescape.payment.global.exception.InvalidPgPaymentException;
import roomescape.payment.tosspayment.domain.TossPaymentErrorResponse;

@Component
public class TossPgPaymentRestClient implements PgPaymentRestClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public TossPgPaymentRestClient(RestClient tosspaymentRestClient, ObjectMapper objectMapper) {
        this.restClient = tosspaymentRestClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public PgPaymentDataDto confirmPayment(PgPaymentRequestDto requestDto) {
        PgPayment pgPayment = restClient.post()
                .uri("/v1/payments/confirm")
                .body(requestDto)
                .retrieve()
                .onStatus(this::isError, getErrorHandler())
                .toEntity(PgPayment.class)
                .getBody();

        return PgPaymentDataDto.of(pgPayment);
    }

    @Override
    public boolean isError(HttpStatusCode httpStatusCode) {
        return httpStatusCode.is4xxClientError() || httpStatusCode.is5xxServerError();
    }

    private ErrorHandler getErrorHandler() {
        return (req, res) -> {
            TossPaymentErrorResponse error = objectMapper.readValue(res.getBody(), TossPaymentErrorResponse.class);
            HttpStatus status = HttpStatus.valueOf(res.getStatusCode().value());
            throw new InvalidPgPaymentException(error.message(), status);
        };
    }
}
