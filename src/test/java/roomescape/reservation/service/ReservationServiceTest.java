package roomescape.reservation.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import roomescape.client.payment.TossPaymentClient;
import roomescape.client.payment.dto.PaymentConfirmationFromTossDto;
import roomescape.client.payment.dto.PaymentConfirmationToTossDto;
import roomescape.exception.RoomEscapeException;
import roomescape.exception.model.ReservationExceptionCode;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.member.repository.MemberRepository;
import roomescape.registration.domain.reservation.domain.Reservation;
import roomescape.registration.domain.reservation.dto.ReservationRequest;
import roomescape.registration.domain.reservation.dto.ReservationResponse;
import roomescape.registration.domain.reservation.repository.ReservationRepository;
import roomescape.registration.domain.reservation.service.ReservationService;
import roomescape.registration.domain.waiting.domain.Waiting;
import roomescape.registration.domain.waiting.repository.WaitingRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;
import roomescape.vo.Name;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

@Sql(scripts = "/init.sql")
@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ReservationServiceTest {

    private static final LocalDate BEFORE = LocalDate.now().minusDays(1);
    private static final LocalTime TIME = LocalTime.of(9, 0);

    private final Member reservationOwner = new Member(1L, new Name("polla"), "kyunellroll@gmail.com", "polla99", MemberRole.MEMBER);
    private final Reservation reservation = new Reservation(
            1L,
            LocalDate.now().plusDays(2),
            new ReservationTime(1L, TIME),
            new Theme(1L, new Name("pollaBang"), "폴라 방탈출", "thumbnail",15000L),
            reservationOwner
    );
    private final Waiting waiting = new Waiting(
            1L,
            reservation,
            LocalDateTime.now()
    );

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private WaitingRepository waitingRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private TossPaymentClient tossPaymentClient;

    @Test
    @DisplayName("예약을 추가한다.")
    void addReservation() {
        reservationTimeRepository.save(reservation.getReservationTime());
        themeRepository.save(reservation.getTheme());
        memberRepository.save(reservation.getMember());
        ReservationRequest reservationRequest = new ReservationRequest(reservation.getDate(),
                reservation.getReservationTime().getId(), reservation.getTheme().getId(),
                "paymentType", "paymentKey", "orderId", 1000);
        PaymentConfirmationToTossDto paymentConfirmationToTossDto = PaymentConfirmationToTossDto.from(reservationRequest);
        given(tossPaymentClient.sendPaymentConfirm(paymentConfirmationToTossDto))
                .willReturn(getValidPaymentConfirmationFromTossDto());

        ReservationResponse reservationResponse = reservationService
                .addReservation(reservationRequest, 1L);

        assertThat(reservationResponse.id()).isEqualTo(1);
    }

    @Test
    @DisplayName("예약을 찾는다.")
    void findReservations() {
        reservationTimeRepository.save(reservation.getReservationTime());
        themeRepository.save(reservation.getTheme());
        memberRepository.save(reservation.getMember());
        reservationRepository.save(reservation);

        List<ReservationResponse> reservationResponses = reservationService.findReservations();

        assertThat(reservationResponses).hasSize(1);
    }

    @Test
    @DisplayName("예약 삭제: 예약 대기가 없으면 예약을 지운다")
    void removeReservations() {
        reservationTimeRepository.save(reservation.getReservationTime());
        themeRepository.save(reservation.getTheme());
        memberRepository.save(reservation.getMember());
        reservationRepository.save(reservation);
        waitingRepository.save(waiting);

        assertDoesNotThrow(() -> reservationService.removeReservation(reservation.getId(), reservationOwner.getId()));
    }

    @Test
    @DisplayName("예약 삭제: 예약 대기가 있으면 예약 대기를 예약으로 교체한다")
    void approveWatingToReservation() {
        reservationTimeRepository.save(reservation.getReservationTime());
        themeRepository.save(reservation.getTheme());
        memberRepository.save(reservation.getMember());
        reservationRepository.save(reservation);
        waitingRepository.save(waiting);

        assertAll(
                () -> assertDoesNotThrow(() -> reservationService.removeReservation(reservation.getId(), reservationOwner.getId())),
                () -> assertThat(reservationService.findReservations()).hasSize(1),
                () -> assertThat(reservationService.findReservations().get(0).memberName()).isEqualTo(
                        waiting.getReservation().getMember().getName()),
                () -> assertThat(waitingRepository.findAll()).isEmpty()
        );
    }

    @Test
    @DisplayName("자신의 것이 아닌 것을 삭제하려 하면 예외가 발생한다.")
    void cannotDeleteOtherReservation() {
        reservationTimeRepository.save(reservation.getReservationTime());
        themeRepository.save(reservation.getTheme());
        memberRepository.save(reservation.getMember());
        reservationRepository.save(reservation);
        waitingRepository.save(waiting);
        Member notOwner = memberRepository.save(
                new Member(new Name("주인이 아님"), "notOwner1@email.com", "password1", MemberRole.MEMBER)
        );

        Throwable cannotDeleteOthersException = assertThrows(RoomEscapeException.class,
                () -> reservationService.removeReservation(reservation.getId(), notOwner.getId()));
        assertEquals(ReservationExceptionCode.ONLY_OWNER_CAN_DELETE.getMessage(),
                cannotDeleteOthersException.getMessage());
    }

    @Test
    @DisplayName("과거의 날짜를 예약하려고 시도하는 경우 에러를 발생한다.")
    void validation_ShouldThrowException_WhenReservationDateIsPast() {
        reservationTimeRepository.save(reservation.getReservationTime());
        themeRepository.save(reservation.getTheme());
        memberRepository.save(reservation.getMember());
        reservationRepository.save(reservation);

        ReservationRequest reservationRequest = new ReservationRequest(BEFORE, 1L, 1L,
                "paymentType", "paymentKey", "orderId", 0);
        Throwable pastDateReservation = assertThrows(RoomEscapeException.class,
                () -> reservationService.addReservation(reservationRequest, 1L));

        assertEquals(ReservationExceptionCode.RESERVATION_DATE_IS_PAST_EXCEPTION.getMessage(),
                pastDateReservation.getMessage());
    }

    private PaymentConfirmationFromTossDto getValidPaymentConfirmationFromTossDto() {
        return new PaymentConfirmationFromTossDto(
                "test-payment-key", "test-order-id", 10000L, "DONE"
        );
    }
}
