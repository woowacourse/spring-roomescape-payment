package roomescape.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import roomescape.Fixtures;
import roomescape.payment.domain.Payment;
import roomescape.payment.infrastructure.TossPaymentRestClient;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(value = {PaymentService.class, EncodingService.class, TossPaymentRestClient.class, ObjectMapper.class})
@Sql(value = "/recreate_table.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("결제 서비스")
class PaymentServiceTest {

    private final PaymentService paymentService;
    private final EncodingService encodingService;

    @Autowired
    public PaymentServiceTest(PaymentService paymentService, EncodingService encodingService) {
        this.paymentService = paymentService;
        this.encodingService = encodingService;
    }

    @DisplayName("결제 서비스는 주어진 결제 내역의 복호화 된 paymetKey를 반환한다.")
    @Test
    void getPlainPaymentKey() {
        // given
        Payment payment = Payment.of(Fixtures.paymentResponseFixture, Fixtures.memberReservationFixture, encodingService);

        // when
        String actual = paymentService.getPlainPaymentKey(payment);

        // then
        assertThat(actual).isEqualTo("testKey");
    }
}
