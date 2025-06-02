package roomescape.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.request.ReservationThemeRequest;
import roomescape.dto.response.ReservationThemeResponse;
import roomescape.service.reservation.ReservationThemeService;

@RequiredArgsConstructor
@RestController
public class AdminThemeController {

    private final ReservationThemeService reservationThemeService;

    @PostMapping("/admin/themes")
    public ResponseEntity<ReservationThemeResponse> save(@RequestBody ReservationThemeRequest request) {
        ReservationThemeResponse response = reservationThemeService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/admin/themes/{themeId}")
    public ResponseEntity<Void> remove(@PathVariable long themeId) {
        reservationThemeService.remove(themeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
