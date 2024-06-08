package roomescape.domain;

import jakarta.persistence.Entity;

@Entity
public class NotPayed extends Payment {

    public NotPayed() {
        super(State.READY);
    }
}
