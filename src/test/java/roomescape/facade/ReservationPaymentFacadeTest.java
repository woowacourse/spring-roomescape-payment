package roomescape.facade;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.facade.dto.ReservationWithPaymentResponseDto;
import roomescape.payment.dto.PaymentResponseDto;
import roomescape.payment.service.PaymentService;
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
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class ReservationPaymentFacadeTest {

    @Mock
    private ReservationService mockReservationService;

    @Mock
    private PaymentService mockPaymentService;

    @InjectMocks
    ReservationPaymentFacade facade;


    @DisplayName("결제와 예약이 성공적으로 진행되면 결과 응답이 정상적으로 반환된다.")
    @Test
    void addWithPayment() {
        //given
        ReservationWithPaymentDto requestDto = new ReservationWithPaymentDto(
                LocalDate.now().plusDays(5),
                1L,
                1L,
                "paymentKey",
                "orderId",
                1000
        );

        User user = UserFixture.create(Role.ROLE_MEMBER, "유저1", "user@email.com", "1234");

        ReservationResponseDto reservationResponse = new ReservationResponseDto(
                1L,
                LocalDate.now().plusDays(5),
                new ReservationTimeResponseDto(1L, LocalTime.of(10, 0)),
                new ThemeResponseDto(1L, "테마명","방탈출 테마", "무서운 테마"),
                new UserResponseDto(user.getId(), user.getRole().name() ,user.getName(), user.getEmail(), user.getPassword())
        );

        PaymentResponseDto paymentResponse = new PaymentResponseDto(
                "paymentKey",
                "orderId",
                1000
        );

        Mockito.when(mockReservationService.add(any(), eq(user))).thenReturn(reservationResponse);
        Mockito.when(mockPaymentService.approve(any(), any())).thenReturn(paymentResponse);

        // when
        ReservationWithPaymentResponseDto response = facade.addWithPayment(requestDto, user);

        // then
        assertAll(
                () -> assertThat(response.id()).isEqualTo(reservationResponse.id()),
                () -> assertThat(response.date()).isEqualTo(reservationResponse.date()),
                () -> assertThat(response.time()).isEqualTo(reservationResponse.time()),
                () -> assertThat(response.theme()).isEqualTo(reservationResponse.theme()),
                () -> assertThat(response.user()).isEqualTo(reservationResponse.user()),
                () -> assertThat(response.paymentKey()).isEqualTo(paymentResponse.paymentKey()),
                () -> assertThat(response.amount()).isEqualTo(paymentResponse.totalAmount())
        );

        verify(mockReservationService).add(any(), eq(user));
        verify(mockPaymentService).approve(any(), any());
    }

}
