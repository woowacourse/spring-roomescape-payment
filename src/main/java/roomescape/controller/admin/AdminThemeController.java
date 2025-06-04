package roomescape.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.dto.request.ReservationThemeRequest;
import roomescape.dto.response.ReservationThemeResponse;
import roomescape.service.reservation.ReservationThemeService;

@Tag(name = "5. 어드민 전용 API")
@RequiredArgsConstructor
@RestController
public class AdminThemeController {

    private final ReservationThemeService reservationThemeService;

    @Operation(summary = "테마 추가")
    @PostMapping("/admin/themes")
    public ResponseEntity<ReservationThemeResponse> save(@RequestBody ReservationThemeRequest request) {
        ReservationThemeResponse response = reservationThemeService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "테마 삭제")
    @DeleteMapping("/admin/themes/{themeId}")
    public ResponseEntity<Void> remove(@PathVariable long themeId) {
        reservationThemeService.remove(themeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
