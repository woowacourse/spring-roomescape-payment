package roomescape.presentation.rest;

import static org.springframework.http.HttpStatus.CREATED;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.RoomescapeService;
import roomescape.application.UserService;
import roomescape.domain.auth.AuthenticationInfo;
import roomescape.presentation.request.SignupRequest;
import roomescape.presentation.response.UserReservationResponse;
import roomescape.presentation.response.UserResponse;

@Tag(name = "User", description = "사용자 관련 API")
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final RoomescapeService roomescapeService;

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 이름을 입력하면 회원가입을 요청합니다.")
    @PostMapping
    @ResponseStatus(CREATED)
    public UserResponse register(@RequestBody @Valid final SignupRequest request) {
        var user = userService.register(request.email(), request.password(), request.name());
        return UserResponse.from(user);
    }

    @Operation(summary = "내 예약 조회", description = "사용자의 예약과 예약대기 내역을 모두 조회합니다.")
    @GetMapping("/reservations")
    public List<UserReservationResponse> getAllReservationsByUser(final AuthenticationInfo authenticationInfo) {
        var reservations = roomescapeService.getAllReservationsByUser(authenticationInfo.id());
        return UserReservationResponse.from(reservations);
    }
}
