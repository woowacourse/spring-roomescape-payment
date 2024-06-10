package roomescape.exception;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;

@Tag(name = "결제 승인 예외", description = "결제 승인 예외는 Toss api에서 제공하는 에러 객체를 String으로 저장하고 커스텀 예외 코드를 가져야 한다.")
public class PaymentConfirmException extends RuntimeException{

    private final String failureCode;
    private final ExceptionCode exceptionCode;

    public PaymentConfirmException(String code, ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.failureCode = code;
        this.exceptionCode = exceptionCode;
    }

    public PaymentConfirmException(ExceptionCode exceptionCode) {
        super(exceptionCode.getMessage());
        this.failureCode = exceptionCode.getHttpStatus().toString();
        this.exceptionCode = exceptionCode;
    }

    public String getFailureCode() {
        return failureCode;
    }

    public HttpStatus getHttpStatus() {
        return exceptionCode.getHttpStatus();
    }

    @Override
    public String getMessage() {
        return exceptionCode.getMessage();
    }
}
