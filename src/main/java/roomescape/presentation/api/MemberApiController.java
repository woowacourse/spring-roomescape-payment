package roomescape.presentation.api;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.AuthRequired;
import roomescape.auth.Role;
import roomescape.business.model.vo.UserRole;
import roomescape.business.service.MemberService;
import roomescape.presentation.dto.request.RegisterRequest;
import roomescape.presentation.dto.response.UserResponse;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/members")
    @AuthRequired
    @Role(UserRole.ADMIN)
    public List<UserResponse> getUsers() {
        return memberService.getAll();
    }

    @PostMapping("/members")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        final UserResponse response = memberService.register(request);
        return ResponseEntity.created(URI.create("/members")).body(response);
    }
}
