package roomescape.facade;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.facade.dto.ReservationWithPaymentResponseDto;
import roomescape.payment.dto.PaymentRequestDto;
import roomescape.payment.dto.PaymentResponseDto;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.dto.ReservationRequestDto;
import roomescape.reservation.domain.dto.ReservationResponseDto;
import roomescape.reservation.domain.dto.ReservationWithPaymentDto;
import roomescape.reservation.service.ReservationService;
import roomescape.reservationtime.domain.dto.ReservationTimeResponseDto;
import roomescape.theme.domain.dto.ThemeResponseDto;
import roomescape.user.domain.Role;
import roomescape.user.domain.User;
import roomescape.user.domain.dto.UserResponseDto;
import roomescape.user.fixture.UserFixture;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@SpringBootTest
class ReservationPaymentFacadeTest {

    @Autowired
    private ReservationPaymentFacade facade;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private PaymentService tossPaymentService;

    @Test
    @DisplayName("Facade는 예약과 결제를 처리하고 응답을 반환한다 (MockBean 사용)")
    void addWithPayment() {
        //given
        ReservationWithPaymentDto reservationRequestDto = new ReservationWithPaymentDto(
                LocalDate.now().plusDays(5),
                1L,
                1L,
                "paymentKey",
                "orderId",
                1000
        );

        User user = UserFixture.create(Role.ROLE_MEMBER, "유저1", "user@email.com", "1234");

        ReservationResponseDto reservationResponseDto = new ReservationResponseDto(
                1L,
                LocalDate.now().plusDays(5),
                new ReservationTimeResponseDto(1L, LocalTime.of(10, 0)),
                new ThemeResponseDto(1L, "공포의방", "방탈출 테마", "무서운 테마"),
                new UserResponseDto(user.getId(), user.getRole().name(), user.getName(), user.getEmail(), user.getPassword())
        );

        PaymentResponseDto paymentResponseDto = new PaymentResponseDto(
                "결제키",
                "주문ID",
                1000
        );

        when(reservationService.add(any(ReservationRequestDto.class), eq(user)))
                .thenReturn(reservationResponseDto);

        when(tossPaymentService.approve(any(PaymentRequestDto.class), any(Reservation.class)))
                .thenReturn(paymentResponseDto);

        // when
        ReservationWithPaymentResponseDto result = facade.addWithPayment(reservationRequestDto, user);

        // then
        Assertions.assertAll(
                () -> assertThat(result.paymentKey()).isEqualTo("결제키"),
                () -> assertThat(result.amount()).isEqualTo(1000),
                () -> assertThat(result.user().email()).isEqualTo("user@email.com"),
                () -> assertThat(result.theme().name()).isEqualTo("공포의방")
        );
    }
}
