package roomescape.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.response.ReservationThemeResponse;
import roomescape.service.reservation.ReservationThemeService;

import java.util.List;

@Tag(name = "2. 테마 관련 API")
@RequiredArgsConstructor
@RestController
public class ReservationThemeController {

    private final ReservationThemeService reservationThemeService;

    @Operation(summary = "모든 테마 조회")
    @GetMapping("/themes")
    public ResponseEntity<List<ReservationThemeResponse>> getAll() {
        List<ReservationThemeResponse> response = reservationThemeService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "인기 테마 조회")
    @GetMapping("/themes/ranking")
    public ResponseEntity<List<ReservationThemeResponse>> getPopulars() {
        List<ReservationThemeResponse> response = reservationThemeService.getPopulars();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
