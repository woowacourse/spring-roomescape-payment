package roomescape.external.lock;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class RoomescapeLock {

    @Id
    @Column(name = "lock_id")
    private String lockId;

    public RoomescapeLock(String lockId) {
        this.lockId = lockId;
    }

    protected RoomescapeLock() {

    }
}
