package roomescape.common.exception.custom;

import java.util.NoSuchElementException;

public class EntityNotFoundException extends NoSuchElementException {

    public EntityNotFoundException(final String message) {
        super(message);
    }
}
