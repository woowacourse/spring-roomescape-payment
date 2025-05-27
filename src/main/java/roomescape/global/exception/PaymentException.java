package roomescape.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import roomescape.payment.application.dto.PaymentErrorResponse;

public class PaymentException extends RuntimeException {
    private final HttpStatus code;
    private final String message;
    private final String data;

    public PaymentException(final PaymentErrorResponse errorResponse, final HttpStatusCode statusCode) {
        this.code = HttpStatus.valueOf(statusCode.value());
        this.message = validateMessage(errorResponse);
        this.data = errorResponse.getData();
    }

    private String validateMessage(final PaymentErrorResponse errorResponse) {
        if(errorResponse.getCode().equals("UNAUTHORIZED_KEY") || errorResponse.getCode().equals("INCORRECT_BASIC_AUTH_FORMAT")){
            return "오류가 발생하였습니다. 고객센터에 문의해주세요";
        }
        return errorResponse.getMessage();
    }

    public HttpStatus getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getData() {
        return data;
    }
}
