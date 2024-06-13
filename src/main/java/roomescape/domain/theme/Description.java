package roomescape.domain.theme;

import static roomescape.exception.ErrorCode.THEME_DESCRIPTION_LENGTH_ERROR;

import jakarta.persistence.Embeddable;
import roomescape.exception.RoomEscapeException;

@Embeddable
public class Description {

    private static final int MIN_DESCRIPTION_LENGTH = 10;

    private String description;

    protected Description() {
    }

    public Description(String description) {
        validateDescription(description);
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    private void validateDescription(String description) {
        if (description.length() < MIN_DESCRIPTION_LENGTH) {
            throw new RoomEscapeException(
                    THEME_DESCRIPTION_LENGTH_ERROR,
                    " theme_description = " + description
            );
        }
    }
}
