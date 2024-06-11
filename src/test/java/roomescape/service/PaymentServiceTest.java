package roomescape.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import roomescape.controller.request.ReservationRequest;
import roomescape.exception.PaymentException;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql(scripts = "/initialize_table.sql")
public class PaymentServiceTest {

    private PaymentService paymentService;

    @Autowired
    public PaymentServiceTest(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @DisplayName("등록된 금액과 다른 금액이 전달되었을 경우 예외를 발생한다.")
    @Test
    void should_throw_exception_when_different_amount() {
        assertThatThrownBy(() ->
                paymentService.confirmReservationPayments(
                        new ReservationRequest(LocalDate.now().minusDays(1), 1L, 1L, "paymentId", "paymentKey", 1234L)
                ))
                .isInstanceOf(PaymentException.class)
                .hasMessage("[ERROR] 클라이언트의 지불 정보가 일치하지 않습니다. 금액 정보 : [1234]");
    }
}
