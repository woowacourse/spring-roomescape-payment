package roomescape.reservation.service.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.Fixture.HORROR_THEME;
import static roomescape.Fixture.MEMBER_JOJO;
import static roomescape.Fixture.MEMBER_KAKI;
import static roomescape.Fixture.RESERVATION_TIME_10_00;
import static roomescape.Fixture.TODAY;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import roomescape.common.util.DatabaseCleaner;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.controller.dto.response.MemberReservationResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.ReservationWithPayment;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.domain.WaitingWithRank;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;
import roomescape.reservation.repository.WaitingRepository;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
class MemberReservationServiceTest {

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
    private PaymentRepository paymentRepository;

    @Autowired
    private WaitingRepository waitingRepository;

    @Autowired
    private MemberReservationService memberReservationService;

    @AfterEach
    void init() {
        databaseCleaner.cleanUp();
    }

    @DisplayName("회원의 예약 목록과 예약 대기 목록을 조회한다.")
    @Test
    void findReservationsAndWaitings() {
        // given
        ReservationTime reservationTime = reservationTimeRepository.save(RESERVATION_TIME_10_00);
        Theme theme = themeRepository.save(HORROR_THEME);
        Member jojo = memberRepository.save(MEMBER_JOJO);
        Member kaki = memberRepository.save(MEMBER_KAKI);

        Reservation reservation = reservationRepository.save(new Reservation(jojo, TODAY, theme, reservationTime));
        Payment payment = paymentRepository.save(new Payment("paymentKey", "orderId", 1000L, reservation));

        waitingRepository.save(new Waiting(kaki, TODAY, theme, reservationTime));
        Waiting secondWait = waitingRepository.save(new Waiting(jojo, TODAY, theme, reservationTime));

        MemberReservationResponse expectedSuccess = MemberReservationResponse.toResponse(
                new ReservationWithPayment(reservation, payment));
        MemberReservationResponse expectedWait = MemberReservationResponse.toResponse(
                new WaitingWithRank(secondWait, 2L));

        // when
        List<MemberReservationResponse> jojoReservations = memberReservationService.findReservationsAndWaitings(1L);

        // then
        assertAll(
                () -> assertThat(jojoReservations).hasSize(2),
                () -> assertThat(jojoReservations).contains(expectedSuccess, expectedWait)
        );
    }
}
