package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.time.LocalTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.domain.repository.ReservationTimeRepository;
import roomescape.reservation.dto.request.ReservationTimeRequest;
import roomescape.system.exception.RoomEscapeException;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.repository.ThemeRepository;

@SpringBootTest
@Import(ReservationTimeService.class)
@Sql(scripts = "/truncate.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class ReservationTimeServiceTest {

    @Autowired
    private ReservationTimeService reservationTimeService;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("중복된 예약 시간을 등록하는 경우 예외가 발생한다.")
    void duplicateTimeFail() {
        // given
        reservationTimeRepository.save(new ReservationTime(LocalTime.of(12, 30)));

        // when & then
        assertThatThrownBy(() -> reservationTimeService.addTime(new ReservationTimeRequest(LocalTime.of(12, 30))))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 시간을 조회하면 예외가 발생한다.")
    void findTimeByIdFail() {
        // given
        ReservationTime saved = reservationTimeRepository.save(new ReservationTime(LocalTime.of(12, 30)));

        // when
        Long invalidTimeId = saved.getId() + 1;

        // when & then
        assertThatThrownBy(() -> reservationTimeService.findTimeById(invalidTimeId))
                .isInstanceOf(RoomEscapeException.class);
    }

    @Test
    @DisplayName("삭제하려는 시간에 예약이 존재하면 예외를 발생한다.")
    void usingTimeDeleteFail() {
        // given
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(1L).withNano(0);
        ReservationTime reservationTime = reservationTimeRepository.save(
                new ReservationTime(localDateTime.toLocalTime()));
        Theme theme = themeRepository.save(new Theme("테마명", "설명", "썸네일URL"));
        Member member = memberRepository.save(new Member("name", "email@email.com", "password", Role.MEMBER));

        // when
        reservationRepository.save(new Reservation(localDateTime.toLocalDate(), reservationTime, theme, member,
                ReservationStatus.CONFIRMED));

        // then
        assertThatThrownBy(() -> reservationTimeService.removeTimeById(reservationTime.getId()))
                .isInstanceOf(RoomEscapeException.class);
    }
}
