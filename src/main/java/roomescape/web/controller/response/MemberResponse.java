package roomescape.web.controller.response;

import roomescape.service.response.MemberAppResponse;

public record MemberResponse(Long id, String name, String role) {

    public static MemberResponse from(MemberAppResponse memberAppResponse) {
        return new MemberResponse(memberAppResponse.id(), memberAppResponse.name(), memberAppResponse.role());
    }
}
