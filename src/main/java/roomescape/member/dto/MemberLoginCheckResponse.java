package roomescape.member.dto;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "멤버 로그인 확인 응답", description = "로그인이 제대로 되었는지 확인하기 위한 사용자 정보 응답.")
public record MemberLoginCheckResponse(String name) {
}
