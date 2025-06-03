package roomescape.utility;

import org.springframework.web.client.RestClientException;
import roomescape.domain.PaymentResult;
import roomescape.exception.PaymentException;
import roomescape.external.payment.PaymentClient;

public class PaymentClientStub implements PaymentClient {

    private String errorCase = null;
    private boolean occurRestClientError;

    @Override
    public PaymentResult pay(String paymentKey, String orderId, long amount, String paymentType) {
        if (occurRestClientError) {
            throw new RestClientException("restclienterror");
        }
        if (errorCase != null) {
            throw new PaymentException(errorCase);
        }
        return PaymentResult.createWithoutId("askdkasrwe", "sdfa132", "asdfasdf", 1L);
    }

    public void clearErrorCase() {
        this.errorCase = null;
    }

    public void setErrorCase(String errorCase) {
        this.errorCase = errorCase;
    }

    public void setOccurRestClientError(boolean occurRestClientError) {
        this.occurRestClientError = occurRestClientError;
    }
}
