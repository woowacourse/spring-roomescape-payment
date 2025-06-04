package roomescape.reservation.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import roomescape.reservation.application.dto.MemberReservationRequest;
import roomescape.reservation.application.dto.ReservationResponse;
import roomescape.reservation.domain.repository.ReservationRepository;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class ReservationSchedulerTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationRepository reservationRepository;

    @DisplayName("결제 대기상태에서 대기 시간을 초과하면 삭제가 되어야 한다.")
    @Test
    void payment_pending_and_over_waiting_time_then_delete_reservation() throws InterruptedException {
        // given
        MemberReservationRequest memberReservationRequest = new MemberReservationRequest(LocalDate.now().plusDays(1), 1L, 1L);
        ReservationResponse reservationResponse = reservationService.addMemberReservation(memberReservationRequest, 1L);
        assertThat(reservationRepository.findById(reservationResponse.id())).isNotEmpty();

        // when
        Thread.sleep(2000);

        // then
        assertThat(reservationRepository.findById(reservationResponse.id())).isEmpty();
    }

}
