package roomescape.domain.reservation.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import roomescape.domain.reservation.model.ReservationWaitingWithOrder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
class CustomReservationWaitingRepositoryImplTest {

    @Autowired
    private CustomReservationWaitingRepository customReservationWaitingRepository;

    @DisplayName("특정 사용자의 예약 대기 정보를 조회한다.")
    @Test
    void findPopularThemesTest() {
        // Given
        final Long memberId = 1L;

        // When
        final List<ReservationWaitingWithOrder> reservationWaitingWithOrders = customReservationWaitingRepository.findAllReservationWaitingWithOrdersByMemberId(memberId);
        final ReservationWaitingWithOrder firstReservationWaitingWithOrder = reservationWaitingWithOrders.get(0);
        final ReservationWaitingWithOrder secondReservationWaitingWithOrder = reservationWaitingWithOrders.get(1);
        // Then
        assertAll(
                () -> assertThat(reservationWaitingWithOrders).hasSize(2),
                () -> assertThat(firstReservationWaitingWithOrder.reservationWaiting().getId()).isEqualTo(1L),
                () -> assertThat(firstReservationWaitingWithOrder.order()).isEqualTo(1),
                () -> assertThat(secondReservationWaitingWithOrder.reservationWaiting().getId()).isEqualTo(4L),
                () -> assertThat(secondReservationWaitingWithOrder.order()).isEqualTo(2)
        );
    }
}
