package roomescape.web.controller.response;

import roomescape.service.response.MemberDto;

public record MemberResponse(Long id, String name, String role) {

    public static MemberResponse from(MemberDto memberAppResponse) {
        return new MemberResponse(memberAppResponse.id(), memberAppResponse.name(), memberAppResponse.role());
    }
}
