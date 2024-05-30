package roomescape.exception;

import java.util.Collection;

public record MultipleErrorResponses<T>(Collection<T> responses) {
}
