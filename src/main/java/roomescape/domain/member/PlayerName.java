package roomescape.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import roomescape.exception.RoomescapeException;

@Embeddable
public class PlayerName {
    public static final int NAME_MAX_LENGTH = 20;

    @Column(nullable = false)
    private String name;

    protected PlayerName() {
    }

    public PlayerName(String name) {
        validateNonBlank(name);
        validateLength(name);
        this.name = name;
    }

    private void validateNonBlank(String name) {
        if (name == null || name.isBlank()) {
            throw new RoomescapeException("회원명은 필수 입력값 입니다.");
        }
    }

    private void validateLength(String name) {
        if (name != null && name.length() > NAME_MAX_LENGTH) {
            throw new RoomescapeException(String.format("회원명은 %d자 이하여야 합니다.", NAME_MAX_LENGTH));
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlayerName playerName)) {
            return false;
        }
        return Objects.equals(this.name, playerName.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
