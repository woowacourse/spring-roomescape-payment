package roomescape.domain.reservation.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.config.TestConfig;
import roomescape.domain.reservation.dto.ReservationDto;
import roomescape.domain.reservation.dto.SaveReservationRequest;
import roomescape.domain.reservation.exception.InvalidReserveInputException;
import roomescape.domain.reservation.model.Reservation;
import roomescape.domain.reservation.model.ReservationWaiting;
import roomescape.domain.reservation.repository.ReservationRepository;
import roomescape.domain.reservation.repository.ReservationWaitingRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(TestConfig.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationServiceTest {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationWaitingRepository reservationWaitingRepository;
    @Autowired
    private ReservationRepository reservationRepository;

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
    void saveReservationWithPaymentConfirmTest() {
        // Given
        final LocalDate date = LocalDate.now().plusDays(10);
        final SaveReservationRequest saveReservationRequest = new SaveReservationRequest(
                date,
                3L,
                1L,
                1L,
                "test-order-id",
                10000L,
                "test_payment_key"
        );

        // When
        final ReservationDto reservation = reservationService.saveReservationWithPaymentConfirm(saveReservationRequest);

        // Then
        final List<ReservationDto> reservations = reservationService.getReservations();
        assertAll(
                () -> assertThat(reservations).hasSize(17),
                () -> assertThat(reservation.id()).isEqualTo(17L),
                () -> assertThat(reservation.member().id()).isEqualTo(3L),
                () -> assertThat(reservation.date().getValue()).isEqualTo(date),
                () -> assertThat(reservation.time().startAt()).isEqualTo(LocalTime.of(9, 30))
        );
    }

    @DisplayName("저장하려는 예약 시간이 존재하지 않는다면 예외를 발생시킨다.")
    @Test
    void throwExceptionWhenSaveReservationWithNotExistReservationWithPaymentTimeTest() {
        // Given
        final SaveReservationRequest saveReservationRequest = new SaveReservationRequest(
                LocalDate.now(),
                3L,
                9L,
                1L,
                "test_order_id",
                10000L,
                "test_payment_key"
        );

        // When & Then
        assertThatThrownBy(() -> reservationService.saveReservationWithPaymentConfirm(saveReservationRequest))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 id의 예약 시간이 존재하지 않습니다.");
    }

    @DisplayName("현재 보다 이전 날짜/시간의 예약 정보를 저장하려고 하면 예외가 발생한다.")
    @Test
    void throwExceptionWhenPastDateOrTime() {
        // Given
        final SaveReservationRequest saveReservationRequest = new SaveReservationRequest(
                LocalDate.now().minusDays(3),
                3L,
                1L,
                1L,
                "test_order_id",
                10000L,
                "test_payment_key"
        );

        // When & Then
        assertThatThrownBy(() -> reservationService.saveReservationWithPaymentConfirm(saveReservationRequest))
                .isInstanceOf(InvalidReserveInputException.class)
                .hasMessage("현재 날짜보다 이전 날짜를 예약할 수 없습니다.");
    }

    @DisplayName("이미 존재하는 예약 날짜/시간/테마가 입력되면 예외가 발생한다.")
    @Test
    void throwExceptionWhenInputDuplicateReservationDate() {
        // Given
        final SaveReservationRequest saveReservationRequest = new SaveReservationRequest(
                LocalDate.now().plusDays(2),
                3L,
                4L,
                9L,
                "test_order_id",
                10000L,
                "test_payment_key"
        );

        // When & Then
        assertThatThrownBy(() -> reservationService.saveReservationWithPaymentConfirm(saveReservationRequest))
                .isInstanceOf(InvalidReserveInputException.class)
                .hasMessage("이미 해당 날짜/시간의 테마 예약이 있습니다.");
    }
}
