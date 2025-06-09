package roomescape.member.presentation;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.application.MemberService;
import roomescape.member.dto.request.MemberRequest;
import roomescape.member.dto.response.MemberResponse;

@Slf4j
@RestController
public class MemberController {

    private final MemberService memberService;

    public MemberController(final MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> findAll() {
        log.info("모든 회원 조회 요청 수신");
        List<MemberResponse> members = memberService.findAll();

        log.debug("조회된 회원 수: {}", members.size());
        return ResponseEntity.ok(members);
    }

    @PostMapping("/members")
    public ResponseEntity<MemberResponse> createMember(@RequestBody final MemberRequest request) {
        log.info("회원 생성 요청 수신: email={}, name={}", request.email(), request.name());
        MemberResponse response = memberService.save(request);

        log.info("회원 생성 완료: id={}, name={}", response.id(), response.name());
        return ResponseEntity.ok().body(response);
    }
}
