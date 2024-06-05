package roomescape.reservation.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.theme.domain.Theme;

public record ReservationRequest(

        @NotNull(message = "예약 날짜는 null일 수 없습니다.")
        LocalDate date,
        @NotNull(message = "예약 요청의 timeId는 null일 수 없습니다.")
        Long timeId,
        @NotNull(message = "예약 요청의 themeId는 null일 수 없습니다.")
        Long themeId,
        String paymentKey,
        String orderId,
        String amount,
        String paymentType
) {
}
