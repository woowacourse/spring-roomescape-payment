package roomescape.reservation.service;

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
import roomescape.member.domain.Member;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.payment.infra.PaymentClient;
import roomescape.payment.dto.PaymentResponse;
import roomescape.reservation.controller.dto.*;
import roomescape.reservation.domain.*;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.domain.repository.ReservationSlotRepository;
import roomescape.reservation.domain.repository.ReservationTimeRepository;
import roomescape.reservation.domain.repository.ThemeRepository;
import roomescape.reservation.domain.specification.ReservationSpecification;
import roomescape.util.ServiceTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static roomescape.fixture.MemberFixture.*;
import static roomescape.fixture.ReservationSlotFixture.getNextDayReservationSlot;
import static roomescape.fixture.ReservationTimeFixture.getNoon;
import static roomescape.fixture.ThemeFixture.getTheme1;
import static roomescape.fixture.ThemeFixture.getTheme2;

@DisplayName("예약 로직 테스트")
@ExtendWith(MockitoExtension.class)
class ReservationRegisterTest extends ServiceTest {
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
    ReservationRegister reservationRegister;
    @SpyBean
    PaymentClient paymentClient;

    @DisplayName("예약 생성에 성공한다.")
    @Test
    void create() {
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

        ReservationResponse reservationResponse = reservationRegister.reserve(reservationPaymentRequest, getMemberTacan().getId());
        //then
        assertAll(() -> assertThat(reservationResponse.date()).isEqualTo(date),
                () -> assertThat(reservationResponse.time().id()).isEqualTo(time.getId()));
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
        reservationRepository.save(new Reservation(memberChoco, reservationSlot1));

        Member memberClover = memberRepository.save(getMemberClover());
        reservationRepository.save(new Reservation(memberClover, reservationSlot2));

        //when
        List<ReservationResponse> reservations = reservationRegister.findReservations(
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
        reservationRepository.save(new Reservation(memberChoco, reservationSlot));

        Member memberClover = memberRepository.save(getMemberClover());
        reservationRepository.save(new Reservation(memberClover, reservationSlot));

        //when
        List<ReservationResponse> reservations = reservationRegister.findReservations(
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
        reservationRepository.save(new Reservation(memberChoco, reservationSlot1));
        reservationRepository.save(new Reservation(memberChoco, reservationSlot2));

        //when
        List<ReservationResponse> reservations = reservationRegister.findReservations(
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
        reservationRepository.save(new Reservation(memberChoco, reservationSlot1));
        reservationRepository.save(new Reservation(memberChoco, reservationSlot2));

        //when
        List<ReservationResponse> reservations = reservationRegister.findReservations(
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
                new Reservation(member, reservationSlot));

        //when
        reservationRegister.deleteReservation(AuthInfo.of(member), reservation.getId());
        Specification<Reservation> spec = Specification.where(ReservationSpecification.greaterThanOrEqualToStartDate(LocalDate.now()))
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
        reservationRepository.save(new Reservation(member, reservationSlot));

        ReservationRequest reservationRequest = new ReservationRequest(reservationSlot.getDate().toString(), time.getId(),
                theme.getId());

        //when & then
        assertThatThrownBy(() -> reservationRegister.createReservation(reservationRequest, member.getId(), ReservationStatus.BOOKED))
                .isInstanceOf(BadRequestException.class);
    }

    @DisplayName("예약 삭제 시, 사용자 예약도 함께 삭제된다.")
    @Test
    void deleteReservationSlodReservation() {
        //given
        Member member = memberRepository.save(getMemberChoco());
        ReservationTime time = reservationTimeRepository.save(getNoon());
        Theme theme = themeRepository.save(getTheme1());
        ReservationSlot reservationSlot = reservationSlotRepository.save(getNextDayReservationSlot(time, theme));
        reservationRepository.save(new Reservation(member, reservationSlot));

        //when
        reservationRegister.deleteReservationSlot(reservationSlot.getId());

        //then
        assertThat(reservationRegister.findReservations(
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

        reservationRepository.save(new Reservation(member, reservationSlot1));
        reservationRepository.save(new Reservation(member, reservationSlot2));

        //when
        List<ReservationWithStatus> myReservations = reservationRegister.findReservations(AuthInfo.of(member));

        //then
        assertAll(
                () -> assertThat(myReservations).hasSize(2),
                () -> assertThat(myReservations).extracting(ReservationWithStatus::time).containsOnly(time.getStartAt())
        );
    }



}
