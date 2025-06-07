package roomescape.approval.application.command;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.approval.domain.AdminApproval;
import roomescape.approval.domain.repository.ApprovalRepository;

@Service
@AllArgsConstructor
public class AdminApprovalCommandService implements ApprovalCommandService<AdminApproval> {
    private final ApprovalRepository approvalRepository;

    @Override
    public boolean supports(Class<?> approvalClass) {
        return AdminApproval.class.equals(approvalClass);
    }

    @Override
    @Transactional
    public void approve(AdminApproval adminApproval) {
        adminApproval.approve();
        adminApproval.approveReservation();
        approvalRepository.save(adminApproval);
    }
}
