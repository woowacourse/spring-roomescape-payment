package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.util.Fixture.HORROR_THEME;
import static roomescape.util.Fixture.HOUR_10;
import static roomescape.util.Fixture.KAKI;
import static roomescape.util.Fixture.RESERVATION_HOUR_10;
import static roomescape.util.Fixture.RESERVATION_HOUR_11;
import static roomescape.util.Fixture.TODAY;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import roomescape.common.exception.EntityNotExistException;
import roomescape.config.DatabaseCleaner;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.dto.AvailableReservationTimeResponse;
import roomescape.reservation.dto.TimeSaveRequest;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class ReservationTimeServiceTest {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationTimeService reservationTimeService;

    @AfterEach
    void init() {
        databaseCleaner.cleanUp();
    }

    @DisplayName("중복된 예약 시간을 추가할 경우 예외가 발생한다.")
    @Test
    void saveExceptionTest() {
        reservationTimeService.save(new TimeSaveRequest(HOUR_10));

        assertThatThrownBy(() -> reservationTimeService.save(new TimeSaveRequest(HOUR_10)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("예약 시간 아이디로 조회 시 존재하지 않는 아이디면 예외가 발생한다.")
    @Test
    void findByIdExceptionTest() {
        assertThatThrownBy(() -> reservationTimeService.findById(1L))
                .isInstanceOf(EntityNotExistException.class);
    }

    @DisplayName("예약 가능한 시간을 조회한다.")
    @Test
    void findAvailableTimesTest() {
        ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_HOUR_10);
        ReservationTime hour11 = reservationTimeRepository.save(RESERVATION_HOUR_11);

        Theme horrorTheme = themeRepository.save(HORROR_THEME);

        Member kaki = memberRepository.save(KAKI);

        Reservation reservation = reservationRepository.save(new Reservation(kaki, TODAY, horrorTheme, hour10, ReservationStatus.SUCCESS));

        List<AvailableReservationTimeResponse> availableTimes = reservationTimeService.findAvailableTimes(
                reservation.getDate(),
                HORROR_THEME.getId()
        );

        assertThat(availableTimes).containsExactly(
                AvailableReservationTimeResponse.toResponse(hour10, true),
                AvailableReservationTimeResponse.toResponse(hour11, false)
        );
    }

    @DisplayName("이미 해당 시간으로 예약 되있을 경우 삭제 시 예외가 발생한다.")
    @Test
    void deleteExceptionTest() {
        ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_HOUR_10);

        Theme horrorTheme = themeRepository.save(HORROR_THEME);

        Member kaki = memberRepository.save(KAKI);

        reservationRepository.save(new Reservation(kaki, TODAY, horrorTheme, hour10, ReservationStatus.SUCCESS));

        assertThatThrownBy(() -> reservationTimeService.delete(hour10.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
