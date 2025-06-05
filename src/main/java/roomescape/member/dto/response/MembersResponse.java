package roomescape.member.dto.response;

import java.util.List;

public record MembersResponse(List<MemberResponse> data
) {
    public static MembersResponse of(List<MemberResponse> data) {
        return new MembersResponse(data);
    }
}
