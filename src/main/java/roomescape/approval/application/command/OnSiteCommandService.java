package roomescape.approval.application.command;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.approval.domain.ApprovalType;
import roomescape.approval.domain.Onsite;
import roomescape.approval.domain.repository.ApprovalRepository;

@Service
@AllArgsConstructor
public class OnSiteCommandService implements ApprovalCommandService<Onsite> {
    private final ApprovalRepository approvalRepository;

    @Override
    public boolean supports(ApprovalType type) {
        return type == ApprovalType.ONSITE;
    }

    @Override
    @Transactional
    public void approve(Onsite onSite) {
        onSite.approve();
        onSite.approveReservation();
        approvalRepository.save(onSite);
    }
}
