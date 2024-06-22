package roomescape.reservation.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import roomescape.auth.domain.AuthInfo;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.ReservationFixture;
import roomescape.fixture.ReservationTimeFixture;
import roomescape.member.domain.Member;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.infra.PaymentClient;
import roomescape.reservation.controller.dto.*;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationSlot;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.domain.repository.ReservationSlotRepository;
import roomescape.util.ServiceTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static roomescape.fixture.MemberFixture.*;
import static roomescape.fixture.ReservationSlotFixture.getNextDayReservationSlot;
import static roomescape.fixture.ThemeFixture.getTheme1;

@DisplayName("예약 대기 로직 테스트")
class WaitingReservationRegisterTest extends ServiceTest {

    @Autowired
    ReservationRegister reservationRegister;
    @Autowired
    WaitingReservationService waitingReservationService;
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    ReservationSlotRepository reservationSlotRepository;
    @SpyBean
    PaymentClient paymentClient;

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

    @DisplayName("예약 대기 변경 성공 : 예약 삭제 시 대기 번호가 빠른 예약이 PENDING 처리된다.")
    @Test
    void waitingReservationConfirm() {
        Reservation bookedReservation = ReservationFixture.getBookedReservation();
        waitingReservationService.deleteReservation(AuthInfo.of(bookedReservation.getMember()), bookedReservation.getId());
        ReservationSlot reservationSlot = bookedReservation.getReservationSlot();

        List<ReservationWithStatus> myReservations = reservationRegister.findReservations(AuthInfo.of(MemberFixture.getMemberAdmin()));
        ReservationWithStatus nextReservationWithStatus = myReservations.stream()
                .filter(myReservationWithStatus -> myReservationWithStatus.themeName().equals(reservationSlot.getTheme().getName()))
                .filter(myReservationWithStatus -> myReservationWithStatus.time().equals(reservationSlot.getTime().getStartAt()))
                .filter(myReservationWithStatus -> myReservationWithStatus.date().equals(reservationSlot.getDate()))
                .findAny()
                .get();

        assertThat(nextReservationWithStatus.status()).isEqualTo(ReservationStatus.PENDING);
    }

    @DisplayName("예약 대기 확정 성공 : 대기 예약을 결제한다.")
    @Test
    void payReservation() {
        //given
        PaymentResponse paymentResponse = new PaymentResponse(
                "test",
                "test",
                1000L,
                "test",
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "DONE"
        );

        BDDMockito.doReturn(paymentResponse)
                .when(paymentClient)
                .confirm(any());
        Reservation reservation = ReservationFixture.getBookedReservation();
        ReservationSlot reservationSlot = reservation.getReservationSlot();

        //when
        waitingReservationService.deleteReservation(AuthInfo.of(MemberFixture.getMemberChoco()), reservation.getId());
        WaitingReservationPaymentRequest reservationPaymentRequest = new WaitingReservationPaymentRequest(
                reservationSlot.getDate().format(DateTimeFormatter.ISO_DATE),
                reservationSlot.getTime().getStartAt().format(DateTimeFormatter.ISO_TIME),
                reservationSlot.getTheme().getName(),
                paymentResponse.paymentKey(),
                paymentResponse.orderId(),
                paymentResponse.totalAmount(),
                paymentResponse.method(
                ));

        //then
        assertThatNoException().isThrownBy(() -> {
            waitingReservationService.confirmReservation(reservationPaymentRequest, getMemberAdmin().getId());
        });
    }
}
