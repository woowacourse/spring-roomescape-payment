package roomescape.dto.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import roomescape.dto.member.MemberNameResponseDto;
import roomescape.dto.payment.PaymentResponseDto;
import roomescape.dto.theme.ThemeResponseDto;
import roomescape.dto.time.ReservationTimeResponseDto;

import java.time.LocalDate;

public record CreatedReservationDto(
        Long id,
        MemberNameResponseDto member,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        ThemeResponseDto theme,
        ReservationTimeResponseDto time,
        String status,
        String orderId,
        Long amount
) {

    public static CreatedReservationDto from(ReservationResponseDto reservationDto, PaymentResponseDto paymentDto) {
        return new CreatedReservationDto(
                reservationDto.id(),
                reservationDto.member(),
                reservationDto.date(),
                reservationDto.theme(),
                reservationDto.time(),
                reservationDto.status(),
                paymentDto.orderId(),
                paymentDto.amount());
    }
}
