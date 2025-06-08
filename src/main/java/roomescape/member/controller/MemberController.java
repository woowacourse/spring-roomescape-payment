package roomescape.member.controller;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.dto.request.SignupRequest;
import roomescape.member.dto.response.MemberResponse;
import roomescape.member.dto.response.SignUpResponse;
import roomescape.member.service.MemberService;

@Slf4j
@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ResponseEntity<List<MemberResponse>> findAllUsers() {
        return ResponseEntity.ok(memberService.findAllUsers());
    }

    @PostMapping
    public ResponseEntity<SignUpResponse> signUp(final @RequestBody SignupRequest signupRequest) {
        log.info("회원 가입 시도 email = {}", signupRequest.email());
        SignUpResponse response = memberService.signup(signupRequest);
        log.info("회원 가입 성공 email = {}", signupRequest.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
