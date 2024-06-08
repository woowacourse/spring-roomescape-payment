package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import roomescape.fixture.PaymentConfirmFixtures;
import roomescape.member.model.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.dto.SavePaymentCredentialRequest;
import roomescape.payment.infrastructure.PaymentGateway;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.dto.ReservationDto;
import roomescape.reservation.dto.SaveAdminReservationRequest;
import roomescape.reservation.dto.SaveReservationRequest;
import roomescape.reservation.model.PaymentStatus;
import roomescape.reservation.model.Reservation;
import roomescape.reservation.model.ReservationWaiting;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationWaitingRepository;

@SpringBootTest
@Sql(value = "classpath:test-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationWaitingRepository reservationWaitingRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @MockBean
    private PaymentGateway paymentGateway;

    @Autowired
    private PaymentService paymentService;

    @DisplayName("전체 예약 정보를 조회한다.")
    @Test
    void getReservationsTest() {
        // When
        final List<ReservationDto> reservations = reservationService.getReservations();

        // Then
        assertThat(reservations).hasSize(16);
    }

    @DisplayName("예약 정보를 저장한다.")
    @Test
    @Sql("classpath:test-payment-credential-data.sql")
    void saveReservationTest() {
        // Given
        final LocalDate date = LocalDate.now().plusDays(10);
        final String orderId = "orderId";
        final long amount = 1000L;
        final String paymentKey = "paymentKey";
        final SaveReservationRequest saveReservationRequest = new SaveReservationRequest(
                date,
                1L,
                1L,
                orderId,
                amount,
                paymentKey
        );
        final Member member = memberRepository.findById(3L).orElseThrow();
        paymentService.saveCredential(new SavePaymentCredentialRequest(paymentKey, amount));
        given(paymentGateway.confirm(anyString(), anyLong(), anyString()))
                .willReturn(PaymentConfirmFixtures.getDefaultResponse(paymentKey, amount));

        // When
        final ReservationDto reservation = reservationService.saveReservation(saveReservationRequest, member.getId());

        // Then
        final List<ReservationDto> reservations = reservationService.getReservations();
        assertAll(
                () -> assertThat(reservations).hasSize(17),
                () -> assertThat(reservation.id()).isEqualTo(17L),
                () -> assertThat(reservation.member().id()).isEqualTo(3L),
                () -> assertThat(reservation.date().getValue()).isEqualTo(date),
                () -> assertThat(reservation.time().startAt()).isEqualTo(LocalTime.of(9, 30)),
                () -> assertThat(reservation.paymentStatus()).isEqualTo(PaymentStatus.DONE),
                () -> verify(paymentGateway, times(1)).confirm(anyString(), anyLong(), anyString())
        );
    }

    @DisplayName("저장하려는 예약 시간이 존재하지 않는다면 예외를 발생시킨다.")
    @Test
    void throwExceptionWhenSaveReservationWithNotExistReservationTimeTest() {
        // Given
        final SaveReservationRequest saveReservationRequest = new SaveReservationRequest(
                LocalDate.now(),
                9L,
                1L,
                "orderId",
                1000L,
                "paymentKey"
        );
        final Member member = memberRepository.findById(3L).orElseThrow();
        final Long id = member.getId();

        // When & Then
        assertThatThrownBy(() -> reservationService.saveReservation(saveReservationRequest, id))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 id의 예약 시간이 존재하지 않습니다.");
    }

    @DisplayName("관리자의 요청으로 예약 정보를 저장한다.")
    @Test
    void saveReservationAdmin() {
        // Given
        final LocalDate date = LocalDate.now().plusDays(10);
        final SaveAdminReservationRequest saveReservationRequest = new SaveAdminReservationRequest(
                date,
                3L,
                3L,
                1L
        );
        // When
        final ReservationDto reservation = reservationService.saveReservation(saveReservationRequest);

        // Then
        final List<ReservationDto> reservations = reservationService.getReservations();
        assertAll(
                () -> assertThat(reservations).hasSize(17),
                () -> assertThat(reservation.id()).isEqualTo(17L),
                () -> assertThat(reservation.member().id()).isEqualTo(3L),
                () -> assertThat(reservation.date().getValue()).isEqualTo(date),
                () -> assertThat(reservation.paymentStatus()).isEqualTo(PaymentStatus.WAITING)
        );
    }

    @DisplayName("관리자 저장하려는 예약 시간이 존재하지 않는다면 예외를 발생시킨다.")
    @Test
    void throwExceptionWhenSaveAdminReservationWithNotExistReservationTimeTest() {
        // Given
        final SaveAdminReservationRequest saveReservationRequest = new SaveAdminReservationRequest(
                LocalDate.now(),
                3L,
                9L,
                1L
        );

        // When & Then
        assertThatThrownBy(() -> reservationService.saveReservation(saveReservationRequest))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 id의 예약 시간이 존재하지 않습니다.");
    }

    @DisplayName("예약 정보를 삭제한다.")
    @Test
    void deleteReservationTest() {
        // When
        reservationService.deleteReservation(5L);

        // When
        final List<ReservationDto> reservations = reservationService.getReservations();

        //then
        assertThat(reservations).hasSize(15);
    }

    @DisplayName("예약 정보를 삭제하면 가장 우선 순위가 빠른 예약 대기가 예약으로 등록된다.")
    @Test
    void changeWaitingToReservationWhenDeleteReservationTest() {
        final long targetId = 13L;
        final long newGeneratedReservationId = 17L;
        final long highPriorityWaitingReservationId = 1L;

        // When
        final ReservationWaiting reservationWaiting = reservationWaitingRepository.findById(
                        highPriorityWaitingReservationId)
                .orElseThrow();
        reservationService.deleteReservation(targetId);
        final Reservation newReservation = reservationRepository.findById(newGeneratedReservationId).orElseThrow();

        // Then
        assertAll(
                () -> assertThat(reservationWaiting.getTheme().getId())
                        .isEqualTo(newReservation.getTheme().getId()),
                () -> assertThat(reservationWaiting.getMember().getId())
                        .isEqualTo(newReservation.getMember().getId()),
                () -> assertThat(reservationWaiting.getDate().getValue())
                        .isEqualTo(newReservation.getDate().getValue()),
                () -> assertThat(reservationWaiting.getTime().getStartAt())
                        .isEqualTo(newReservation.getTime().getStartAt()),
                () -> assertThat(newReservation.getPaymentStatus()).isEqualTo(PaymentStatus.WAITING)
        );
    }

    @DisplayName("현재 보다 이전 날짜/시간의 예약 정보를 저장하려고 하면 예외가 발생한다.")
    @Test
    void throwExceptionWhenPastDateOrTime() {
        // Given
        final SaveReservationRequest saveReservationRequest = new SaveReservationRequest(
                LocalDate.now().minusDays(3),
                1L,
                1L,
                "orderId",
                1000L,
                "paymentKey"
        );
        final Member member = memberRepository.findById(3L).orElseThrow();
        final Long id = member.getId();

        // When & Then
        assertThatThrownBy(() -> reservationService.saveReservation(saveReservationRequest, id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("현재 날짜보다 이전 날짜를 예약할 수 없습니다.");
    }

    @DisplayName("이미 존재하는 예약 날짜/시간/테마가 입력되면 예외가 발생한다.")
    @Test
    void throwExceptionWhenInputDuplicateReservationDate() {
        // Given
        final SaveReservationRequest saveReservationRequest = new SaveReservationRequest(
                LocalDate.now().plusDays(2),
                4L,
                9L,
                "orderId",
                1000L,
                "paymentKey"
        );
        final Member member = memberRepository.findById(3L).orElseThrow();
        final Long id = member.getId();

        // When & Then
        assertThatThrownBy(() -> reservationService.saveReservation(saveReservationRequest, id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 해당 날짜/시간의 테마 예약이 있습니다.");
    }

    @DisplayName("회원 아이디로 예약한 예약 정보를 가져온다.")
    @Test
    void getMyReservations() {
        //given
        final Long memberId = 1L;

        //when
        final List<MyReservationResponse> results = reservationService.getMyReservations(memberId);

        //then
        assertThat(results).hasSize(3);
    }
}
