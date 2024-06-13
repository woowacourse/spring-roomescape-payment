package roomescape.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.core.dto.member.LoginMember;
import roomescape.core.dto.payment.PaymentConfirmResponse;
import roomescape.core.dto.payment.PaymentRequest;
import roomescape.utils.DatabaseCleaner;
import roomescape.utils.TestFixture;

@ServiceTest
class PaymentServiceTest {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private TestFixture testFixture;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        databaseCleaner.executeTruncate();
        testFixture.initTestData();
    }

    @Test
    @DisplayName("결제를 승인할 수 있다.")
    void confirmPayment() {
        final PaymentRequest request = new PaymentRequest(1L, 1000, "orderId", "paymentKey");
        final LoginMember loginMember = new LoginMember(1L);

        final PaymentConfirmResponse response = paymentService.confirmPayment(request, loginMember);

        assertAll(
                () -> assertThat(response.totalAmount()).isEqualTo(1000),
                () -> assertThat(response.orderId()).isEqualTo("orderId"),
                () -> assertThat(response.paymentKey()).isEqualTo("paymentKey")
        );
    }

    @Test
    @DisplayName("결제를 취소할 수 있다.")
    void cancelPayment() {
        assertDoesNotThrow(() -> paymentService.cancel(1L, new LoginMember(1L)));
    }
}
