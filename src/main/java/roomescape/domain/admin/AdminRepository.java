package roomescape.domain.admin;

import java.util.Optional;

public interface AdminRepository {

    boolean existsByEmail(String email);

    Optional<Admin> findById(Long id);

    Optional<Admin> findByEmail(String email);
}
