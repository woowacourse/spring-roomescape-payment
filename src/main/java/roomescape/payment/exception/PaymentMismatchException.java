package roomescape.payment.exception;

import org.springframework.http.HttpStatus;
import roomescape.common.domain.DomainTerm;
import roomescape.common.exception.base.BusinessException;
import roomescape.common.exception.util.ExceptionMessageFormatter;

public class PaymentMismatchException extends BusinessException {

    public PaymentMismatchException(final DomainTerm term, final Object... params) {
        super(
                buildLogMessage(term, params),
                buildUserMessage(term)
        );
    }

    private static String buildLogMessage(final DomainTerm term, final Object... params) {
        return ExceptionMessageFormatter.format("%s values mismatch.".formatted(term.name()), params);
    }

    private static String buildUserMessage(final DomainTerm term) {
        return "요청한 결제정보와 실제 %s이 일치하지 않습니다.".formatted(term.label());
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.CONFLICT;
    }
}
