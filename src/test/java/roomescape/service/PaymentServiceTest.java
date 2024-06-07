package roomescape.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import roomescape.global.exception.RoomescapeException;
import roomescape.repository.PaymentRepository;
import roomescape.service.client.FakeTossPaymentClient;
import roomescape.service.dto.PaymentRequest;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/truncate.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class PaymentServiceTest {

    PaymentService paymentService;

    @Autowired
    PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(new FakeTossPaymentClient(), paymentRepository);
    }

    @Nested
    @DisplayName("결제 요청")
    class Pay {
        @Test
        @DisplayName("성공: 결제 요청 성공")
        void pay() {
            PaymentRequest request = new PaymentRequest("paymentKey", 1000, "orderId");
            assertThatCode(
                    () -> paymentService.pay(request))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패: 잘못된 orderId나 paymentKey 입력 시 예외가 발생한다")
        void pay_roomEscapeException() {
            PaymentRequest request = new PaymentRequest("", 1000, "");
            assertThatThrownBy(
                    () -> paymentService.pay(request))
                    .isInstanceOf(RoomescapeException.class);
        }
    }
}
