package roomescape.approval.application.command;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.approval.domain.OnSite;
import roomescape.approval.domain.repository.ApprovalRepository;

@Service
@AllArgsConstructor
public class OnSiteCommandService implements ApprovalCommandService<OnSite> {
    private final ApprovalRepository approvalRepository;

    @Override
    public boolean supports(Class<?> approvalClass) {
        return OnSite.class.equals(approvalClass);
    }

    @Override
    @Transactional
    public void approve(OnSite onSite) {
        onSite.approve();
        onSite.approveReservation();
        approvalRepository.save(onSite);
    }
}
