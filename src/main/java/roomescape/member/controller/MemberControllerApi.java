package roomescape.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import roomescape.member.dto.response.FindMembersResponse;

public interface MemberControllerApi {

    @Operation(summary = "회원 목록 조회",
            description = "전체 회원 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "회원 목록 조회 성공", content = @Content(schema = @Schema(implementation = FindMembersResponse.class)))
    ResponseEntity<List<FindMembersResponse>> getMembers();
}
