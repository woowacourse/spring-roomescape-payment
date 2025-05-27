package roomescape.waiting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import roomescape.auth.web.exception.NotAuthorizationException;
import roomescape.common.CleanUp;
import roomescape.fixture.db.MemberDbFixture;
import roomescape.fixture.db.ReservationDbFixture;
import roomescape.global.exception.NotFoundException;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.repository.ReservationRepository;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class WaitingServiceTest {

    @Autowired
    private WaitingService waitingService;

    @Autowired
    private ReservationDbFixture reservationDbFixture;

    @Autowired
    private MemberDbFixture memberDbFixture;

    @Autowired
    private CleanUp cleanUp;
    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        cleanUp.all();
    }

    @Test
    void 대기_예약을_삭제한다() {
        // given
        Reservation savedWaiting = reservationDbFixture.waiting();

        // when
        waitingService.delete(savedWaiting.getId());

        // then
        assertThat(reservationRepository.existsById(savedWaiting.getId())).isFalse();
    }

    @Test
    void 존재하지_않는_대기_예약을_삭제할_수_없다() {
        // when & then
        assertThatThrownBy(() -> waitingService.delete(999L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void 본인의_대기_예약을_취소한다() {
        // given
        Reservation savedWaiting = reservationDbFixture.waiting();
        Member 유저1 = savedWaiting.getReserver();

        // when
        waitingService.cancelWaiting(savedWaiting.getId(), 유저1.getId());

        // then
        assertThat(reservationRepository.findById(savedWaiting.getId()).get().getStatus()).isEqualTo(
                ReservationStatus.CANCELED);
    }

    @Test
    void 대기_예약이_아닌데_삭제를_하면_예외를_발생한다() {
        // given
        Reservation reservation = reservationDbFixture.reserve();
        Member 유저1 = reservation.getReserver();

        // when & then
        assertThatThrownBy(() -> waitingService.cancelWaiting(reservation.getId(), 유저1.getId())).isInstanceOf(
                NotFoundException.class);
    }

    @Test
    void 다른_사용자의_대기_예약을_삭제할_수_없다() {
        // given
        Member 유저2 = memberDbFixture.유저2_생성();

        Reservation savedWaiting = reservationDbFixture.waiting();

        // when & then
        assertThatThrownBy(() -> waitingService.cancelWaiting(savedWaiting.getId(), 유저2.getId())).isInstanceOf(
                NotAuthorizationException.class);
    }


}
