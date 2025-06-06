package roomescape.lock.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import roomescape.lock.LockEntity;
import roomescape.lock.repository.LockRepository;

@Service
@RequiredArgsConstructor
public class LockService {

    private final LockRepository lockRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void acquireLock(final String lockKey) {
        lockRepository.save(new LockEntity(lockKey));
        lockRepository.findByKeyWithPessimisticWrite(lockKey);
    }
}
