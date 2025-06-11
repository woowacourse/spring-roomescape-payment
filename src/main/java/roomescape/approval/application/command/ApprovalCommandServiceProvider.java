package roomescape.approval.application.command;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import roomescape.approval.domain.Approval;

@Component
@AllArgsConstructor
public class ApprovalCommandServiceProvider {
    private final List<ApprovalCommandService<? extends Approval>> approvalCommandServices;

    public <T extends Approval> ApprovalCommandService<T> findService(T approval) {
        return (ApprovalCommandService<T>) approvalCommandServices.stream()
                .filter(service -> service.supports(approval.getType()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 승인 방식입니다: " + approval.getType()));
    }

}
