package roomescape.approval.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import roomescape.approval.domain.AdminApproval;
import roomescape.approval.domain.Approval;
import roomescape.approval.domain.ApprovalType;
import roomescape.approval.domain.Onsite;
import roomescape.approval.domain.Payment;

public record ApprovalResponse(
        @Schema(description = "승인 방식 (예: 온라인 결제, 현장 결제, 관리자 승인)")
        String type,
        @Schema(description = "결제 키 (nullable) (온라인 결제인 경우에만 존재)", nullable = true)
        String paymentKey,
        @Schema(description = "결제 금액 (nullable) (온라인 결제, 현장 결제일 경우 존재)", nullable = true)
        BigDecimal amount) {
    public static ApprovalResponse from(Approval approval) {
        if (approval.getType() == ApprovalType.PAYMENT) {
            Payment payment = (Payment) approval;
            return new ApprovalResponse("온라인 결제", payment.getPaymentKey(), payment.getAmount());
        }

        if (approval.getType() == ApprovalType.ONSITE) {
            Onsite onSite = (Onsite) approval;
            return new ApprovalResponse("현장 결제", null, onSite.getAmount());
        }

        if (approval.getType() == ApprovalType.ADMIN_APPROVAL) {
            AdminApproval adminApproval = (AdminApproval) approval;
            return new ApprovalResponse("관리자 승인: " + adminApproval.getMember().getName().getValue(), null, null);
        }

        throw new IllegalArgumentException("지원하지 않는 승인 방식입니다: " + approval.getType());
    }
}
