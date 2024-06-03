package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import roomescape.IntegrationTestSupport;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.payment.repository.PaymentRepository;
import roomescape.exception.RoomEscapeBusinessException;
import roomescape.service.dto.PaymentApproveRequest;

@Transactional
class PaymentServiceTest extends IntegrationTestSupport {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

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

    @DisplayName("결제를 한다.")
    @Test
    void requestApproval() {
        // given
        long reservationId = 1L;
        long memberId = 1L;
        PaymentApproveRequest paymentApproveRequest = new PaymentApproveRequest(
                reservationId,
                memberId,
                "paymentKey",
                "orderId",
                1000
        );

        // when
        paymentService.requestApproval(paymentApproveRequest);

        // then
        Payment payment = paymentRepository.findByReservationIdAndStatus(reservationId, PaymentStatus.DONE).get();

        assertThat(payment.getMemberId()).isEqualTo(memberId);
    }

    @DisplayName("결제를 취소한다.")
    @Test
    void requestRefund() {
        // given
        long reservationId = 1L;
        long memberId = 1L;
        PaymentApproveRequest paymentApproveRequest = new PaymentApproveRequest(
                reservationId,
                memberId,
                "paymentKey",
                "orderId",
                1000
        );
        paymentService.requestApproval(paymentApproveRequest);

        // when
        paymentService.requestRefund(reservationId, memberId);

        // then
        Payment payment = paymentRepository.findByReservationIdAndStatus(reservationId, PaymentStatus.CANCELED).get();

        assertThat(payment.getMemberId()).isEqualTo(memberId);
    }

    @DisplayName("결제 완료된 예약 ID를 조회한다.")
    @Test
    void findDoneStatusReservationIds() {
        // given
        List<Long> doneReservationIds = IntStream.range(1, 8).mapToObj(Long::valueOf).toList();

        for (Long reservationId : doneReservationIds) {
            long memberId = 1L;
            PaymentApproveRequest paymentApproveRequest = new PaymentApproveRequest(
                    reservationId,
                    memberId,
                    "paymentKey",
                    "orderId",
                    1000
            );
            paymentService.requestApproval(paymentApproveRequest);
        }

        List<Long> reservationIds = IntStream.range(1, 12).mapToObj(Long::valueOf).toList();
        // when
        List<Long> doneStatusReservationIds = paymentService.findDoneStatusReservationIds(reservationIds);

        // then
        assertThat(doneStatusReservationIds).isEqualTo(doneReservationIds);
    }
}
