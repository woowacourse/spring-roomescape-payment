package roomescape.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.response.ReservationThemeResponse;
import roomescape.service.reservation.ReservationThemeService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ReservationThemeController {

    private final ReservationThemeService reservationThemeService;

    @GetMapping("/themes")
    public ResponseEntity<List<ReservationThemeResponse>> reservationThemeList() {
        List<ReservationThemeResponse> response = reservationThemeService.findReservationThemes();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/themes/ranking")
    public ResponseEntity<List<ReservationThemeResponse>> reservationThemeRankingList() {
        List<ReservationThemeResponse> response = reservationThemeService.findPopularThemes();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
