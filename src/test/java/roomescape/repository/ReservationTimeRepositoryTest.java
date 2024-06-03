package roomescape.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import roomescape.model.ReservationTime;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(scripts = {"/initialize_table.sql", "/test_data.sql"})
class ReservationTimeRepositoryTest {

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @DisplayName("모든 예약 시간을 조회한다")
    @Test
    void should_get_reservation_times() {
        List<ReservationTime> reservationTimes = reservationTimeRepository.findAll();

        assertThat(reservationTimes).hasSize(2);
    }

    @DisplayName("예약 시간을 추가한다")
    @Test
    void should_add_reservation_time() {
        reservationTimeRepository.save(new ReservationTime(LocalTime.of(12, 0)));

        long count = reservationTimeRepository.count();

        assertThat(count).isEqualTo(3);
    }

    @DisplayName("예약 시간을 삭제한다")
    @Test
    void should_delete_reservation_time() {
        reservationTimeRepository.deleteById(2L);

        long count = reservationTimeRepository.count();

        assertThat(count).isEqualTo(1);
    }

    @DisplayName("아이디에 해당하는 예약 시간을 조회한다.")
    @Test
    void should_get_reservation_time() {
        ReservationTime reservationTime = reservationTimeRepository.findById(1L).get();

        assertThat(reservationTime.getStartAt()).isEqualTo(LocalTime.of(10, 0));
    }

    @DisplayName("아이디가 존재하면 참을 반환한다.")
    @Test
    void should_return_true_when_id_exist() {
        boolean exists = reservationTimeRepository.existsById(1L);

        assertThat(exists).isTrue();
    }

    @DisplayName("시간에 해당하는 예약이 존재하면 참을 반환한다.")
    @Test
    void should_return_true_when_start_at_reservation_exist() {
        boolean exists = reservationTimeRepository.existsByStartAt(LocalTime.of(10, 0));

        assertThat(exists).isTrue();
    }


    @DisplayName("주어진 날짜와 테마에 해당하는 예약된 시간을 조회한다.")
    @Test
    void should_return_reserved_time_when_give_date_and_theme() {
        List<ReservationTime> allReservedTimes = reservationTimeRepository.findAllReservedTimes(LocalDate.now().plusDays(1), 1L);

        assertThat(allReservedTimes).hasSize(1);
    }

}
