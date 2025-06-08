package roomescape.external.lock;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomescapeLockRepository extends JpaRepository<RoomescapeLock, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RoomescapeLock> findById(String lockId);
}
