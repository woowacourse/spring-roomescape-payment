package roomescape.dto.request;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record ReservationRequest(
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate date,
        long themeId,
        long timeId,
        // TODO : 결제 정보만 따로 담는 DTO가 필요할까?
        String paymentKey,
        String orderId,
        int amount,
        String paymentType
) {
}
