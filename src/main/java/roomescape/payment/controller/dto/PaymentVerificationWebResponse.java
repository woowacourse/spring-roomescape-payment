package roomescape.payment.controller.dto;

import roomescape.member.auth.vo.MemberInfo;

public record PaymentVerificationWebResponse(
        String orderId,
        int amount,
        MemberInfo memberInfo
) {
}
