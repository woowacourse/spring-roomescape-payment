package roomescape.reservation.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import roomescape.registration.domain.reservation.dto.ReservationResponse;
import roomescape.registration.domain.reservation.repository.ReservationRepository;
import roomescape.registration.dto.PaymentResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Sql(scripts = "/data-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DataJpaTest
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @DisplayName("주어진 회원 아이디로 예약 정보와 결제 정보를 한번에 조회한다.")
    @Test
    void findAllReservationsWithPaymentsByMemberId() {
        List<ReservationResponse> result = reservationRepository.findAllReservationsWithPaymentsByMemberId(2L);

        assertThat(result).containsExactlyInAnyOrder(
                new ReservationResponse(3, "일반", "polla", LocalDate.parse("2024-05-01"), LocalTime.parse("13:40"),
                        new PaymentResponse(3L, LocalDateTime.parse("2024-04-30T12:00:00"), 10000L)),
                new ReservationResponse(4, "일반", "dobby", LocalDate.parse("2024-05-02"), LocalTime.parse("13:40"),
                        new PaymentResponse(4L, LocalDateTime.parse("2024-04-30T13:00:00"), 15000L)),
                new ReservationResponse(6, "일반", "polla", LocalDate.parse("2024-05-04"), LocalTime.parse("15:40"),
                        new PaymentResponse(null, null, 10000L))
                );
    }
}
