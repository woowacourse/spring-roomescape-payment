package roomescape.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import roomescape.IntegrationTestSupport;
import roomescape.exception.RoomEscapeBusinessException;
import roomescape.service.dto.PaymentApproveRequest;

@Transactional
class PaymentServiceTest extends IntegrationTestSupport {

    @Autowired
    private PaymentService paymentService;

    @DisplayName("하나의 예약에 대해 결제 완료된 건을 중복해서 결제할 수 없다.")
    @Test
    void validateDuplicatePayment() {
        // given
        PaymentApproveRequest paymentApproveRequest = new PaymentApproveRequest(
                1L,
                1L,
                "paymentKey",
                "orderId",
                1000
        );
        paymentService.requestApproval(paymentApproveRequest);

        // when // then
        assertThatThrownBy(() -> paymentService.requestApproval(paymentApproveRequest))
                .isInstanceOf(RoomEscapeBusinessException.class)
                .hasMessageContaining("이미 결제된 예약입니다.");



    }
}
