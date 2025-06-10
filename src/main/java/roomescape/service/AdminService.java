package roomescape.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.BusinessException;
import roomescape.domain.admin.Admin;
import roomescape.domain.admin.AdminRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final AdminRepository adminRepository;

    public boolean isExistsByEmail(final String email) {
        return adminRepository.existsByEmail(email);
    }

    public Admin findByEmail(final String email) {
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("관리자를 찾을 수 없습니다."));
    }

    public Admin findById(final Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new BusinessException("관리자를 찾을 수 없습니다."));
    }
}
