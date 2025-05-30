package roomescape.utility;

import org.springframework.web.client.RestClientException;
import roomescape.domain.PaymentResult;
import roomescape.exception.PaymentException;

public class PaymentClientStub implements PaymentClient {

    private PaymentResult paymentResult = PaymentResult.createWithoutId("askdkasrwe", "sdfa132", "asdfasdf", 0L);
    private String errorCase = null;
    private boolean occurRestClientError;

    @Override
    public PaymentResult pay(String paymentKey, String orderId, long amount) {
        if (occurRestClientError) {
            throw new RestClientException("restclienterror");
        }
        if (errorCase != null) {
            throw new PaymentException(errorCase);
        }
        return paymentResult;
    }

    public void clearErrorCase() {
        this.errorCase = null;
    }

    public void setErrorCase(String errorCase) {
        this.errorCase = errorCase;
    }

    public void setPaymentResult(PaymentResult paymentResult) {
        this.paymentResult = paymentResult;
    }

    public void setOccurRestClientError(boolean occurRestClientError) {
        this.occurRestClientError = occurRestClientError;
    }
}
