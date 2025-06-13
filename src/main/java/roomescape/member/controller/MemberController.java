package roomescape.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.exception.dto.ErrorResponse;
import roomescape.member.dto.MemberCreateRequest;
import roomescape.member.dto.MemberResponse;
import roomescape.member.service.MemberService;

@Tag(name = "회원", description = "회원 API")
@Slf4j
@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "회원 생성", description = "이름, 이메일, 비밀번호로 새로운 회원을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원 생성 성공",
                    content = @Content(
                            mediaType = "application/json"
                    )),
            @ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody @Valid final MemberCreateRequest request
    ) {
        log.info("멤버 생성 요청: name={}, email={}", request.name(), request.email());
        memberService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "전체 회원 조회", description = "등록된 모든 회원 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "회원 목록 조회 성공",
            content = @Content(
                    mediaType = "application/json"
            ))
    @GetMapping
    public ResponseEntity<List<MemberResponse>> findAll() {
        final List<MemberResponse> responses = memberService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(responses);
    }
}
