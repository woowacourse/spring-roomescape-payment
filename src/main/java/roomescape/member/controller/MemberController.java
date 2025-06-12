package roomescape.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.service.MemberService;
import roomescape.member.service.dto.request.MemberCreateRequest;
import roomescape.member.service.dto.response.MemberResponse;

import java.util.List;

@RestController
@RequestMapping("/members")
@Tag(name = "멤버 컨트롤러", description = "멤버에 관한 API 모음")
public class MemberController {

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    @Operation(summary = "전체 멤버 조회", description = "전체 멤버 목록을 조회합니다.")
    public ResponseEntity<List<MemberResponse>> findAll() {
        final List<MemberResponse> responses = memberService.findAll();

        return ResponseEntity.status(HttpStatus.OK)
                .body(responses);
    }

    @PostMapping
    @Operation(summary = "멤버 생성", description = "이메일, 패스워드, 이름을 포함한 멤버를 생성합니다.")
    public ResponseEntity<Void> create(
            @RequestBody @Valid final MemberCreateRequest request
    ) {
        memberService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }
}
