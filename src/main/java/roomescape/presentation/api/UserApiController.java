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
import roomescape.business.service.UserService;
import roomescape.presentation.dto.request.RegisterRequest;
import roomescape.presentation.dto.response.UserResponse;

@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;

    @GetMapping("/members")
    @AuthRequired
    @Role(UserRole.ADMIN)
    public List<UserResponse> getUsers() {
        return userService.getAll();
    }

    @PostMapping("/members")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
        final UserResponse response = userService.register(request.name(), request.email(), request.password());
        return ResponseEntity.created(URI.create("/members")).body(response);
    }
}
