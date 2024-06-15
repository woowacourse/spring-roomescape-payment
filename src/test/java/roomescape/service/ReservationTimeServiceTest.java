package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.BasicAcceptanceTest;
import roomescape.dto.response.reservation.AvailableTimeResponse;
import roomescape.dto.request.reservation.ReservationTimeRequest;
import roomescape.exception.RoomescapeException;

class ReservationTimeServiceTest extends BasicAcceptanceTest {
    @Autowired
    private ReservationTimeService reservationTimeService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DisplayName("요청으로 들어온 예약 시간이 예외 조건에 해당되지 않을 때 해당 예약 시간을 저장한다.")
    @Test
    void save() {
        reservationTimeService.save(new ReservationTimeRequest(LocalTime.of(10, 0)));

        assertThat(reservationTimeService.findAll()).hasSize(1);
    }

    @DisplayName("이미 존재하는 예약 시간을 생성 요청하면 예외가 발생한다.")
    @Test
    void duplicateTime() {
        jdbcTemplate.update("INSERT INTO reservation_time (start_at) VALUES ('10:00')");
        ReservationTimeRequest reservationTimeRequest = new ReservationTimeRequest(LocalTime.of(10, 0));

        assertThatThrownBy(() -> reservationTimeService.save(reservationTimeRequest))
                .isInstanceOf(RoomescapeException.class)
                .hasMessageContaining("중복된 예약 시간입니다. ");
    }

    @DisplayName("해당 id의 예약 시간을 삭제한다.")
    @Test
    void deleteById() {
        jdbcTemplate.update("INSERT INTO reservation_time (start_at) VALUES ('10:00')");
        reservationTimeService.deleteById(1L);

        assertThat(reservationTimeService.findAll()).hasSize(0);
    }

    @DisplayName("예약에 사용된 예약 시간을 삭제 요청하면, 예외가 발생한다.")
    @Test
    void invalidReservationWhenReservedInTime() {
        jdbcTemplate.update(
                "INSERT INTO member (name, email, password, role) VALUES ('회원', 'member@wooteco.com', 'wootecoCrew6!', 'BASIC')");
        jdbcTemplate.update(
                "INSERT INTO theme (name, description, thumbnail, price) VALUES ('name1', 'description1', 'thumbnail1', 1000)");
        jdbcTemplate.update("INSERT INTO reservation_time (start_at) VALUES ('10:00')");
        jdbcTemplate.update(
                "INSERT INTO reservation (date, member_id, time_id, theme_id, status) VALUES (CURRENT_DATE + INTERVAL '1' DAY, 1, 1, 1, 'RESERVATION')");

        assertThatThrownBy(() -> reservationTimeService.deleteById(1L))
                .isInstanceOf(RoomescapeException.class)
                .hasMessageContaining("해당 예약 시간에 연관된 예약이 존재하여 삭제할 수 없습니다.");
    }

    @DisplayName("존재하지 않는 예약 시간을 삭제 요청하면, 예외가 발생한다.")
    @Test
    void invalidNotExistTime() {
        assertThatThrownBy(() -> reservationTimeService.deleteById(99L))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage("존재하지 않는 예약 시간입니다.");
    }

    @DisplayName("예약이 있는 시간과 없는 시간을 구분한다.")
    @Test
    void findAvailableTimes() {
        jdbcTemplate.update(
                "INSERT INTO member (name, email, password, role) VALUES ('회원', 'member@wooteco.com', 'wootecoCrew6!', 'BASIC')");
        jdbcTemplate.update(
                "INSERT INTO theme (name, description, thumbnail, price) VALUES ('name1', 'description1', 'thumbnail1', 1000)");
        jdbcTemplate.update("INSERT INTO reservation_time (start_at) VALUES ('10:00')");
        jdbcTemplate.update("INSERT INTO reservation_time (start_at) VALUES ('11:00')");
        jdbcTemplate.update(
                "INSERT INTO reservation (date, member_id, time_id, theme_id, status) VALUES (CURRENT_DATE + INTERVAL '1' DAY, 1, 1, 1, 'RESERVATION')");
        List<AvailableTimeResponse> availableTimeResponses = reservationTimeService.findAvailableTimes(
                LocalDate.now().plusDays(1), 1);

        Assertions.assertAll(
                () -> assertThat(availableTimeResponses.get(0).isBooked()).isTrue(),
                () -> assertThat(availableTimeResponses.get(1).isBooked()).isFalse()
        );
    }
}
