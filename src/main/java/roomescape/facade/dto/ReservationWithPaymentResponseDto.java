package roomescape.facade.dto;

import roomescape.payment.dto.PaymentResponseDto;
import roomescape.reservation.domain.dto.ReservationResponseDto;
import roomescape.reservationtime.domain.dto.ReservationTimeResponseDto;
import roomescape.theme.domain.dto.ThemeResponseDto;
import roomescape.user.domain.dto.UserResponseDto;

import java.time.LocalDate;

public record ReservationWithPaymentResponseDto(
        Long id,
        LocalDate date,
        ReservationTimeResponseDto time,
        ThemeResponseDto theme,
        UserResponseDto user,
        String paymentKey,
        int amount
) {
    public static ReservationWithPaymentResponseDto of(
            ReservationResponseDto reservationDto,
            PaymentResponseDto paymentDto
    ) {
        return new ReservationWithPaymentResponseDto(
                reservationDto.id(),
                reservationDto.date(),
                reservationDto.time(),
                reservationDto.theme(),
                reservationDto.user(),
                paymentDto.paymentKey(),
                paymentDto.totalAmount()
        );
    }
}
