package roomescape.user.ui;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.user.application.UserFacade;
import roomescape.user.ui.dto.UserResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserFacade userFacade;

    @GetMapping
    @Operation(summary = "사용자 전체 조회")
    public ResponseEntity<List<UserResponse>> getAll() {
        return ResponseEntity.ok(userFacade.getAll());
    }
}
