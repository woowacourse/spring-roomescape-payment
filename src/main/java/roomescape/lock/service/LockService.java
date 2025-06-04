package roomescape.lock.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import roomescape.lock.Lock;
import roomescape.lock.repository.LockRepository;

@Service
@RequiredArgsConstructor
public class LockService {

    private final LockRepository lockRepository;
    private final EntityManager em;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void acquireLock(final String lockKey) {
        if (!lockRepository.existsById(lockKey)) {
            Lock newLock = new Lock(lockKey);
            lockRepository.save(newLock);
        }
        em.find(Lock.class, lockKey, LockModeType.PESSIMISTIC_WRITE);
    }
}
