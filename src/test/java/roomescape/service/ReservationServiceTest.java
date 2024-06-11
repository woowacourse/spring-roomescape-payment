package roomescape.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import roomescape.config.TestPaymentConfig;
import roomescape.domain.Member;
import roomescape.domain.Payment;
import roomescape.domain.Reservation;
import roomescape.dto.LoginMemberRequest;
import roomescape.dto.ReservationDetailResponse;
import roomescape.dto.ReservationResponse;
import roomescape.dto.ReservationWithPaymentRequest;
import roomescape.exception.ExceptionType;
import roomescape.exception.RoomescapeException;
import roomescape.repository.PaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.exception.ExceptionType.*;

@Import(TestPaymentConfig.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class ReservationServiceTest extends FixtureUsingTest {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private PaymentRepository paymentRepository;

    @DisplayName("지나지 않은 시간에 대한 예약을 생성할 수 있다.")
    @Test
    void createFutureReservationTest() {
        //when
        ReservationResponse saved = reservationService.saveByUser(
                LoginMemberRequest.from(USER1),
                new ReservationWithPaymentRequest(
                        LocalDate.now().plusDays(1),
                        reservationTime_10_0.getId(),
                        theme1.getId(),
                        FakePayment.CORRECT_REQ.getPaymentKey(),
                        FakePayment.CORRECT_REQ.getOrderId(),
                        FakePayment.CORRECT_REQ.getAmount()
                )
        );
        //then
        assertAll(
                () -> assertThat(reservationRepository.findAll())
                        .hasSize(1),
                () -> assertThat(saved.id()).isEqualTo(1L)
        );
    }

    @DisplayName("지난 시간에 대해 예약을 시도할 경우 예외가 발생한다.")
    @Test
    void createPastReservationFailTest() {
        assertThatThrownBy(() -> reservationService.saveByUser(
                LoginMemberRequest.from(USER1),
                new ReservationWithPaymentRequest(
                        LocalDate.now().minusDays(1),
                        reservationTime_10_0.getId(),
                        theme1.getId(),
                        FakePayment.CORRECT_REQ.getPaymentKey(),
                        FakePayment.CORRECT_REQ.getOrderId(),
                        FakePayment.CORRECT_REQ.getAmount()
                )))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(PAST_TIME_RESERVATION.getMessage());
    }

    @DisplayName("존재하지 않는 시간에 대해 예약을 생성하면 예외가 발생한다.")
    @Test
    void createReservationWithTimeNotExistsTest() {
        assertThatThrownBy(() -> reservationService.saveByUser(
                LoginMemberRequest.from(USER1),
                new ReservationWithPaymentRequest(
                        LocalDate.now().minusDays(1),
                        reservationTimeIdNotExists,
                        theme1.getId(),
                        FakePayment.CORRECT_REQ.getPaymentKey(),
                        FakePayment.CORRECT_REQ.getOrderId(),
                        FakePayment.CORRECT_REQ.getAmount()
                )))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(NOT_FOUND_RESERVATION_TIME.getMessage());
    }

    @DisplayName("존재하지 않는 테마에 대해 예약을 생성하면 예외가 발생한다.")
    @Test
    void createReservationWithThemeNotExistsTest() {
        assertThatThrownBy(() -> reservationService.saveByUser(
                LoginMemberRequest.from(USER1),
                new ReservationWithPaymentRequest(
                        LocalDate.now().plusDays(1),
                        reservationTime_10_0.getId(),
                        themeIdNotSaved,
                        FakePayment.CORRECT_REQ.getPaymentKey(),
                        FakePayment.CORRECT_REQ.getOrderId(),
                        FakePayment.CORRECT_REQ.getAmount()
                )))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(NOT_FOUND_THEME.getMessage());
    }

    @DisplayName("예약이 여러 개 존재하는 경우 모든 예약을 조회할 수 있다.")
    @Test
    void findAllTest() {
        //given
        reservationRepository.save(new Reservation(LocalDate.now().plusDays(1), reservationTime_10_0, theme1,
                USER1));
        reservationRepository.save(new Reservation(LocalDate.now().plusDays(2), reservationTime_10_0, theme1,
                USER1));
        reservationRepository.save(new Reservation(LocalDate.now().plusDays(3), reservationTime_10_0, theme1,
                USER1));
        reservationRepository.save(new Reservation(LocalDate.now().plusDays(4), reservationTime_10_0, theme1,
                USER1));

        //when
        List<ReservationResponse> reservationResponses = reservationService.findAll();

        //then
        assertThat(reservationResponses).hasSize(4);
    }

    @DisplayName("예약이 하나 존재하는 경우")
    @Nested
    class OneReservationExistsTest {

        LocalDate defaultDate = LocalDate.now().plusDays(1);
        Reservation defaultReservation;
        Member usedMember = USER1;
        Member notUsedMember = USER2;
        Payment payment;

        @BeforeEach
        void addDefaultReservation() {
            payment = paymentRepository.save(FakePayment.CORRECT_REQ);
            defaultReservation = new Reservation(defaultDate, reservationTime_10_0, theme1, USER1, payment);
            defaultReservation = reservationRepository.save(defaultReservation);
        }

        @DisplayName("본인의 예약을 삭제할 수 있다.")
        @Test
        void deleteReservationTest() {
            //when
            reservationService.delete(LoginMemberRequest.from(usedMember), 1L);

            //then
            assertThat(reservationRepository.findAll()).isEmpty();
        }

        @DisplayName("다른 사람의 예약을 삭제할 수 없다.")
        @Test
        void deleteOthersReservationFailTest() {
            assertThatThrownBy(() ->
                    reservationService.delete(LoginMemberRequest.from(notUsedMember), 1L))
                    .isInstanceOf(RoomescapeException.class)
                    .hasMessage(ExceptionType.FORBIDDEN_DELETE.getMessage());

            assertThat(reservationRepository.findAll()).isNotEmpty();
        }

        @DisplayName("관리자는 다른 사람의 예약을 삭제할 수 있다.")
        @Test
        void deleteOthersReservationByAdminFailTest() {
            reservationService.delete(LoginMemberRequest.from(ADMIN), 1L);
            assertThat(reservationRepository.findAll()).isEmpty();
        }

        @DisplayName("관리자는 다른 사람의 예약 대기를 삭제할 수 있다.")
        @Test
        void deleteWaitingByAdminTest() {
            //given
            ReservationResponse waitingResponse = reservationService.saveByUser(
                    LoginMemberRequest.from(USER2),
                    new ReservationWithPaymentRequest(
                            defaultReservation.getDate(),
                            defaultReservation.getReservationTime().getId(),
                            defaultReservation.getTheme().getId(),
                            FakePayment.CORRECT_REQ.getPaymentKey(),
                            FakePayment.CORRECT_REQ.getOrderId(),
                            FakePayment.CORRECT_REQ.getAmount()
                    ));
            //when
            reservationService.deleteWaitingByAdmin(waitingResponse.id());

            //then
            assertThat(reservationService.findAll().size()).isEqualTo(1);
        }

        @DisplayName("존재하지 않는 예약에 대한 삭제 요청은 정상 요청으로 간주한다.")
        @Test
        void deleteNotExistReservationNotThrowsException() {
            assertThatCode(
                    () -> reservationService.delete(LoginMemberRequest.from(defaultReservation.getMember()), 2L))
                    .doesNotThrowAnyException();
        }
    }

    @DisplayName("본인의 예약 / 예약 대기 목록을 확인할 수 있다.")
    @Test
    void findMembersReservationTest() {
        //given
        reservationService.saveByUser(
                LoginMemberRequest.from(USER2),
                new ReservationWithPaymentRequest(
                        LocalDate.now().plusDays(1),
                        reservationTime_10_0.getId(),
                        theme1.getId(),
                        FakePayment.CORRECT_REQ.getPaymentKey(),
                        FakePayment.CORRECT_REQ.getOrderId(),
                        FakePayment.CORRECT_REQ.getAmount()
                ));

        ReservationResponse waitingReservation = reservationService.saveByUser(
                LoginMemberRequest.from(USER1),
                new ReservationWithPaymentRequest(
                        LocalDate.now().plusDays(1),
                        reservationTime_10_0.getId(),
                        theme1.getId(),
                        FakePayment.CORRECT_REQ.getPaymentKey(),
                        FakePayment.CORRECT_REQ.getOrderId(),
                        FakePayment.CORRECT_REQ.getAmount()
                ));

        ReservationResponse bookedReservation = reservationService.saveByUser(
                LoginMemberRequest.from(USER1),
                new ReservationWithPaymentRequest(
                        LocalDate.now().plusDays(1),
                        reservationTime_11_0.getId(),
                        theme2.getId(),
                        FakePayment.CORRECT_REQ.getPaymentKey(),
                        FakePayment.CORRECT_REQ.getOrderId(),
                        FakePayment.CORRECT_REQ.getAmount()
                ));

        //when
        List<ReservationDetailResponse> usersReservations = reservationService.findAllByMemberId(USER1.getId());

        //then
        assertThat(usersReservations).containsExactlyInAnyOrder(
                new ReservationDetailResponse(
                        waitingReservation.id(),
                        waitingReservation.theme().name(),
                        waitingReservation.date(),
                        waitingReservation.time().startAt(),
                        "1번째 예약대기",
                        "truePaymentKey",
                        BigDecimal.valueOf(1)
                ), new ReservationDetailResponse(
                        bookedReservation.id(),
                        bookedReservation.theme().name(),
                        bookedReservation.date(),
                        bookedReservation.time().startAt(),
                        "예약",
                        "truePaymentKey",
                        BigDecimal.valueOf(1)
                )
        );
    }
}
