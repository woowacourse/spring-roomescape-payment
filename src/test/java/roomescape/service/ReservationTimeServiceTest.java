package roomescape.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import roomescape.controller.request.ReservationTimeRequest;
import roomescape.controller.response.IsReservedTimeResponse;
import roomescape.exception.BadRequestException;
import roomescape.exception.DuplicatedException;
import roomescape.exception.NotFoundException;
import roomescape.model.ReservationTime;
import roomescape.repository.ReservationTimeRepository;

import java.time.LocalTime;
import java.util.List;

import static java.time.LocalDate.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Sql(scripts = {"/initialize_table.sql", "/test_data.sql"})
class ReservationTimeServiceTest {

    private ReservationTimeRepository reservationTimeRepository;
    private ReservationTimeService reservationTimeService;

    @Autowired
    public ReservationTimeServiceTest(ReservationTimeRepository reservationTimeRepository, ReservationTimeService reservationTimeService) {
        this.reservationTimeRepository = reservationTimeRepository;
        this.reservationTimeService = reservationTimeService;
    }

    @DisplayName("모든 예약 시간을 반환한다")
    @Test
    void should_return_all_reservation_times() {
        List<ReservationTime> reservationTimes = reservationTimeService.findAllReservationTimes();

        assertThat(reservationTimes).hasSize(2);
    }

    @DisplayName("아이디에 해당하는 예약 시간을 반환한다.")
    @Test
    void should_get_reservation_time() {
        ReservationTime reservationTime = reservationTimeService.findReservationTime(2);

        assertThat(reservationTime.getStartAt()).isEqualTo(LocalTime.of(11, 0));
    }

    @DisplayName("예약 시간을 추가한다")
    @Test
    void should_add_reservation_times() {
        reservationTimeService.addReservationTime(new ReservationTimeRequest(LocalTime.of(13, 0)));

        List<ReservationTime> allReservationTimes = reservationTimeRepository.findAll();

        assertThat(allReservationTimes).hasSize(3);
    }

    @DisplayName("예약 시간을 삭제한다")
    @Test
    void should_remove_reservation_times() {
        reservationTimeService.deleteReservationTime(2L);

        List<ReservationTime> allReservationTimes = reservationTimeRepository.findAll();
        assertThat(allReservationTimes).hasSize(1);
    }

    @DisplayName("존재하지 않는 시간이면 예외를 발생시킨다.")
    @Test
    void should_throw_exception_when_not_exist_id() {
        assertThatThrownBy(() -> reservationTimeService.deleteReservationTime(10000000))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("[ERROR] 아이디가 10000000인 예약 시간이 존재하지 않습니다.");
    }

    @DisplayName("특정 시간에 대해 예약이 존재하는데, 그 시간을 삭제하려 할 때 예외가 발생한다.")
    @Test
    void should_throw_exception_when_exist_reservation_using_time() {
        assertThatThrownBy(() -> reservationTimeService.deleteReservationTime(1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("[ERROR] 해당 시간에 예약이 존재하여 삭제할 수 없습니다.");
    }

    @DisplayName("존재하는 시간을 추가하려 할 때 예외가 발생한다.")
    @Test
    void should_throw_exception_when_add_exist_time() {
        LocalTime reservedTime = LocalTime.of(10, 0);
        ReservationTimeRequest request = new ReservationTimeRequest(reservedTime);

        assertThatThrownBy(() -> reservationTimeService.addReservationTime(request))
                .isInstanceOf(DuplicatedException.class)
                .hasMessage("[ERROR] 이미 존재하는 시간입니다.");
    }

    @DisplayName("예약 가능 상태를 담은 시간 정보를 반환한다.")
    @Test
    void should_return_times_with_book_state() {
        List<IsReservedTimeResponse> times = reservationTimeService.getIsReservedTime(now().plusDays(1), 1L);

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(times).hasSize(2);
            softAssertions.assertThat(times).containsOnly(
                    new IsReservedTimeResponse(1L, LocalTime.of(10, 0), true),
                    new IsReservedTimeResponse(2L, LocalTime.of(11, 0), false));
        });
    }
}
