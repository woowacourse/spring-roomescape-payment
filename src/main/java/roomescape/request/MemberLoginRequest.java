package roomescape.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record MemberLoginRequest(
        @NotNull
        @NotEmpty
        String password,
        @NotNull
        @NotEmpty
        String email) {
}
