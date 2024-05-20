package roomescape.payment;

import org.slf4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import roomescape.LoggerUtil;
import roomescape.exception.RoomEscapeErrorCode;
import roomescape.exception.RoomEscapeException;

@Service
public class TossPaymentClient {
    private final static String TOSS_SECRET_KEY = "Basic dGVzdF9nc2tfZG9jc19PYVB6OEw1S2RtUVhrelJ6M3k0N0JNdzY6";
    private final static String TOSS_PAYMENT_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private final static Logger log = LoggerUtil.getLogger(TossPaymentClient.class);
    private final RestTemplate restTemplate;

    public TossPaymentClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public PaymentConfirmResponse requestPayment(PaymentConfirmRequest paymentConfirmRequest) {
        try {
            ResponseEntity<PaymentConfirmResponse> paymentConfirmResponseResponseEntity = restTemplate.postForEntity(
                    TOSS_PAYMENT_URL,
                    new HttpEntity<>(paymentConfirmRequest, getHttpHeaders())
                    , PaymentConfirmResponse.class);
            return paymentConfirmResponseResponseEntity.getBody();
        } catch (RestClientResponseException re) {
            log.error("토스 결제 에러 message: {}, body : {}", re.getMessage(), re.getResponseBodyAsString());
            TossError error = re.getResponseBodyAs(TossError.class);
            throw new RoomEscapeException(error, (HttpStatus) re.getStatusCode());
        } catch (Exception e) {
            log.error("TossPaymentClient requestPayment error", e);
            throw new RoomEscapeException(RoomEscapeErrorCode.PAYMENT_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", TOSS_SECRET_KEY);
        return headers;
    }

}
