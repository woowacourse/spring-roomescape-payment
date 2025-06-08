package roomescape.theme.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.logging.LogExecution;
import roomescape.theme.dto.ReservationThemeRequest;
import roomescape.theme.dto.ReservationThemeResponse;
import roomescape.theme.service.ReservationThemeService;

@RestController
@RequestMapping("/themes")
public class ReservationThemeController {

    private final ReservationThemeService reservationThemeService;

    public ReservationThemeController(final ReservationThemeService reservationThemeService) {
        this.reservationThemeService = reservationThemeService;
    }

    @GetMapping()
    public ResponseEntity<List<ReservationThemeResponse>> reservationThemeList() {
        return ResponseEntity.status(HttpStatus.OK).body(reservationThemeService.findReservationThemes());
    }

    @GetMapping("/ranking")
    public ResponseEntity<List<ReservationThemeResponse>> reservationThemeRankingList() {
        return ResponseEntity.status(HttpStatus.OK).body(reservationThemeService.findPopularThemes());
    }

    @LogExecution(
            description = "테마 생성",
            content = {LogExecution.LogContent.REQUEST, LogExecution.LogContent.RESPONSE, LogExecution.LogContent.EXECUTION_TIME, LogExecution.LogContent.EXCEPTION},
            level = LogExecution.LogLevel.INFO,
            maskSensitiveData = false
    )
    @PostMapping()
    public ResponseEntity<ReservationThemeResponse> reservationThemeAdd(
            @RequestBody ReservationThemeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationThemeService.addReservationTheme(request));
    }

    @LogExecution(
            description = "테마 삭제",
            content = {LogExecution.LogContent.REQUEST, LogExecution.LogContent.EXECUTION_TIME, LogExecution.LogContent.EXCEPTION},
            level = LogExecution.LogLevel.WARN,
            maskSensitiveData = false
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> reservationThemeRemove(@PathVariable(name = "id") long id) {
        reservationThemeService.removeReservationTheme(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
