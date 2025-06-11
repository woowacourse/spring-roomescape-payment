package roomescape.member.controller.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import roomescape.member.dto.MemberCreateRequest;
import roomescape.member.dto.MemberResponse;

@Tag(name = "Member", description = "사용자 API")
@RequestMapping("/members")
public interface MemberApi {

    @Operation(summary = "사용자 생성", description = "사용자를 생성한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "사용자 생성 성공")
    })
    @PostMapping
    ResponseEntity<Void> create(
            @RequestBody @Valid final MemberCreateRequest request
    );

    @Operation(summary = "전체 사용자 조회", description = "전체 사용자 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "전체 사용자 조회 성공")
    })
    @GetMapping
    ResponseEntity<List<MemberResponse>> findAll();
}
