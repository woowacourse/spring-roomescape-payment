package roomescape.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.api.MemberThemeApi;
import roomescape.dto.response.ReservationThemeResponse;
import roomescape.service.reservation.ReservationThemeService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ThemeController implements MemberThemeApi {

    private final ReservationThemeService reservationThemeService;

    @Override
    @GetMapping("/themes")
    public ResponseEntity<List<ReservationThemeResponse>> getAll() {
        List<ReservationThemeResponse> response = reservationThemeService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Override
    @GetMapping("/themes/ranking")
    public ResponseEntity<List<ReservationThemeResponse>> getPopulars() {
        List<ReservationThemeResponse> response = reservationThemeService.getPopulars();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
