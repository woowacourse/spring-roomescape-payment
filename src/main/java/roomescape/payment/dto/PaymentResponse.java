package roomescape.payment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import roomescape.member.dto.MemberResponse;
import roomescape.payment.domain.Payment;
import roomescape.theme.dto.ThemeResponse;
import roomescape.time.dto.TimeResponse;

public record PaymentResponse(
        Long id,
        MemberResponse member,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        TimeResponse time,
        ThemeResponse theme,
        String paymentKey,
        BigDecimal amount
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                MemberResponse.from(payment.getMember()),
                payment.getSchedule().getDate(),
                TimeResponse.from(payment.getSchedule().getTime()),
                ThemeResponse.from(payment.getSchedule().getTheme()),
                payment.getPaymentKey(),
                payment.getAmount());
    }
}
