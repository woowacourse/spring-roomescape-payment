package roomescape.infrastructure.persistence.jpa;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import roomescape.domain.admin.Admin;

public interface AdminJpaRepository extends CrudRepository<Admin, Long> {

    boolean existsByEmail(String email);

    Optional<Admin> findByEmail(String email);
}
