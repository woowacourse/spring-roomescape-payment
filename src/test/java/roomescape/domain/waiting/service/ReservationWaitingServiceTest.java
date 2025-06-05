package roomescape.domain.waiting.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import roomescape.domain.member.Role;
import roomescape.domain.member.entity.Member;
import roomescape.domain.member.repository.FakeMemberRepository;
import roomescape.domain.member.repository.MemberRepositoryInterface;
import roomescape.domain.reservation.entity.Reservation;
import roomescape.domain.reservation.repository.FakeReservationRepository;
import roomescape.domain.reservation.repository.ReservationRepositoryInterface;
import roomescape.domain.theme.entity.Theme;
import roomescape.domain.theme.repository.FakeThemeRepository;
import roomescape.domain.theme.repository.ThemeRepositoryInterface;
import roomescape.domain.time.entity.ReservationTime;
import roomescape.domain.time.repository.FakeReservationTimeRepository;
import roomescape.domain.time.repository.ReservationTimeRepositoryInterface;
import roomescape.domain.waiting.entity.Waiting;
import roomescape.domain.waiting.exception.WaitingNotAllowedException;
import roomescape.domain.waiting.repository.FakeWaitingRepository;
import roomescape.domain.waiting.repository.WaitingRepositoryInterface;
import roomescape.infrastructure.exception.DataNotFoundException;

public class ReservationWaitingServiceTest {

    private final ReservationRepositoryInterface reservationRepository = new FakeReservationRepository();
    private final ReservationTimeRepositoryInterface reservationTimeRepository = new FakeReservationTimeRepository();
    private final ThemeRepositoryInterface themeRepository = new FakeThemeRepository();
    private final MemberRepositoryInterface memberRepository = new FakeMemberRepository();
    private final WaitingRepositoryInterface waitingRepository = new FakeWaitingRepository();
    private final ReservationWaitingService reservationWaitingService = new ReservationWaitingService(
            reservationRepository,
            reservationTimeRepository,
            themeRepository,
            waitingRepository
    );

    @Test
    void 예약_대기_저장() {
        // given
        final Member savedMember = memberRepository.save(new Member("우가", "wooga@gmail.com", "1234", Role.USER));
        final LocalTime time = LocalTime.parse("20:00");
        final LocalDate date = LocalDate.parse("2026-11-28");
        final ReservationTime savedTime = reservationTimeRepository.save(new ReservationTime(time));

        final String themeName = "공포";
        final String description = "무섭다";
        final String thumbnail = "귀신사진";
        final Theme savedTheme = themeRepository.save(new Theme(themeName, description, thumbnail));

        reservationRepository.save(
                new Reservation(savedMember, date, savedTime, savedTheme)
        );
        //when
        final Waiting savedWaiting = reservationWaitingService.createWaitingReservation(
                savedMember,
                date,
                savedTime.getId(),
                savedTheme.getId()
        );

        //then
        Assertions.assertThat(savedWaiting.getId()).isEqualTo(1);
    }

    @Test
    void 예약_대기_저장할_때_예약이_존재하지_않으면_예외_발생() {
        // given
        final Member savedMember = memberRepository.save(new Member("우가", "wooga@gmail.com", "1234", Role.USER));
        final LocalTime time = LocalTime.parse("20:00");
        final LocalDate date = LocalDate.parse("2026-11-28");
        final ReservationTime savedTime = reservationTimeRepository.save(new ReservationTime(time));

        final String themeName = "공포";
        final String description = "무섭다";
        final String thumbnail = "귀신사진";
        final Theme savedTheme = themeRepository.save(new Theme(themeName, description, thumbnail));

        //when & then
        Assertions.assertThatThrownBy(
                        () -> reservationWaitingService.createWaitingReservation(savedMember, date, savedTime.getId(),
                                savedTheme.getId()))
                .isInstanceOf(WaitingNotAllowedException.class);
    }

    @Test
    void 멤버를_기준으로_예약_대기_찾기() {
        // given
        final Member savedMember = memberRepository.save(new Member("우가", "wooga@gmail.com", "1234", Role.USER));
        final LocalTime time = LocalTime.parse("20:00");
        final LocalDate date = LocalDate.parse("2026-11-28");
        final ReservationTime savedTime = reservationTimeRepository.save(new ReservationTime(time));

        final String themeName = "공포";
        final String description = "무섭다";
        final String thumbnail = "귀신사진";
        final Theme savedTheme = themeRepository.save(new Theme(themeName, description, thumbnail));

        final Waiting savedWaiting = waitingRepository.save(new Waiting(savedMember, savedTime, savedTheme, date));

        // when
        final List<Waiting> waitings = reservationWaitingService.findWaitingByMember(savedMember);

        // then
        Assertions.assertThat(waitings).containsExactly(savedWaiting);
    }

