package roomescape.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import roomescape.IntegrationTestSupport;
import roomescape.controller.dto.PaymentApproveRequest;
import roomescape.exception.customexception.RoomEscapeBusinessException;
import roomescape.exception.customexception.ThirdPartyAPIException;

class PaymentControllerTest extends IntegrationTestSupport {

    @Autowired
    private PaymentController paymentController;

    @Test
    @DisplayName("400에러 반환시 custom exception으로 예외가 전환된다.")
    void is4XXException_PaymentException(){
        PaymentApproveRequest request = new PaymentApproveRequest(
                null, null, null
        );

        HeaderGenerator headerGenerator = () -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("TossPayments-Test-Code", "INVALID_CARD_LOST_OR_STOLEN");
            return headers;
        };

        assertThatThrownBy(() -> paymentController.approve(headerGenerator, request))
                .isInstanceOf(RoomEscapeBusinessException.class);
    }

    @Test
    @DisplayName("500에러 반환시 custom exeption으로 예외가 전환된다.")
    void is5XXException_PaymentException(){
        PaymentApproveRequest request = new PaymentApproveRequest(
                null, null, null
        );

        HeaderGenerator headerGenerator = () -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("TossPayments-Test-Code", "FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING");
            return headers;
        };

        assertThatThrownBy(() -> paymentController.approve(headerGenerator, request))
                .isInstanceOf(ThirdPartyAPIException.class);
    }
}
