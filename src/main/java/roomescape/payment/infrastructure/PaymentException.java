package roomescape.payment.infrastructure;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import roomescape.payment.infrastructure.dto.TossPaymentErrorResponse;

public class PaymentException extends RuntimeException {

    private final HttpStatus code;
    private final String message;
    private final String data;
    private final String orderId;

    public PaymentException(final TossPaymentErrorResponse errorResponse, final HttpStatusCode statusCode,
                            final String orderId) {
        this.code = HttpStatus.valueOf(statusCode.value());
        this.message = validateMessage(errorResponse);
        this.data = errorResponse.getData();
        this.orderId = orderId;
    }

    private String validateMessage(final TossPaymentErrorResponse errorResponse) {
        if(TossPaymentErrorMessage.contains(errorResponse.getCode())){
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

    public String getOrderId() {
        return orderId;
    }
}