    @Test
    void 아이디를_기준으로_예약_대기_삭제() {
        // given
        final Member savedMember = memberRepository.save(new Member("우가", "wooga@gmail.com", "1234", Role.USER));
        final LocalTime time = LocalTime.parse("20:00");
        final LocalDate date = LocalDate.parse("2026-11-28");
        final ReservationTime savedTime = reservationTimeRepository.save(new ReservationTime(time));

        final String themeName = "공포";
        final String description = "무섭다";
        final String thumbnail = "귀신사진";
        final Theme savedTheme = themeRepository.save(new Theme(themeName, description, thumbnail));

        final Waiting savedWaiting = waitingRepository.save(new Waiting(savedMember, savedTime, savedTheme, date));

        // when
        reservationWaitingService.deleteWaitingById(savedWaiting.getId());

        // then
        Assertions.assertThat(waitingRepository.findByMember(savedMember))
                .doesNotContain(savedWaiting);
    }

    @Test
    void 아이디를_기준으로_예약_대기_삭제_예외_발생() {

        // when & then
        Assertions.assertThatThrownBy(() -> reservationWaitingService.deleteWaitingById(Long.MAX_VALUE))
                .isInstanceOf(DataNotFoundException.class);
    }

    @Test
    void 예약_대기_정보에_따른_순번_조회() {
        // given
        final Member savedMember = memberRepository.save(new Member("우가", "wooga@gmail.com", "1234", Role.USER));
        final LocalTime time = LocalTime.parse("20:00");
        final LocalDate date = LocalDate.parse("2026-11-28");
        final ReservationTime savedTime = reservationTimeRepository.save(new ReservationTime(time));

        final String themeName = "공포";
        final String description = "무섭다";
        final String thumbnail = "귀신사진";
        final Theme savedTheme = themeRepository.save(new Theme(themeName, description, thumbnail));

        waitingRepository.save(new Waiting(savedMember, savedTime, savedTheme, date));
        waitingRepository.save(new Waiting(savedMember, savedTime, savedTheme, date));
        waitingRepository.save(new Waiting(savedMember, savedTime, savedTheme, date));
        waitingRepository.save(new Waiting(savedMember, savedTime, savedTheme, date));
        final Waiting savedWaiting = waitingRepository.save(new Waiting(savedMember, savedTime, savedTheme, date));

        // when
        final long count = reservationWaitingService.getRankInWaiting(savedWaiting);

        // then
        Assertions.assertThat(count).isEqualTo(5);
    }

    @Test
    void 예약_대기_정보에서_앞_번호가_사라지면_번호_당겨지는_순번_조회() {
        // given
        final Member savedMember = memberRepository.save(new Member("우가", "wooga@gmail.com", "1234", Role.USER));
        final LocalTime time = LocalTime.parse("20:00");
        final LocalDate date = LocalDate.parse("2026-11-28");
        final ReservationTime savedTime = reservationTimeRepository.save(new ReservationTime(time));

        final String themeName = "공포";
        final String description = "무섭다";
        final String thumbnail = "귀신사진";
        final Theme savedTheme = themeRepository.save(new Theme(themeName, description, thumbnail));

        waitingRepository.save(new Waiting(savedMember, savedTime, savedTheme, date));
        waitingRepository.save(new Waiting(savedMember, savedTime, savedTheme, date));
        final Waiting savedWaiting1 = waitingRepository.save(new Waiting(savedMember, savedTime, savedTheme, date));
        waitingRepository.save(new Waiting(savedMember, savedTime, savedTheme, date));
        final Waiting savedWaiting2 = waitingRepository.save(new Waiting(savedMember, savedTime, savedTheme, date));

        waitingRepository.deleteById(savedWaiting1.getId());

        // when
        final long count = reservationWaitingService.getRankInWaiting(savedWaiting2);

        // then
        Assertions.assertThat(count).isEqualTo(4);
    }
}
