package roomescape.reservation.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.auth.domain.AuthInfo;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.ReservationFixture;
import roomescape.fixture.ReservationTimeFixture;
import roomescape.member.domain.Member;
import roomescape.reservation.controller.dto.ReservationRequest;
import roomescape.reservation.controller.dto.ReservationWithStatus;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationSlot;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.domain.repository.ReservationSlotRepository;
import roomescape.util.ServiceTest;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.fixture.MemberFixture.getMemberChoco;
import static roomescape.fixture.MemberFixture.getMemberTacan;
import static roomescape.fixture.ReservationSlotFixture.getNextDayReservationSlot;
import static roomescape.fixture.ThemeFixture.getTheme1;

@DisplayName("예약 대기 로직 테스트")
class WaitingReservationServiceTest extends ServiceTest {

    @Autowired
    ReservationService reservationService;
    @Autowired
    WaitingReservationService waitingReservationService;
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    ReservationSlotRepository reservationSlotRepository;

    @DisplayName("예약 대기 성공")
    @Test
    void existSameReservation() {
        //given
        ReservationSlot reservationSlot = getNextDayReservationSlot(ReservationTimeFixture.get1PM(), getTheme1());
        Member choco = getMemberChoco();
        Member tacan = getMemberTacan();

        reservationSlotRepository.save(reservationSlot);
        reservationRepository.save(new Reservation(choco, reservationSlot));

        //when
        waitingReservationService.reserveWaiting(new ReservationRequest(
                reservationSlot.getDate().format(DateTimeFormatter.ISO_DATE),
                reservationSlot.getTime().getId(),
                reservationSlot.getTheme().getId(
                )), tacan.getId());

        List<Reservation> allByMember = reservationRepository.findAllByMember(tacan);
        Reservation addedReservation = allByMember
                .stream()
                .filter(reservation -> Objects.equals(reservation.getReservationSlot().getId(), reservationSlot.getId()))
                .findAny()
                .get();

        //then
        assertThat(addedReservation.getStatus()).isEqualTo(ReservationStatus.WAITING);
    }

    @DisplayName("예약 대기 변경 성공 : 예약 삭제 시 대기 번호가 빠른 예약이 BOOKED 처리된다.")
    @Test
    void waitingReservationConfirm() {
        Reservation bookedReservation = ReservationFixture.getBookedReservation();
        waitingReservationService.deleteReservation(AuthInfo.of(bookedReservation.getMember()), bookedReservation.getId());
        ReservationSlot reservationSlot = bookedReservation.getReservationSlot();

        List<ReservationWithStatus> myReservations = reservationService.findReservations(AuthInfo.of(MemberFixture.getMemberAdmin()));
        ReservationWithStatus nextReservationWithStatus = myReservations.stream()
                .filter(myReservationWithStatus -> myReservationWithStatus.themeName().equals(reservationSlot.getTheme().getName()))
                .filter(myReservationWithStatus -> myReservationWithStatus.time().equals(reservationSlot.getTime().getStartAt()))
                .filter(myReservationWithStatus -> myReservationWithStatus.date().equals(reservationSlot.getDate()))
                .findAny()
                .get();

        assertThat(nextReservationWithStatus.status()).isEqualTo(ReservationStatus.BOOKED);
    }
}
