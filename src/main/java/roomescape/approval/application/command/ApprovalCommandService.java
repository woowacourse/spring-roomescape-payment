package roomescape.approval.application.command;

import roomescape.approval.domain.Approval;

public interface ApprovalCommandService<T extends Approval> {
    boolean supports(Class<?> approvalClass);

    void approve(T approvalMethod);
}
