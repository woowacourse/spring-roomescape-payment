package roomescape.member.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.member.dto.response.MemberResponse;

@Schema(description = "회원 정보 응답")
public record MemberResponseDocs(
        @Schema(description = "회원 ID", example = "1")
        Long id,
        @Schema(description = "회원 이름", example = "홍길동")
        String name) {

    public static MemberResponseDocs from(MemberResponse response) {
        return new MemberResponseDocs(response.id(), response.name());
    }
} 