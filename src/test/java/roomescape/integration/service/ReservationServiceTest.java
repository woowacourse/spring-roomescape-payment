package roomescape.integration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import static roomescape.exception.type.RoomescapeExceptionType.DUPLICATE_RESERVATION;
import static roomescape.exception.type.RoomescapeExceptionType.DUPLICATE_WAITING_RESERVATION;
import static roomescape.exception.type.RoomescapeExceptionType.NOT_FOUND_RESERVATION_TIME;
import static roomescape.exception.type.RoomescapeExceptionType.NOT_FOUND_THEME;
import static roomescape.exception.type.RoomescapeExceptionType.PAST_TIME_RESERVATION;
import static roomescape.fixture.PaymentFixture.PAYMENT_INFO;
import static roomescape.fixture.PaymentFixture.PAYMENT_RESPONSE;
import static roomescape.fixture.ReservationFixture.ReservationOfDate;
import static roomescape.fixture.ReservationFixture.ReservationOfDateAndMemberAndStatus;
import static roomescape.fixture.ReservationFixture.ReservationOfDateAndStatus;
import static roomescape.fixture.ReservationTimeFixture.DEFAULT_RESERVATION_TIME;
import static roomescape.fixture.ThemeFixture.DEFAULT_THEME;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

import roomescape.reservation.dto.ReservationWaitingDetailResponse;
import roomescape.auth.domain.Role;
import roomescape.exception.RoomescapeException;
import roomescape.fixture.MemberFixture;
import roomescape.member.domain.LoginMember;
import roomescape.member.entity.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.entity.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.Reservations;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.dto.ReservationDetailResponse;
import roomescape.reservation.dto.ReservationPaymentDetail;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.entity.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.service.ReservationService;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.repository.ReservationTimeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Sql(value = "/clear.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class ReservationServiceTest {

    private final LoginMember loginMember = MemberFixture.DEFAULT_LOGIN_MEMBER;
    private final Member member = MemberFixture.DEFAULT_MEMBER;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void initService() {
        reservationTimeRepository.save(DEFAULT_RESERVATION_TIME);
        themeRepository.save(DEFAULT_THEME);
        memberRepository.save(member);
    }

    @DisplayName("지나지 않은 시간에 대한 예약을 생성할 수 있다.")
    @Test
    void createFutureReservationTest() {
        //when
        ReservationResponse saved = reservationService.saveReservationPayment(
                        loginMember,
                        new ReservationRequest(
                                LocalDate.now().plusDays(1),
                                DEFAULT_THEME.getId(),
                                DEFAULT_RESERVATION_TIME.getId()
                        ), PAYMENT_INFO)
                .reservationResponse();

        //then
        assertAll(
                () -> assertThat(new Reservations(reservationRepository.findAll()).getReservations())
                        .hasSize(1),
                () -> assertThat(paymentRepository.findByReservationId(saved.id()).get()).isNotNull(),
                () -> assertThat(saved.id()).isEqualTo(1L)
        );
    }

    @DisplayName("지난 시간에 대해 예약을 시도할 경우 예외가 발생한다.")
    @Test
    void createPastReservationFailTest() {
        assertThatThrownBy(() -> reservationService.saveReservationPayment(
                loginMember,
                new ReservationRequest(
                        LocalDate.now().minusDays(1),
                        DEFAULT_THEME.getId(),
                        DEFAULT_RESERVATION_TIME.getId()),
                PAYMENT_INFO))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(PAST_TIME_RESERVATION.getMessage());
    }

    @DisplayName("존재하지 않는 시간에 대해 예약을 생성하면 예외가 발생한다.")
    @Test
    void createReservationWithTimeNotExistsTest() {
        assertThatThrownBy(() -> reservationService.saveReservationPayment(
                loginMember,
                new ReservationRequest(
                        LocalDate.now().minusDays(1),
                        2L,
                        DEFAULT_THEME.getId()),
                PAYMENT_INFO))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(NOT_FOUND_RESERVATION_TIME.getMessage());
    }

    @DisplayName("존재하지 않는 테마에 대해 예약을 생성하면 예외가 발생한다.")
    @Test
    void createReservationWithThemeNotExistsTest() {
        assertThatThrownBy(() -> reservationService.saveReservationPayment(
                loginMember,
                new ReservationRequest(
                        LocalDate.now().plusDays(1),
                        DEFAULT_RESERVATION_TIME.getId(),
                        2L),
                PAYMENT_INFO))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(NOT_FOUND_THEME.getMessage());
    }

    @DisplayName("관리자는 예약이 여러 개 존재하는 경우 모든 예약을 조회할 수 있다.")
    @Test
    void findAllReservationsTest() {
        //given
        reservationRepository.save(ReservationOfDate(LocalDate.now().plusDays(1)));
        reservationRepository.save(ReservationOfDate(LocalDate.now().plusDays(2)));
        reservationRepository.save(
                ReservationOfDateAndStatus(LocalDate.now().plusDays(3), ReservationStatus.WAITING)
        );
        reservationRepository.save(ReservationOfDate(LocalDate.now().plusDays(4)));

        //when
        List<ReservationResponse> reservationResponses = reservationService.findAllReservations();

        //then
        assertThat(reservationResponses).hasSize(3);
    }

    @DisplayName("관리자는 예약 대기가 여러 개 존재하는 경우 모든 예약 대기를 조회할 수 있다.")
    @Test
    void findAllWaitingReservationsTest() {
        //given
        Member testMember = new Member(2L, "test", Role.USER, "test@test.com", "1234");
        memberRepository.save(testMember);
        reservationRepository.save(
                ReservationOfDateAndMemberAndStatus(LocalDate.now().plusDays(1), testMember, ReservationStatus.BOOKED)
        );
        reservationRepository.save(
                ReservationOfDateAndMemberAndStatus(LocalDate.now().plusDays(1), member, ReservationStatus.WAITING)
        );
        reservationRepository.save(
                ReservationOfDateAndMemberAndStatus(LocalDate.now().plusDays(2), testMember, ReservationStatus.BOOKED)
        );
        reservationRepository.save(
                ReservationOfDateAndMemberAndStatus(LocalDate.now().plusDays(2), member, ReservationStatus.WAITING)
        );

        //when
        List<ReservationWaitingDetailResponse> reservationResponses = reservationService.findAllWaitingReservations();

        //then
        assertThat(reservationResponses).hasSize(2);
    }

    @DisplayName("특정 사용자의 예약이 여러 개 존재하는 경우 모든 예약을 조회할 수 있다.")
    @Test
    void findAllReservationsByMemberId() {
        //given
        Member testMember = new Member(2L, "test", Role.USER, "test@test.com", "1234");
        memberRepository.save(testMember);
        Reservation reservation1 = ReservationOfDateAndMemberAndStatus(LocalDate.now().plusDays(1),
                member, ReservationStatus.BOOKED);
        Reservation reservation2 = ReservationOfDateAndMemberAndStatus(LocalDate.now().plusDays(2),
                testMember, ReservationStatus.BOOKED);
        Reservation reservation3 = ReservationOfDateAndMemberAndStatus(LocalDate.now().plusDays(2),
                member, ReservationStatus.WAITING);
        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);
        reservationRepository.save(reservation3);

        paymentRepository.save(new Payment(reservation1, PAYMENT_INFO));
        //when
        List<ReservationPaymentDetail> reservationResponses = reservationService.findAllByMemberId(member.getId());

        //then
        List<ReservationPaymentDetail> expected = List.of(
                new ReservationPaymentDetail(ReservationDetailResponse.from(reservation1), PAYMENT_RESPONSE),
                new ReservationPaymentDetail(ReservationDetailResponse.from(new Waiting(reservation3, 1)), PaymentResponse.nothing()));

        assertThat(reservationResponses).isEqualTo(expected);
    }

    @DisplayName("특정 사용자가 자신의 예약 대기를 취소할 수 있다.")
    @Test
    void deleteReservationWaiting() {
        //given
        Member testMember = new Member(2L, "test", Role.USER, "test@test.com", "1234");
        memberRepository.save(testMember);
        Reservation reservation1 = ReservationOfDateAndMemberAndStatus(LocalDate.now().plusDays(1),
                testMember, ReservationStatus.BOOKED);
        Reservation reservation2 = ReservationOfDateAndMemberAndStatus(LocalDate.now().plusDays(1),
                member, ReservationStatus.WAITING);
        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);

        //when
        reservationService.deleteByMemberIdAndId(loginMember, 2);
        //then
        assertThat(reservationService.findAllByMemberId(1L)).isEqualTo(List.of());
    }

    @DisplayName("관리자는 예약 대기를 취소할 수 있다.")
    @Test
    void deleteReservationWaitingByAdmin() {
        //given
        Member testMember = new Member(2L, "test", Role.USER, "test@test.com", "1234");
        memberRepository.save(testMember);
        Reservation reservation1 = ReservationOfDateAndMemberAndStatus(LocalDate.now().plusDays(1),
                testMember, ReservationStatus.BOOKED);
        Reservation reservation2 = ReservationOfDateAndMemberAndStatus(LocalDate.now().plusDays(1),
                member, ReservationStatus.WAITING);
        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);

        //when
        reservationService.deleteById(2);
        //then
        assertThat(reservationService.findAllByMemberId(1L)).isEqualTo(List.of());
    }


    @DisplayName("관리자가 예약을 삭제 시 다음 순번의 예약 대기가 예약된다.")
    @Test
    void reservationWaitingByAdmin() {
        //given
        Member testMember = new Member(2L, "test", Role.USER, "test@test.com", "1234");
        memberRepository.save(testMember);
        Reservation reservation1 = ReservationOfDateAndMemberAndStatus(LocalDate.now().plusDays(1),
                testMember, ReservationStatus.BOOKED);
        Reservation reservation2 = ReservationOfDateAndMemberAndStatus(LocalDate.now().plusDays(1),
                member, ReservationStatus.WAITING);
        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);

        //when
        reservationRepository.deleteById(1L);
        List<ReservationWaitingDetailResponse> waitingResponses = reservationService.findAllWaitingReservations();
        List<ReservationResponse> bookedResponses = reservationService.findAllReservations();
        //then
        assertAll(
                () -> assertThat(waitingResponses).isEqualTo(List.of()),
                () -> assertThat(bookedResponses.size()).isEqualTo(1)
        );
    }

    @DisplayName("예약이 하나 존재하는 경우")
    @Nested
    class OneReservationExistsTest {

        LocalDate defaultDate = LocalDate.now().plusDays(1);
        Reservation defaultReservation;

        @BeforeEach
        void addDefaultReservation() {
            defaultReservation = new Reservation(defaultDate, DEFAULT_RESERVATION_TIME, DEFAULT_THEME, member, ReservationStatus.BOOKED);
            defaultReservation = reservationRepository.save(defaultReservation);
        }

        @DisplayName("이미 예약된 시간, 테마의 예약을 또 생성할 수 없다.")
        @Test
        void duplicatedReservationFailTest() {
            assertThatThrownBy(() -> reservationService.saveReservationPayment(
                    loginMember,
                    new ReservationRequest(
                            defaultDate,
                            DEFAULT_RESERVATION_TIME.getId(),
                            DEFAULT_THEME.getId()),
                    PAYMENT_INFO))
                    .isInstanceOf(RoomescapeException.class)
                    .hasMessage(DUPLICATE_RESERVATION.getMessage());
        }

        @DisplayName("자신이 예약하거나 예약 대기한 시간, 테마의 예약 대기를 생성할 수 없다. ")
        @Test
        void duplicatedWaitingReservationFailTest() {
            assertThatThrownBy(() -> reservationService.saveWaiting(
                    loginMember,
                    new ReservationRequest(defaultDate, DEFAULT_RESERVATION_TIME.getId(), DEFAULT_THEME.getId())))
                    .isInstanceOf(RoomescapeException.class)
                    .hasMessage(DUPLICATE_WAITING_RESERVATION.getMessage());
        }

        @DisplayName("결제된 예약을 삭제할 수 있다.")
        @Test
        void deleteReservationPaymentTest() {
            paymentRepository.save(new Payment(defaultReservation, PAYMENT_INFO));
            //when
            reservationService.cancelReservationPayment(1L, 1L);

            //then
            assertThat(new Reservations(reservationRepository.findAll()).getReservations()).isEmpty();
        }

        @DisplayName("결제되지 않은 예약을 삭제할 수 있다.")
        @Test
        void deleteReservationTest() {
            //when
            reservationService.deleteById(1L);

            //then
            assertThat(new Reservations(reservationRepository.findAll()).getReservations()).isEmpty();
        }

        @DisplayName("존재하지 않는 예약에 대한 삭제 요청은 정상 요청으로 간주한다.")
        @Test
        void deleteNotExistReservationNotThrowsException() {
            assertThatCode(() -> reservationService.deleteById(2L))
                    .doesNotThrowAnyException();
        }
    }
}
