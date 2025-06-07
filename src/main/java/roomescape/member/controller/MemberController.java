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

@Tag(name = "일반 유저", description = "유저 회원가입 및 조회")
@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "회원가입")
    @PostMapping
    public ResponseEntity<Void> create(
            @RequestBody @Valid final MemberCreateRequest request
    ) {
        memberService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .build();
    }

    @Operation(summary = "가입자 목록 조회", description = "모든 가입자 정보 조회")
    @GetMapping
    public ResponseEntity<List<MemberResponse>> findAll() {
        final List<MemberResponse> responses = memberService.findAll();

        return ResponseEntity.status(HttpStatus.OK)
                .body(responses);
    }
}
