package roomescape.infrastructure.persistence.adapter;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.domain.admin.Admin;
import roomescape.domain.admin.AdminRepository;
import roomescape.infrastructure.persistence.jpa.AdminJpaRepository;

@Repository
@RequiredArgsConstructor
public class AdminRepositoryImpl implements AdminRepository {

    private final AdminJpaRepository adminJpaRepository;

    @Override
    public boolean existsByEmail(final String email) {
        return adminJpaRepository.existsByEmail(email);
    }

    @Override
    public Optional<Admin> findById(final Long id) {
        return adminJpaRepository.findById(id);
    }

    @Override
    public Optional<Admin> findByEmail(final String email) {
        return adminJpaRepository.findByEmail(email);
    }
}
