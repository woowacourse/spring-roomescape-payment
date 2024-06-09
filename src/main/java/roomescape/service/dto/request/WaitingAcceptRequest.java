package roomescape.service.dto.request;

import java.math.BigDecimal;

public record WaitingAcceptRequest(
        long waitingId,
        long memberId,
        String accountNumber,
        String accountHolder,
        String bankName,
        BigDecimal amount
) {
}
