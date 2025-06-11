package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.domain.reservation.ReservationService;
import roomescape.domain.reservation.dto.AdminFilterReservationRequest;
import roomescape.domain.reservation.dto.AdminReservationRequest;
import roomescape.domain.reservation.dto.ReservationResponse;

@Tag(
        name = "어드민 전용 예약 컨트롤러",
        description = "어드민 전용 API들로, ADMIN role을 가진 쿠키를 통해 접근 가능, TOKEN 쿠키에 어드민 전용 JWT 필수"
)
@RestController
@RequestMapping("/admin/reservations")
@AllArgsConstructor
public class AdminReservationController {

    private final ReservationService reservationService;

    @Operation(
            description = "예약을 생성하고 생성된 예약을 응답으로 반환한다."
    )
    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @RequestBody @Valid final AdminReservationRequest request
    ) {
        final ReservationResponse response = reservationService.createForAdmin(request);
        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @Operation(
            description = "예약을 필터링하여 조회한다."
    )
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> readAllByMemberAndThemeAndDateRange(
            @ModelAttribute final AdminFilterReservationRequest request
    ) {
        final List<ReservationResponse> response = reservationService
                .readAllByMemberAndThemeAndDateRange(request);
        return ResponseEntity.ok(response);
    }
}
