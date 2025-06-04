package roomescape.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.controller.api.AdminThemeApi;
import roomescape.dto.request.ReservationThemeRequest;
import roomescape.dto.response.ReservationThemeResponse;
import roomescape.service.reservation.ReservationThemeService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminThemeController implements AdminThemeApi {

    private final ReservationThemeService reservationThemeService;

    @Override
    @PostMapping("/themes")
    public ResponseEntity<ReservationThemeResponse> save(@RequestBody ReservationThemeRequest request) {
        ReservationThemeResponse response = reservationThemeService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @DeleteMapping("/themes/{themeId}")
    public ResponseEntity<Void> remove(@PathVariable long themeId) {
        reservationThemeService.remove(themeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
