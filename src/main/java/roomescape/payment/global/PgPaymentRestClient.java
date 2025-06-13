package roomescape.payment.global;

import org.springframework.http.HttpStatusCode;
import roomescape.payment.global.domain.dto.PgPaymentRequestDto;
import roomescape.payment.global.domain.dto.PgPaymentDataDto;

public interface PgPaymentRestClient {

    PgPaymentDataDto confirmPayment(PgPaymentRequestDto requestDto);

    boolean isError(HttpStatusCode httpStatusCode);
}
