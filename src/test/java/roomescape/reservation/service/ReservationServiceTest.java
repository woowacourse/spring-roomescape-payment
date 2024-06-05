package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.Fixture.HORROR_THEME;
import static roomescape.Fixture.MEMBER_JOJO;
import static roomescape.Fixture.MEMBER_KAKI;
import static roomescape.Fixture.RESERVATION_TIME_10_00;
import static roomescape.Fixture.TODAY;
import static roomescape.reservation.domain.Status.SUCCESS;
import static roomescape.reservation.domain.Status.WAIT;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import roomescape.auth.domain.Role;
import roomescape.auth.dto.LoginMember;
import roomescape.common.config.DatabaseCleaner;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.controller.dto.response.MemberReservationResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.ReservationWithRank;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;
import roomescape.reservation.service.dto.request.ReservationPaymentRequest;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
class ReservationServiceTest {

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
    private ReservationService reservationService;

    @AfterEach
    void init() {
        databaseCleaner.cleanUp();
    }

    @DisplayName("존재하지 않는 예약 시간에 예약을 하면 예외가 발생한다.")
    @Test
    void notExistReservationTimeIdExceptionTest() {
        Theme horror = themeRepository.save(HORROR_THEME);
        Member jojo = memberRepository.save(MEMBER_JOJO);

        ReservationPaymentRequest saveRequest = new ReservationPaymentRequest(
                jojo.getId(), TODAY, horror.getId(), 1L, "paymentKey", "orderId", 1000L
        );

        assertThatThrownBy(() -> reservationService.save(saveRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @DisplayName("예약 아이디로 조회 시 존재하지 않는 아이디면 예외가 발생한다.")
    @Test
    void findByIdExceptionTest() {
        assertThatThrownBy(() -> reservationService.findById(1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("회원의 예약 목록과 예약 대기 목록을 조회한다.")
    @Test
    void findReservationsAndWaitingsByMember() {
        // given
        ReservationTime reservationTime = reservationTimeRepository.save(RESERVATION_TIME_10_00);
        Theme theme = themeRepository.save(HORROR_THEME);
        Member jojo = memberRepository.save(MEMBER_JOJO);
        Member kaki = memberRepository.save(MEMBER_KAKI);

        Reservation success = reservationRepository.save(
                new Reservation(jojo, TODAY, theme, reservationTime, SUCCESS, "paymentKey", 1000L));
        reservationRepository.save(new Reservation(kaki, TODAY, theme, reservationTime, WAIT, "paymentKey", 1000L));
        Reservation secondWait = reservationRepository.save(
                new Reservation(jojo, TODAY, theme, reservationTime, WAIT, "paymentKey", 1000L));

        MemberReservationResponse expectedSuccess = MemberReservationResponse.toResponse(
                new ReservationWithRank(success, 0)
        );
        MemberReservationResponse expectedWait = MemberReservationResponse.toResponse(
                new ReservationWithRank(secondWait, 2)
        );

        // when
        LoginMember loginJojo = new LoginMember(1L, Role.MEMBER, jojo.getName(), jojo.getEmail());
        List<MemberReservationResponse> jojoReservations = reservationService.findReservationsAndWaitingsByMember(
                loginJojo);

        // then
        assertAll(
                () -> assertThat(jojoReservations).hasSize(2),
                () -> assertThat(jojoReservations).containsExactly(expectedSuccess, expectedWait)
        );
    }
}
