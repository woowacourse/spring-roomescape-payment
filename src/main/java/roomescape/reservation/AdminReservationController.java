package roomescape.reservation;

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
import roomescape.reservation.dto.AdminFilterReservationRequest;
import roomescape.reservation.dto.AdminReservationRequest;
import roomescape.reservation.dto.ReservationResponse;

@RestController
@RequestMapping("/admin/reservations")
@AllArgsConstructor
@Tag(name = "관리자 예약 관련 API", description = "관리자가 예약을 생성하고 조회하는 API입니다.")
public class AdminReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "관리자 예약 생성", description = "관리자가 예약을 생성합니다.")
    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @RequestBody @Valid final AdminReservationRequest request
    ) {
        final ReservationResponse response = reservationService.createForAdmin(request);
        return ResponseEntity.created(URI.create("/reservations/" + response.id())).body(response);
    }

    @Operation(summary = "관리자 예약 조회", description = "관리자가 특정 회원, 테마, 날짜 범위에 대한 예약을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> readAllByMemberAndThemeAndDateRange(
            @ModelAttribute final AdminFilterReservationRequest request
    ) {
        final List<ReservationResponse> response = reservationService
                .readAllByMemberAndThemeAndDateRange(request);
        return ResponseEntity.ok(response);
    }
}
