package roomescape.member.ui;

import static roomescape.auth.domain.AuthRole.ADMIN;
import static roomescape.auth.domain.AuthRole.MEMBER;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.domain.RequiresRole;
import roomescape.member.application.MemberService;
import roomescape.member.domain.Member;
import roomescape.member.ui.dto.MemberResponse;
import roomescape.member.ui.dto.MemberResponse.IdName;
import roomescape.member.ui.dto.SignUpRequest;

@Tag(name = "멤버", description = "멤버 관련 api")
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberRestController {

    private final MemberService memberService;

    @Operation(summary = "멤버 생성", description = "멤버 생성 관련 api")
    @PostMapping
    public ResponseEntity<MemberResponse.IdName> create(
            @RequestBody @Valid final SignUpRequest request
    ) {
        final MemberResponse.IdName response = memberService.create(request);

        return ResponseEntity.created(URI.create("/members/" + response.id()))
                .body(response);
    }

    @Operation(summary = "멤버 삭제", description = "멤버 삭제 관련 api")
    @DeleteMapping("/{id}")
    @RequiresRole(authRoles = {ADMIN, MEMBER})
    public ResponseEntity<Void> delete(
            @PathVariable final Long id,
            final Member member
    ) {
        if (id == null || member == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .build();
        }
        if (!member.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .build();
        }
        memberService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }

    @Operation(summary = "모든 멤버 조회", description = "모든 멤버 조회 관련 api")
    @GetMapping
    @RequiresRole(authRoles = {ADMIN})
    public ResponseEntity<List<IdName>> findAll() {
        final List<IdName> responses = memberService.findAllNames();

        return ResponseEntity.status(HttpStatus.OK)
                .body(responses);
    }
}
