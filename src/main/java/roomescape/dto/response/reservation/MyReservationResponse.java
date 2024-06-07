package roomescape.dto.response.reservation;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.reservation.Status;

public record MyReservationResponse(
        Long id,
        String theme,
        LocalDate date,
        @JsonFormat(pattern = "HH:mm")
        LocalTime time,
        Status status,
        String paymentKey,
        BigDecimal totalAmount) {

        public MyReservationResponse(Long id, String theme, LocalDate date, @JsonFormat(pattern = "HH:mm")
        LocalTime time, Status status, String paymentKey, BigDecimal totalAmount) {
                this.id = id;
                this.theme = theme;
                this.date = date;
                this.time = time;
                this.status = status;
                this.paymentKey = paymentKey;
                this.totalAmount = totalAmount;
        }
}
