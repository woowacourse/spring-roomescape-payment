package roomescape.member.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import roomescape.global.logging.LogContent;
import roomescape.global.logging.LogExecution;
import roomescape.global.logging.LogLevel;
import roomescape.member.dto.MemberRegisterRequest;
import roomescape.member.dto.MemberRegisterResponse;
import roomescape.member.dto.MemberResponse;
import roomescape.member.service.MemberService;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @LogExecution(
            description = "회원 가입",
            content = {LogContent.REQUEST, LogContent.RESPONSE, LogContent.EXECUTION_TIME, LogContent.EXCEPTION},
            level = LogLevel.INFO
    )
    @PostMapping("/members")
    public ResponseEntity<MemberRegisterResponse> registerMember(@RequestBody final MemberRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.addMember(request));
    }

    @LogExecution(
            description = "전체 회원 조회",
            content = {LogContent.EXECUTION_TIME, LogContent.EXCEPTION},
            level = LogLevel.DEBUG,
            maskSensitiveData = false
    )
    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> getAllMembers() {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.getAllMembers());
    }
}
