package roomescape.approval.application.command;

import roomescape.approval.domain.Approval;
import roomescape.approval.domain.ApprovalType;

public interface ApprovalCommandService<T extends Approval> {
    boolean supports(ApprovalType type);

    void approve(T approvalMethod);
}
