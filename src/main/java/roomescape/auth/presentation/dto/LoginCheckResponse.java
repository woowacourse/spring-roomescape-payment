package roomescape.auth.presentation.dto;

import roomescape.auth.application.LoginMember;

public record LoginCheckResponse(
        String name
) {

    public LoginCheckResponse(final LoginMember loginMember) {
        this(loginMember.getName());
    }
}
