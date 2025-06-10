package roomescape.reservation.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.auth.login.presentation.dto.LoginMemberInfo;
import roomescape.common.util.time.DateTime;
import roomescape.member.presentation.dto.MyReservationResponse;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.exception.ReservationException;
import roomescape.reservation.presentation.dto.ReservationRequest;
import roomescape.reservation.presentation.dto.ReservationResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ReservationDomainServiceTest {

    private static final String paymentKey = "tgen_20240513184816ZSAZ9";
    private static final String orderId = "MC4wNDYzMzA0OTc2MDgy";
    private static final int amount = 1000;

    @Autowired
    private ReservationDomainService reservationDomainService;

    @Autowired
    private PaymentRepository paymentRepository;

    @DisplayName("멤버별 예약을 조회 할 수 있다.")
    @Test
    void can_find_my_reservation() {
        LoginMemberInfo loginMemberInfo = new LoginMemberInfo(1L);

        List<MyReservationResponse> result = reservationDomainService.getMemberReservations(loginMemberInfo);

        List<MyReservationResponse> expected = List.of(
            new MyReservationResponse(
                1L,
                "테마1",
                LocalDate.of(2025, 4, 28),
                LocalTime.of(10, 0),
                "예약",
                "abcd",
                "주문1",
                1000
            ),
            new MyReservationResponse(
                2L,
                "테마1",
                LocalDate.of(2025, 4, 28),
                LocalTime.of(11, 0),
                "예약",
                "abcd",
                "주문2",
                1000
            )
        );
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("지나간 날짜와 시간에 대한 예약을 생성할 수 없다.")
    @ParameterizedTest
    @MethodSource
    void cant_not_reserve_before_now(final LocalDate date, final Long timeId) {
        Assertions.assertThatThrownBy(
                () -> reservationDomainService.saveReservation(new ReservationRequest(date, timeId, 1L, paymentKey, orderId, amount), 1L))
            .isInstanceOf(ReservationException.class);
    }

    private static Stream<Arguments> cant_not_reserve_before_now() {
        return Stream.of(
            Arguments.of(LocalDate.of(2024, 10, 5), 1L),
            Arguments.of(LocalDate.of(2024, 9, 5), 1L),
            Arguments.of(LocalDate.of(2024, 10, 4), 1L),
            Arguments.of(LocalDate.of(2024, 10, 5), 2L)
        );
    }

    @DisplayName("중복 예약이 불가하다.")
    @Test
    void cant_not_reserve_duplicate() {
        Assertions.assertThatThrownBy(() -> reservationDomainService.saveReservation(
                new ReservationRequest(LocalDate.of(2025, 4, 28), 1L, 1L, paymentKey, orderId, amount), 1L))
            .isInstanceOf(ReservationException.class);
    }

    @DisplayName("대기 목록이 없는 경우 예약을 삭제할 수 있다.")
    @Test
    void can_delete_reservation_without_waiting() {
        Long reservationId = 2L; // 대기 목록이 없는 예약

        reservationDomainService.deleteReservationById(reservationId);

        List<ReservationResponse> reservations = reservationDomainService.getReservations();
        assertThat(reservations).hasSize(3);
        assertThat(reservations).noneMatch(reservation -> reservation.id().equals(reservationId));
    }

    @DisplayName("존재하지 않는 예약은 삭제할 수 없다.")
    @Test
    void cannot_delete_non_existent_reservation() {
        Long nonExistentReservationId = 999L;

        Assertions.assertThatThrownBy(() -> reservationDomainService.deleteReservationById(nonExistentReservationId))
            .isInstanceOf(ReservationException.class)
            .hasMessage("예약을 찾을 수 없습니다.");
    }

    @DisplayName("예약 생성 시 결제 정보도 함께 저장된다")
    @Test
    void saves_payment_with_reservation() {
        ReservationRequest request = new ReservationRequest(
            LocalDate.now().plusDays(1), 1L, 1L, paymentKey, orderId, amount
        );
        Long memberId = 1L;

        Reservation savedReservation = reservationDomainService.saveReservation(request, memberId);

        Payment savedPayment = paymentRepository.findByReservationId(savedReservation.getId())
            .orElseThrow(() -> new AssertionError("결제 정보가 저장되지 않았습니다."));

        assertThat(savedPayment.getPaymentKey()).isEqualTo(paymentKey);
        assertThat(savedPayment.getOrderId()).isEqualTo(orderId);
        assertThat(savedPayment.getAmount()).isEqualTo(amount);
    }

    @TestConfiguration
    static class ReservationDomainConfig {
        @Bean
        public DateTime dateTime() {
            return () -> LocalDateTime.of(2025, 4, 28, 10, 0);
        }
    }
}