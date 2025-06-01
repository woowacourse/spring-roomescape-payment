package roomescape.infrastructure;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.business.model.entity.User;
import roomescape.business.model.vo.Id;

public interface UserRepository extends JpaRepository<User, Id> {

    Optional<User> findByEmail_Value(String email);

    boolean existsByEmail_Value(String email);
}
