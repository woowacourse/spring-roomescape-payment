package roomescape.external.lock;

import org.springframework.stereotype.Component;
import roomescape.repository.RoomescapeLockRepository;

@Component
public class RoomescapeLockInitializer {

    private static final String lockId = "roomescape_lock";

    private final RoomescapeLockRepository roomescapeLockRepository;

    public RoomescapeLockInitializer(RoomescapeLockRepository roomescapeLockRepository) {
        this.roomescapeLockRepository = roomescapeLockRepository;
        initializeLockTable();
    }

    public void initializeLockTable() {
        if (!roomescapeLockRepository.existsById(lockId)) {
            RoomescapeLock lock = new RoomescapeLock(lockId);
            roomescapeLockRepository.save(lock);
        }
    }

    public String getRoomescapeLockId() {
        return lockId;
    }
}
