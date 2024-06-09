package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static roomescape.fixture.MemberFixture.getMemberChoco;
import static roomescape.fixture.MemberFixture.getMemberClover;
import static roomescape.fixture.MemberFixture.getMemberTacan;
import static roomescape.fixture.ReservationSlotFixture.getNextDayReservationSlot;
import static roomescape.fixture.ReservationTimeFixture.getNoon;
import static roomescape.fixture.ThemeFixture.getTheme1;
import static roomescape.fixture.ThemeFixture.getTheme2;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.jpa.domain.Specification;
import roomescape.auth.domain.AuthInfo;
import roomescape.exception.custom.BadRequestException;
import roomescape.exception.custom.ForbiddenException;
import roomescape.fixture.PaymentFixture;
import roomescape.fixture.ReservationTimeFixture;
import roomescape.global.restclient.PaymentWithRestClient;
import roomescape.member.domain.Member;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.reservation.controller.dto.ReservationPaymentRequest;
import roomescape.reservation.controller.dto.ReservationQueryRequest;
import roomescape.reservation.controller.dto.ReservationRequest;
import roomescape.reservation.controller.dto.ReservationResponse;
import roomescape.reservation.controller.dto.ReservationWithStatus;
import roomescape.reservation.domain.Payment;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationSlot;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.domain.repository.ReservationSlotRepository;
import roomescape.reservation.domain.repository.ReservationTimeRepository;
import roomescape.reservation.domain.repository.ThemeRepository;
import roomescape.reservation.domain.specification.ReservationSpecification;
import roomescape.util.ServiceTest;

