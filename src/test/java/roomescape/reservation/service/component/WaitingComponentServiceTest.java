package roomescape.reservation.service.component;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.Fixture.HORROR_THEME;
import static roomescape.Fixture.MEMBER_JOJO;
import static roomescape.Fixture.MEMBER_KAKI;
import static roomescape.Fixture.RESERVATION_TIME_10_00;
import static roomescape.Fixture.TODAY;
import static roomescape.Fixture.TOMORROW;
import static roomescape.reservation.domain.Status.PAYMENT_PENDING;

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
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;
import roomescape.reservation.repository.WaitingRepository;
import roomescape.reservation.service.dto.request.WaitingReservationRequest;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
class WaitingComponentServiceTest {

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
    private WaitingRepository waitingRepository;

    @Autowired
    private WaitingComponentService waitingComponentService;

    @AfterEach
    void init() {
        databaseCleaner.cleanUp();
    }

    @DisplayName("예약 대기 시, 저장된 예약이 없다면 예외가 발생한다.")
    @Test
    void waitingReservationWithNotSavedReservationExceptionTest() {
        Theme horror = themeRepository.save(HORROR_THEME);
        ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_TIME_10_00);
        Member jojo = memberRepository.save(MEMBER_JOJO);

        WaitingReservationRequest saveRequest = new WaitingReservationRequest(
                jojo.getId(),
                TODAY,
                horror.getId(),
                hour10.getId()
        );

        assertThatThrownBy(() -> waitingComponentService.save(saveRequest))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("예약 대기 승인 시, 확정된 예약이 없으면 결제 대기 상태로 변경된다.")
    @Test
    void approveReservation() {
        ReservationTime reservationTime = reservationTimeRepository.save(RESERVATION_TIME_10_00);
        Theme theme = themeRepository.save(HORROR_THEME);
        Member jojo = memberRepository.save(MEMBER_JOJO);

        Waiting waiting = waitingRepository.save(new Waiting(jojo, TOMORROW, theme, reservationTime));
        waitingComponentService.approveReservation(waiting.getId());

        Waiting afterUpdate = waitingRepository.findById(waiting.getId()).get();
        assertThat(afterUpdate.getStatus()).isEqualTo(PAYMENT_PENDING);
    }

    @DisplayName("예약 대기 승인 시, 확정된 예약이 있으면 예외가 발생한다.")
    @Test
    void approveReservationWithDuplicatedReservation() {
        ReservationTime reservationTime = reservationTimeRepository.save(RESERVATION_TIME_10_00);
        Theme theme = themeRepository.save(HORROR_THEME);
        Member jojo = memberRepository.save(MEMBER_JOJO);
        Member kaki = memberRepository.save(MEMBER_KAKI);

        reservationRepository.save(new Reservation(kaki, TOMORROW, theme, reservationTime));
        Waiting waiting = waitingRepository.save(new Waiting(jojo, TOMORROW, theme, reservationTime));

        assertThatThrownBy(() -> waitingComponentService.approveReservation(waiting.getId()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
