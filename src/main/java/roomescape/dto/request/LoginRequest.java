package roomescape.dto.request;

import jakarta.validation.constraints.Email;
import roomescape.global.aop.Sensitive;

public record LoginRequest(
        @Email String email,
        @Sensitive String password
) {
}
