package roomescape.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Member;
import roomescape.presentation.dto.request.LoginMember;

import java.util.List;

@Schema(description = "회원 정보 응답 DTO")
public record MemberResponse(
        @Schema(description = "회원 ID")
        Long id,

        @Schema(description = "회원 이름", example = "홍길동")
        String name,

        @Schema(description = "회원 이메일", example = "user@example.com")
        String email
) {

    public static MemberResponse from(LoginMember loginMember) {
        return new MemberResponse(loginMember.id(), loginMember.name(), loginMember.email());
    }

    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getName(), member.getEmail());
    }

    public static List<MemberResponse> from(List<Member> members) {
        return members.stream()
                .map(MemberResponse::from)
                .toList();
    }
}
