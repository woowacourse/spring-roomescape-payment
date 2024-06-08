package roomescape.integration.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import roomescape.config.PaymentClient;
import roomescape.domain.LoginMember;
import roomescape.domain.ReservationStatus;
import roomescape.domain.Role;
import roomescape.domain.Waiting;
import roomescape.dto.*;
import roomescape.entity.Member;
import roomescape.entity.Payment;
import roomescape.entity.Reservation;
import roomescape.exception.RoomescapeException;
import roomescape.fixture.MemberFixture;
import roomescape.repository.*;
import roomescape.service.PaymentService;
import roomescape.service.ReservationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static roomescape.exception.ExceptionType.*;
import static roomescape.fixture.ReservationFixture.*;
import static roomescape.fixture.ReservationTimeFixture.DEFAULT_RESERVATION_TIME;
import static roomescape.fixture.ThemeFixture.DEFAULT_THEME;

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
    @MockBean
    private PaymentClient paymentClient;

    @BeforeEach
    void initService() {
        reservationTimeRepository.save(DEFAULT_RESERVATION_TIME);
        themeRepository.save(DEFAULT_THEME);
        memberRepository.save(member);
        PaymentResponse paymentResponse = new PaymentResponse(
                "paymentKey",
                "방탈출 예약",
                LocalDateTime.now().toString(),
                LocalDateTime.now().toString(),
                "KRW",
                1000);
        Mockito.doReturn(paymentResponse).when(paymentClient).approve(any());
    }

    @DisplayName("지나지 않은 시간에 대한 예약을 생성할 수 있다.")
    @Test
    void createFutureReservationTest() {
        //when
        ReservationPaymentResponse response = reservationService.save(
                loginMember,
                new ReservationPaymentRequest(
                        LocalDate.now().plusDays(1),
                        DEFAULT_THEME.getId(),
                        DEFAULT_RESERVATION_TIME.getId(),
                        "paymentKey",
                        "방탈출 예약",
                        1000
                ));

        //then
        assertAll(
                () -> assertThat(reservationRepository.findAll()).hasSize(1),
                () -> assertThat(response.reservationResponse().id()).isEqualTo(1L)
        );
    }

    @DisplayName("지난 시간에 대해 예약을 시도할 경우 예외가 발생한다.")
    @Test
    void createPastReservationFailTest() {
        assertThatThrownBy(() -> reservationService.save(
                loginMember,
                new ReservationPaymentRequest(
                        LocalDate.now().minusDays(1),
                        DEFAULT_THEME.getId(),
                        DEFAULT_RESERVATION_TIME.getId(),
                        "paymentKey",
                        "방탈출 예약",
                        1000
                )))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(PAST_TIME_RESERVATION.getMessage());
    }

    @DisplayName("존재하지 않는 시간에 대해 예약을 생성하면 예외가 발생한다.")
    @Test
    void createReservationWithTimeNotExistsTest() {
        assertThatThrownBy(() -> reservationService.save(
                loginMember,
                new ReservationPaymentRequest(
                        LocalDate.now().plusDays(1),
                        DEFAULT_THEME.getId(),
                        2L,
                        "paymentKey",
                        "방탈출 예약",
                        1000
                )))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(NOT_FOUND_RESERVATION_TIME.getMessage());
    }

    @DisplayName("존재하지 않는 테마에 대해 예약을 생성하면 예외가 발생한다.")
    @Test
    void createReservationWithThemeNotExistsTest() {
        assertThatThrownBy(() -> reservationService.save(
                loginMember,
                new ReservationPaymentRequest(
                        LocalDate.now().plusDays(1),
                        2L,
                        DEFAULT_RESERVATION_TIME.getId(),
                        "paymentKey",
                        "방탈출 예약",
                        1000
                )))
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
        List<AdminReservationDetailResponse> reservationResponses = reservationService.findAllWaitingReservations();

        //then
        assertThat(reservationResponses).hasSize(2);
    }

    @DisplayName("특정 사용자의 예약이 여러 개 존재하는 경우 모든 예약을 조회할 수 있다.")
    @Test
    void findAllReservationsByMemberId() {
        //given
        Reservation reservation1 = ReservationOfDateAndMemberAndStatus(LocalDate.now().plusDays(1),
                member, ReservationStatus.BOOKED);
        Reservation reservation2 = ReservationOfDateAndMemberAndStatus(LocalDate.now().plusDays(1),
                member, ReservationStatus.WAITING);
        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);
        paymentRepository.save(new Payment(reservation1.getId(), "paymentKey", "방탈출 예약", LocalDateTime.now().toString(), LocalDateTime.now().toString(), "KRW", 1000));
        paymentRepository.save(new Payment(reservation2.getId(), "paymentKey", "방탈출 예약", LocalDateTime.now().toString(), LocalDateTime.now().toString(), "KRW", 1000));

        //when
        List<ReservationDetailResponse> reservationResponses = reservationService.findReservationsByMemberId(member.getId());

        //then
        assertThat(reservationResponses).hasSize(2);
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
        assertThat(reservationService.findReservationsByMemberId(1L)).hasSize(0);
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
        assertThat(reservationService.findReservationsByMemberId(1L)).hasSize(0);
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
        List<AdminReservationDetailResponse> waitingResponses = reservationService.findAllWaitingReservations();
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
            assertThatThrownBy(() -> reservationService.save(
                    loginMember,
                    new ReservationPaymentRequest(
                            LocalDate.now().plusDays(1),
                            DEFAULT_THEME.getId(),
                            DEFAULT_RESERVATION_TIME.getId(),
                            "paymentKey",
                            "방탈출 예약",
                            1000
                    )))
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

        @DisplayName("예약을 삭제할 수 있다.")
        @Test
        void deleteReservationTest() {
            //when
            reservationService.deleteById(1L);

            //then
            assertThat(reservationRepository.findAll()).isEmpty();
        }

        @DisplayName("존재하지 않는 예약에 대한 삭제 요청은 정상 요청으로 간주한다.")
        @Test
        void deleteNotExistReservationNotThrowsException() {
            assertThatCode(() -> reservationService.deleteById(2L))
                    .doesNotThrowAnyException();
        }
    }
}