@DisplayName("예약 로직 테스트")
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest extends ServiceTest {
    @Autowired
    ReservationSlotRepository reservationSlotRepository;
    @Autowired
    ReservationTimeRepository reservationTimeRepository;
    @Autowired
    ThemeRepository themeRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    ReservationRepository reservationRepository;
    @Autowired
    ReservationService reservationService;
    @SpyBean
    PaymentWithRestClient paymentWithRestClient;

    @DisplayName("예약 생성에 성공한다.")
    @Test
    void create() {
        BDDMockito.doReturn(PaymentFixture.getPayment())
                .when(paymentWithRestClient)
                .confirm(any());

        String date = "2100-04-18";
        ReservationTime time = reservationTimeRepository.save(getNoon());
        Theme theme = themeRepository.save(getTheme1());

        ReservationPaymentRequest reservationPaymentRequest = new ReservationPaymentRequest(
                date,
                time.getId(),
                theme.getId(),
                "testPaymentKey",
                "testOrderId",
                1000L,
                "test payment type");

        reservationSlotRepository.save(new ReservationSlot(LocalDate.parse(date), time, theme));

        ReservationResponse reservationResponse = reservationService.createReservation(reservationPaymentRequest,
                getMemberTacan().getId());
        //then
        assertAll(() -> assertThat(reservationResponse.date()).isEqualTo(date),
                () -> assertThat(reservationResponse.time().id()).isEqualTo(time.getId()),
                () -> assertThat(reservationRepository.findAll().size()).isEqualTo(3));
    }

    @DisplayName("결제가 실패했을 경우 예약이 생성되지 않는다.")
    @Test
    void notCreateReservationWithPaymentFailure() {

        BDDMockito.doReturn(PaymentFixture.getWrongPayment())
                .when(paymentWithRestClient)
                .confirm(any());
        String date = "2100-04-18";
        ReservationTime time = reservationTimeRepository.save(getNoon());
        Theme theme = themeRepository.save(getTheme1());

        ReservationPaymentRequest reservationPaymentRequest = new ReservationPaymentRequest(
                date,
                time.getId(),
                theme.getId(),
                "testPaymentKey",
                "testOrderId",
                0L,
                "test payment type");

        reservationSlotRepository.save(new ReservationSlot(LocalDate.parse(date), time, theme));

        //then
        assertAll(
                () -> assertThatThrownBy(
                        () -> reservationService.createReservation(reservationPaymentRequest, getMemberTacan().getId()))
                        .isInstanceOf(BadRequestException.class),
                () -> assertThat(reservationRepository.findAll().size()).isEqualTo(2)
        );
    }

    @DisplayName("예약 조회에 성공한다.")
    @Test
    void find() {
        //given
        ReservationTime time = reservationTimeRepository.save(getNoon());
        Theme theme1 = themeRepository.save(getTheme1());
        Theme theme2 = themeRepository.save(getTheme2());
        ReservationSlot reservationSlot1 = reservationSlotRepository.save(getNextDayReservationSlot(time, theme1));
        ReservationSlot reservationSlot2 = reservationSlotRepository.save(getNextDayReservationSlot(time, theme2));

        Member memberChoco = memberRepository.save(getMemberChoco());
        reservationRepository.save(new Reservation(memberChoco, reservationSlot1, PaymentFixture.getPayment()));

        Member memberClover = memberRepository.save(getMemberClover());
        reservationRepository.save(new Reservation(memberClover, reservationSlot2, PaymentFixture.getPayment()));

        //when
        List<ReservationResponse> reservations = reservationService.findReservations(
                new ReservationQueryRequest(theme1.getId(), memberChoco.getId(), LocalDate.now(),
                        LocalDate.now().plusDays(1)));

        //then
        assertAll(() -> assertThat(reservations).hasSize(1),
                () -> assertThat(reservations.get(0).date()).isEqualTo(reservationSlot1.getDate()),
                () -> assertThat(reservations.get(0).time().id()).isEqualTo(time.getId()),
                () -> assertThat(reservations.get(0).time().startAt()).isEqualTo(time.getStartAt()));
    }

    @DisplayName("사용자 필터링 예약 조회에 성공한다.")
    @Test
    void findByMemberId() {
        //given
        ReservationTime time = reservationTimeRepository.save(getNoon());
        Theme theme = themeRepository.save(getTheme1());
        ReservationSlot reservationSlot = reservationSlotRepository.save(getNextDayReservationSlot(time, theme));

        Member memberChoco = memberRepository.save(getMemberChoco());
        reservationRepository.save(new Reservation(memberChoco, reservationSlot, PaymentFixture.getPayment()));

        Member memberClover = memberRepository.save(getMemberClover());
        reservationRepository.save(new Reservation(memberClover, reservationSlot, PaymentFixture.getPayment()));

        //when
        List<ReservationResponse> reservations = reservationService.findReservations(
                new ReservationQueryRequest(null, memberChoco.getId(), LocalDate.now(), LocalDate.now().plusDays(1)));

        //then
        assertAll(() -> assertThat(reservations).hasSize(1),
                () -> assertThat(reservations.get(0).date()).isEqualTo(reservationSlot.getDate()),
                () -> assertThat(reservations.get(0).time().id()).isEqualTo(time.getId()),
                () -> assertThat(reservations.get(0).time().startAt()).isEqualTo(time.getStartAt()));
    }

    @DisplayName("테마 필터링 예약 조회에 성공한다.")
    @Test
    void findByThemeId() {
        //given
        ReservationTime time = reservationTimeRepository.save(getNoon());
        Theme theme1 = themeRepository.save(getTheme1());
        Theme theme2 = themeRepository.save(getTheme2());
        ReservationSlot reservationSlot1 = reservationSlotRepository.save(getNextDayReservationSlot(time, theme1));
        ReservationSlot reservationSlot2 = reservationSlotRepository.save(getNextDayReservationSlot(time, theme2));

        Member memberChoco = memberRepository.save(getMemberChoco());
        reservationRepository.save(new Reservation(memberChoco, reservationSlot1, PaymentFixture.getPayment()));
        reservationRepository.save(new Reservation(memberChoco, reservationSlot2, PaymentFixture.getPayment()));

        //when
        List<ReservationResponse> reservations = reservationService.findReservations(
                new ReservationQueryRequest(theme1.getId(), null, LocalDate.now(), LocalDate.now().plusDays(1)));

        //then
        assertAll(() -> assertThat(reservations).hasSize(1),
                () -> assertThat(reservations.get(0).date()).isEqualTo(reservationSlot1.getDate()),
                () -> assertThat(reservations.get(0).time().id()).isEqualTo(time.getId()),
                () -> assertThat(reservations.get(0).time().startAt()).isEqualTo(time.getStartAt()));
    }

    @DisplayName("날짜로만 예약 조회에 성공한다.")
    @Test
    void findByDate() {
        //given
        ReservationTime time = reservationTimeRepository.save(getNoon());
        Theme theme1 = themeRepository.save(getTheme1());
        Theme theme2 = themeRepository.save(getTheme2());
        ReservationSlot reservationSlot1 = reservationSlotRepository.save(getNextDayReservationSlot(time, theme1));
        ReservationSlot reservationSlot2 = reservationSlotRepository.save(getNextDayReservationSlot(time, theme2));

        Member memberChoco = memberRepository.save(getMemberChoco());
        reservationRepository.save(new Reservation(memberChoco, reservationSlot1, PaymentFixture.getPayment()));
        reservationRepository.save(new Reservation(memberChoco, reservationSlot2, PaymentFixture.getPayment()));

        //when
        List<ReservationResponse> reservations = reservationService.findReservations(
                new ReservationQueryRequest(theme1.getId(), null, LocalDate.now(), LocalDate.now().plusDays(2)));

        //then
        assertAll(() -> assertThat(reservations).hasSize(1),
                () -> assertThat(reservations.get(0).date()).isEqualTo(reservationSlot1.getDate()),
                () -> assertThat(reservations.get(0).time().id()).isEqualTo(time.getId()),
                () -> assertThat(reservations.get(0).time().startAt()).isEqualTo(time.getStartAt()));
    }

    @DisplayName("예약 삭제에 성공한다.")
    @Test
    void delete() {
        //given
        ReservationTime time = reservationTimeRepository.save(getNoon());
        Theme theme = themeRepository.save(getTheme1());
        ReservationSlot reservationSlot = getNextDayReservationSlot(time, theme);
        reservationSlotRepository.save(reservationSlot);
        Member member = memberRepository.save(getMemberChoco());
        Reservation reservation = reservationRepository.save(
                new Reservation(member, reservationSlot, PaymentFixture.getPayment()));

        //when
        reservationService.deleteReservation(AuthInfo.of(member), reservation.getId());
        Specification<Reservation> spec = Specification.where(
                        ReservationSpecification.greaterThanOrEqualToStartDate(LocalDate.now()))
                .and(ReservationSpecification.lessThanOrEqualToEndDate(LocalDate.now().plusDays(1)));

        //then
        assertThat(reservationRepository.findAll(spec)).hasSize(0);
    }

    @DisplayName("일자와 시간 중복 시 예외가 발생한다.")
    @Test
    void duplicatedReservation() {
        //given
        Member member = memberRepository.save(getMemberChoco());
        ReservationTime time = reservationTimeRepository.save(getNoon());
        Theme theme = themeRepository.save(getTheme1());
        ReservationSlot reservationSlot = reservationSlotRepository.save(getNextDayReservationSlot(time, theme));
        reservationRepository.save(new Reservation(member, reservationSlot, PaymentFixture.getPayment()));

        ReservationRequest reservationRequest = new ReservationRequest(reservationSlot.getDate().toString(),
                time.getId(),
                theme.getId());

        //when & then
        assertThatThrownBy(() -> reservationService.createAdminReservation(reservationRequest, member.getId()))
                .isInstanceOf(ForbiddenException.class);
    }

    @DisplayName("예약 삭제 시, 사용자 예약도 함께 삭제된다.")
    @Test
    void deleteReservation() {
        //given
        Member member = memberRepository.save(getMemberChoco());
        ReservationTime time = reservationTimeRepository.save(getNoon());
        Theme theme = themeRepository.save(getTheme1());
        ReservationSlot reservationSlot = reservationSlotRepository.save(getNextDayReservationSlot(time, theme));
        reservationRepository.save(new Reservation(member, reservationSlot, PaymentFixture.getPayment()));

        //when
        reservationService.delete(reservationSlot.getId());

        //then
        assertThat(reservationService.findReservations(
                new ReservationQueryRequest(theme.getId(), member.getId(), LocalDate.now(),
                        LocalDate.now().plusDays(1)))).hasSize(0);
    }

    @DisplayName("나의 예약 조회에 성공한다.")
    @Test
    void myReservations() {
        //given
        Member member = memberRepository.save(getMemberClover());
        ReservationTime time = reservationTimeRepository.save(getNoon());
        Theme theme1 = themeRepository.save(getTheme1());
        Theme theme2 = themeRepository.save(getTheme2());
        ReservationSlot reservationSlot1 = reservationSlotRepository.save(getNextDayReservationSlot(time, theme1));
        ReservationSlot reservationSlot2 = reservationSlotRepository.save(getNextDayReservationSlot(time, theme2));
        Payment payment = PaymentFixture.getPayment();

        reservationRepository.save(new Reservation(member, reservationSlot1, payment));
        reservationRepository.save(new Reservation(member, reservationSlot2, payment));

        //when
        List<ReservationWithStatus> myReservations = reservationService.findReservations(AuthInfo.of(member));

        //then
        assertAll(
                () -> assertThat(myReservations).hasSize(2),
                () -> assertThat(myReservations).extracting(ReservationWithStatus::time).containsOnly(time.getStartAt())
        );
    }

    @DisplayName("앞선 예약이 있는 경우 예약을 대기한다")
    @Test
    void existSameReservation() {
        //given
        ReservationSlot reservationSlot = getNextDayReservationSlot(ReservationTimeFixture.get1PM(), getTheme1());
        Member choco = getMemberChoco();
        Member tacan = getMemberTacan();

        reservationSlotRepository.save(reservationSlot);
        reservationRepository.save(new Reservation(choco, reservationSlot, PaymentFixture.getPayment()));

        //when
        reservationService.createAdminReservation(new ReservationRequest(
                        reservationSlot.getDate().format(DateTimeFormatter.ISO_DATE),
                        reservationSlot.getTime().getId(),
                        reservationSlot.getTheme().getId()),
                tacan.getId());
        List<Reservation> allByMember = reservationRepository.findAllByMember(tacan);
        Reservation addedReservation = allByMember
                .stream()
                .filter(reservation -> Objects.equals(reservation.getReservationSlot().getId(),
                        reservationSlot.getId()))
                .findAny()
                .get();

        //then
        assertThat(addedReservation.getStatus()).isEqualTo(ReservationStatus.WAITING);
    }

}
