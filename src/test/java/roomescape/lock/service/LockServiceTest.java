package roomescape.lock.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
@Import(LockService.class)
class  LockServiceTest {

    @Autowired
    private LockService lockService;

    @Test
    void acquireLock_정상동작_통합테스트() {
        String lockKey = "normal-key";
        assertDoesNotThrow(() -> lockService.acquireLock(lockKey));
    }

    @Test
    void 같은_key로_두번_acquireLock_하면_예외발생() {
        String lockKey = "duplicate-key";
        lockService.acquireLock(lockKey);

        assertThrows(DataIntegrityViolationException.class, () -> {
            lockService.acquireLock(lockKey);
        });
    }
}
