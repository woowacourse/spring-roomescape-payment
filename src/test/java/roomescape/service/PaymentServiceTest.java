package roomescape.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.controller.HeaderGenerator;
import roomescape.service.dto.request.PaymentApproveRequest;
import roomescape.exception.customexception.business.RoomEscapeBusinessException;
import roomescape.exception.customexception.api.ApiException;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

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

        assertThatThrownBy(() -> paymentService.pay(headerGenerator, request))
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

        assertThatThrownBy(() -> paymentService.pay(headerGenerator, request))
                .isInstanceOf(ApiException.class);
    }
}
