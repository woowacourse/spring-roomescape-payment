package roomescape.payment.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.OffsetDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.system.exception.RoomEscapeException;

class CanceledPaymentTest {

    @Test
    @DisplayName("취소 날짜가 승인 날짜 이전이면 예외가 발생한다")
    void invalidDate() {
        OffsetDateTime approvedAt = OffsetDateTime.now();
        OffsetDateTime canceledAt = approvedAt.minusMinutes(1L);
        assertThatThrownBy(() -> new CanceledPayment("payment-key", "reason", 10000L, approvedAt, canceledAt))
                .isInstanceOf(RoomEscapeException.class);
    }
}