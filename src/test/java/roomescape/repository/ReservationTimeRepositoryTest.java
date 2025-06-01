package roomescape.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.reservationitem.ReservationTime;
import roomescape.domain.reservationitem.ReservationTimeRepository;
import roomescape.test_util.RepositoryTest;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class ReservationTimeRepositoryTest extends RepositoryTest {

    @Autowired
    ReservationTimeRepository timeRepository;

    @DisplayName("id로 예약 시간을 조회한다.")
    @Test
    void findById() {
        // given
        ReservationTime savedTime = insertReservationTime(LocalTime.of(12, 12));

        // when
        Optional<ReservationTime> time = timeRepository.findById(savedTime.getId());

        // then
        assertAll(
                () -> assertThat(time).isPresent(),
                () -> assertThat(time.get().getStartAt()).isEqualTo(savedTime.getStartAt())
        );
    }

    @DisplayName("모든 예약 시간을 조회한다.")
    @Test
    void findAll() {
        // given
        ReservationTime savedTime1 = insertReservationTime(LocalTime.of(12, 12));
        ReservationTime savedTime2 = insertReservationTime(LocalTime.of(12, 13));
        ReservationTime savedTime3 = insertReservationTime(LocalTime.of(12, 14));

        // when
        List<ReservationTime> times = timeRepository.findAll();

        // then
        assertThat(times).hasSize(3).extracting(ReservationTime::getStartAt)
                .containsExactlyInAnyOrder(
                        savedTime1.getStartAt(),
                        savedTime2.getStartAt(),
                        savedTime3.getStartAt()
                );
    }

    @DisplayName("예약 시간을 저장한다.")
    @Test
    void save() {
        // given
        LocalTime newTime = LocalTime.of(16, 30);
        ReservationTime reservationTime = new ReservationTime(newTime);

        // when
        ReservationTime saved = timeRepository.save(reservationTime);

        // then
        Optional<ReservationTime> found = timeRepository.findById(saved.getId());
        assertAll(
                () -> assertThat(saved.getStartAt()).isEqualTo(newTime),
                () -> assertThat(found).isPresent(),
                () -> assertThat(found.get().getStartAt()).isEqualTo(newTime)
        );
    }

    @DisplayName("id로 예약시간을 삭제한다.")
    @Test
    void deleteById() {
        // given
        ReservationTime savedTime = insertReservationTime(LocalTime.of(12, 12));

        // when
        timeRepository.deleteById(savedTime.getId());

        // then
        assertThat(timeRepository.findAll()).isEmpty();
    }

    @DisplayName("이미 존재하는 예약 시간인지 확인한다.")
    @Test
    void existByStartAt() {
        // given
        LocalTime savedTimeValue = LocalTime.of(12, 12);
        insertReservationTime(savedTimeValue);

        // when
        final boolean expected = timeRepository.existsByStartAt(savedTimeValue);

        // then
        assertThat(expected).isTrue();
    }
}
