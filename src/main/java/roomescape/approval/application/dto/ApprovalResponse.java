package roomescape.approval.application.dto;

import java.math.BigDecimal;
import roomescape.approval.domain.AdminApproval;
import roomescape.approval.domain.Approval;
import roomescape.approval.domain.ApprovalType;
import roomescape.approval.domain.OnSite;
import roomescape.approval.domain.Payment;

public record ApprovalResponse(String type, String paymentKey, BigDecimal amount) {
    public static ApprovalResponse from(Approval approval) {
        if (approval.getType() == ApprovalType.PAYMENT) {
            Payment payment = (Payment) approval;
            return new ApprovalResponse("온라인 결제", payment.getPaymentKey(), payment.getAmount());
        }

        if (approval.getType() == ApprovalType.ONSITE) {
            OnSite onSite = (OnSite) approval;
            return new ApprovalResponse("현장 결제", null, onSite.getAmount());
        }

        if (approval.getType() == ApprovalType.ADMIN_APPROVAL) {
            AdminApproval adminApproval = (AdminApproval) approval;
            return new ApprovalResponse("관리자 승인: " + adminApproval.getMember().getName().getValue(), null, null);
        }

        throw new IllegalArgumentException("지원하지 않는 승인 방식입니다: " + approval.getClass());
    }
}
