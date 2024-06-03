package roomescape.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import roomescape.global.exception.RoomescapeException;
import roomescape.service.client.FakeTossPaymentClient;

@Sql(scripts = "/truncate.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
class PaymentServiceTest {

    PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(new FakeTossPaymentClient());
    }

    @Nested
    @DisplayName("결제 요청")
    class Pay {
        @Test
        @DisplayName("성공: 결제 요청 성공")
        void pay() {
            assertThatCode(
                    () -> paymentService.pay("orderId", 1000, "paymentKey"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("실패: 잘못된 orderId나 paymentKey 입력 시 예외가 발생한다")
        void pay_roomEscapeException() {
            assertThatThrownBy(
                    () -> paymentService.pay("", 1000, ""))
                    .isInstanceOf(RoomescapeException.class);
        }
    }
}
