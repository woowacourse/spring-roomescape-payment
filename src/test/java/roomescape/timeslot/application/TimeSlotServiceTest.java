package roomescape.timeslot.application;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.infrastructure.ReservationJpaRepository;
import roomescape.reservation.infrastructure.ReservationJpaRepositoryAdapter;
import roomescape.timeslot.domain.TimeSlotRepository;
import roomescape.timeslot.dto.request.TimeSlotConditionRequest;
import roomescape.timeslot.dto.response.TimeSlotConditionResponse;
import roomescape.timeslot.exception.TimeSlotException;
import roomescape.timeslot.infrastructure.TimeSlotJpaRepository;
import roomescape.timeslot.infrastructure.TimeSlotJpaRepositoryAdapter;
import roomescape.timeslot.application.TimeSlotServiceTest.TimeSlotConfig;

@DataJpaTest
@Import(TimeSlotConfig.class)
class TimeSlotServiceTest {

    @Autowired
    private TimeSlotService reservationTimeService;

    @DisplayName("이미 존재하는 예약이 있는 경우 예약 시간을 삭제할 수 없다.")
    @Test
    void can_not_delete_when_reservation_exists() {
        Assertions.assertThatThrownBy(() -> reservationTimeService.deleteTimeSlotById(1L))
            .isInstanceOf(TimeSlotException.class);
    }

    @DisplayName("예약 가능 시간 조회 테스트")
    @Test
    void time_condition_test() {
        LocalDate localDate = LocalDate.of(2025, 4, 28);
        Long themeId = 1L;

        List<TimeSlotConditionResponse> responses = reservationTimeService.getTimesWithCondition(
            new TimeSlotConditionRequest(localDate, themeId));

        Assertions.assertThat(responses).containsExactlyInAnyOrder(
            new TimeSlotConditionResponse(1L, LocalTime.of(10, 0), true),
            new TimeSlotConditionResponse(2L, LocalTime.of(11, 0), true),
            new TimeSlotConditionResponse(3L, LocalTime.of(12, 0), false)
        );
    }

    static class TimeSlotConfig {

        @Bean
        public ReservationRepository reservationRepository(ReservationJpaRepository reservationJpaRepository) {
            return new ReservationJpaRepositoryAdapter(reservationJpaRepository);
        }

        @Bean
        public TimeSlotRepository reservationTimeRepository(
            TimeSlotJpaRepository reservationTimeJpaRepository) {
            return new TimeSlotJpaRepositoryAdapter(reservationTimeJpaRepository);
        }

        @Bean
        public TimeSlotService reservationTimeService(ReservationRepository reservationRepository,
                                                             TimeSlotRepository reservationTimeRepository) {
            return new TimeSlotService(reservationRepository, reservationTimeRepository);
        }
    }
}
