package roomescape.lock.repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import roomescape.lock.LockEntity;

public interface LockRepository extends JpaRepository<LockEntity, String> {

    @org.springframework.data.jpa.repository.Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select l from LockEntity l where l.lockKey = :key")
    Optional<LockEntity> findByKeyWithPessimisticWrite(final String key);
}
