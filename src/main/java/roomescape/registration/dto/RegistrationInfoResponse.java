package roomescape.registration.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.payment.Payment;
import roomescape.registration.domain.reservation.domain.Reservation;
import roomescape.registration.domain.reservation.domain.ReservationStatus;
import roomescape.registration.domain.reservation.dto.ReservationDto;
import roomescape.registration.domain.waiting.domain.Waiting;
import roomescape.registration.domain.waiting.domain.WaitingWithRank;

@Tag(name = "등록 정보 반환", description = "사용자에게 예약과 예약 대기 정보를 응답한다.")
public record RegistrationInfoResponse(
        long id,
        String themeName,
        LocalDate date,
        LocalTime time,
        String status,
        String paymentKey,
        BigDecimal totalAmount) {

    private static final String NULL_INFO = "";

    public static RegistrationInfoResponse of(ReservationDto reservationDto, Payment payment) {
        return new RegistrationInfoResponse(
                reservationDto.id(),
                reservationDto.themeName(),
                reservationDto.date(),
                reservationDto.startAt(),
                ReservationStatus.RESERVED.getStatus(),
                payment.getPaymentKey(),
                payment.getTotalAmount()
        );
    }

    public static RegistrationInfoResponse of(ReservationDto reservationDto) {
        return new RegistrationInfoResponse(
                reservationDto.id(),
                reservationDto.themeName(),
                reservationDto.date(),
                reservationDto.startAt(),
                ReservationStatus.RESERVED.getStatus(),
                NULL_INFO,
                null
        );
    }

    public static RegistrationInfoResponse from(WaitingWithRank waitingWithRank) {
        Waiting waiting = waitingWithRank.waiting();
        Reservation reservation = waiting.getReservation();
        long rank = waitingWithRank.rank();

        return new RegistrationInfoResponse(
                waiting.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(), reservation.getReservationTime().getStartAt(),
                rank + ReservationStatus.WAITING.getStatus(),
                NULL_INFO,
                null
        );
    }
}
