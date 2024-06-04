package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.util.Fixture.HORROR_THEME;
import static roomescape.util.Fixture.JOJO;
import static roomescape.util.Fixture.KAKI;
import static roomescape.util.Fixture.LOGIN_MEMBER_KAKI;
import static roomescape.util.Fixture.RESERVATION_HOUR_10;
import static roomescape.util.Fixture.RESERVATION_HOUR_11;
import static roomescape.util.Fixture.TODAY;
import static roomescape.util.Fixture.TOMORROW;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import roomescape.auth.dto.LoginMember;
import roomescape.config.DatabaseCleaner;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.dto.AdminReservationSaveRequest;
import roomescape.reservation.dto.ReservationWaitingResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class AdminReservationServiceTest {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private AdminReservationService adminReservationService;

    @AfterEach
    void init() {
        databaseCleaner.cleanUp();
    }

    @DisplayName("중복된 예약을 추가할 경우 예외가 발생한다.")
    @Test
    void saveExceptionByDuplicatedReservation() {
        ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_HOUR_10);
        Theme horrorTheme = themeRepository.save(HORROR_THEME);
        memberRepository.save(KAKI);

        LoginMember loginMember = LOGIN_MEMBER_KAKI;
        AdminReservationSaveRequest adminReservationSaveRequest =
                new AdminReservationSaveRequest(TODAY, horrorTheme.getId(), hour10.getId(), loginMember.id());

        adminReservationService.save(adminReservationSaveRequest, loginMember);

        assertThatThrownBy(() -> adminReservationService.save(adminReservationSaveRequest, loginMember))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("현재 날짜 이후의 예약들을 예약 날짜, 예약 시간, 예약 추가 순으로 정렬해 예약 대기 목록을 조회한다.")
    @Test
    void findWaitingReservations() {
        ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_HOUR_10);
        ReservationTime hour11 = reservationTimeRepository.save(RESERVATION_HOUR_11);

        Theme horrorTheme = themeRepository.save(HORROR_THEME);

        Member kaki = memberRepository.save(KAKI);
        Member jojo = memberRepository.save(JOJO);

        Reservation reservation1 = new Reservation(kaki, TOMORROW, horrorTheme, hour11, ReservationStatus.WAIT);
        Reservation reservation2 = new Reservation(kaki, TODAY, horrorTheme, hour10, ReservationStatus.WAIT);
        Reservation reservation3 = new Reservation(kaki, TODAY, horrorTheme, hour11, ReservationStatus.WAIT);
        Reservation reservation4 = new Reservation(jojo, TOMORROW, horrorTheme, hour10, ReservationStatus.WAIT);

        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);
        reservationRepository.save(reservation3);
        reservationRepository.save(reservation4);

        List<ReservationWaitingResponse> waitingReservations = adminReservationService.findWaitingReservations();

        assertThat(waitingReservations).extracting(ReservationWaitingResponse::id)
                .containsExactly(reservation2.getId(), reservation3.getId(), reservation4.getId(), reservation1.getId());
    }
}
