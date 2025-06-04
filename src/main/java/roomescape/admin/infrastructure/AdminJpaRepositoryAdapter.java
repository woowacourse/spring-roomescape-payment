package roomescape.admin.infrastructure;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import roomescape.admin.domain.Admin;
import roomescape.admin.domain.AdminRepository;

@Repository
public class AdminJpaRepositoryAdapter implements AdminRepository {

    private final AdminJpaRepository adminJpaRepository;

    public AdminJpaRepositoryAdapter(AdminJpaRepository adminJpaRepository) {
        this.adminJpaRepository = adminJpaRepository;
    }

    @Override
    public Optional<Admin> findById(Long id) {
        return adminJpaRepository.findById(id);
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        return adminJpaRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return adminJpaRepository.existsByEmail(email);
    }
}
