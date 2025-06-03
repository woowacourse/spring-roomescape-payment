package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.config.RoomescapeLockInitializer;
import roomescape.repository.RoomescapeLockRepository;

@Service
public class RoomescapeLockService {

    private final RoomescapeLockRepository roomescapeLockRepository;
    private final String roomescapeLockId;

    public RoomescapeLockService(RoomescapeLockRepository roomescapeLockRepository,
                                 RoomescapeLockInitializer roomescapeLockInitializer) {
        this.roomescapeLockRepository = roomescapeLockRepository;
        this.roomescapeLockId = roomescapeLockInitializer.getRoomescapeLockId();
    }

    public void doPersistenceLock() {
        roomescapeLockRepository.findById(roomescapeLockId);
    }
}
