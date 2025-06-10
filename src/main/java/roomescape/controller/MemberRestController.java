package roomescape.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.api.MemberRestControllerInterface;
import roomescape.domain.member.dto.MemberResponse;
import roomescape.domain.member.entity.Member;
import roomescape.domain.member.service.MemberService;

@RequiredArgsConstructor
@RestController
public class MemberRestController implements MemberRestControllerInterface {

    private final MemberService memberService;

    @Override
    public ResponseEntity<List<MemberResponse>> getMembers() {
        final List<Member> members = memberService.findAll();
        final List<MemberResponse> memberResponses = members.stream()
                .map(MemberResponse::from)
                .toList();

        return ResponseEntity.ok(memberResponses);
    }
}
