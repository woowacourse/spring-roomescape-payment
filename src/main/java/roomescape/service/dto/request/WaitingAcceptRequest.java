package roomescape.service.dto.request;

import roomescape.domain.member.Member;

import java.math.BigDecimal;

public record WaitingAcceptRequest(
        long waitingId,
        Member member,
        String accountNumber,
        String accountHolder,
        String bankName,
        BigDecimal amount
) {
}
