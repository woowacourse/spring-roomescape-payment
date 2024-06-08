package roomescape.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public abstract class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private State state;

    //얘가 안에 있는 것이 자연스러운가? 밖에서도 객체를 만들기 위해서 사용하기도 하는데?
    enum State {
        READY, DONE
    }

    protected Payment(State state) {
        this.state = state;
    }

    protected Payment() {
    }
}
