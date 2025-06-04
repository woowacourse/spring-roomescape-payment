package roomescape.lock.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import roomescape.lock.Lock;

public interface LockRepository extends JpaRepository<Lock, String> {
}
