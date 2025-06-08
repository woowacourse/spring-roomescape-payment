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
import roomescape.application.ReservationService;
import roomescape.application.UserService;
import roomescape.presentation.request.CreateReservationAdminRequest;
import roomescape.presentation.response.ReservationResponse;
import roomescape.presentation.response.ReservationWithOrderResponse;
import roomescape.presentation.response.UserResponse;

@Tag(name = "Admin", description = "관리자 기능 관련 API")
@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    private final ReservationService reservationService;
    private final UserService userService;

    @Operation(summary = "예약 생성", description = "예약 관리 페이지에서 예약을 생성합니다.")
    @PostMapping("/reservations")
    @ResponseStatus(CREATED)
    public ReservationResponse reserve(@RequestBody @Valid final CreateReservationAdminRequest request) {
        var reservation = reservationService.reserve(request.userId(), request.date(), request.timeId(), request.themeId());
        return ReservationResponse.from(reservation);
    }

    @Operation(summary = "사용자 조회", description = "관리자가 모든 사용자를 조회합니다.")
    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        var users = userService.findAllUsers();
        return UserResponse.from(users);
    }

    @Operation(summary = "예약대기 조회", description = "관리자가 모든 예약대기 내역을 조회합니다.")
    @GetMapping("/waitings")
    public List<ReservationWithOrderResponse> getAllWaitings() {
        var waitings = reservationService.findAllWaitings();
        return ReservationWithOrderResponse.from(waitings);
    }
}
