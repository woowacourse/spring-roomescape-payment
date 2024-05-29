package roomescape.core.controller;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import roomescape.core.dto.member.LoginMember;
import roomescape.core.dto.reservation.ReservationRequest;
import roomescape.core.dto.reservation.ReservationResponse;
import roomescape.core.dto.reservationtime.ReservationTimeRequest;
import roomescape.core.dto.reservationtime.ReservationTimeResponse;
import roomescape.core.dto.theme.ThemeRequest;
import roomescape.core.dto.theme.ThemeResponse;
import roomescape.core.service.ReservationService;
import roomescape.core.service.ReservationTimeService;
import roomescape.core.service.ThemeService;
import roomescape.core.service.WaitingService;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final ReservationService reservationService;
    private final ReservationTimeService reservationTimeService;
    private final ThemeService themeService;
    private final WaitingService waitingService;

    public AdminController(final ReservationService reservationService,
                           final ReservationTimeService reservationTimeService,
                           final ThemeService themeService,
                           final WaitingService waitingService) {
        this.reservationService = reservationService;
        this.reservationTimeService = reservationTimeService;
        this.themeService = themeService;
        this.waitingService = waitingService;
    }

    @GetMapping
    public String admin() {
        return "admin/index";
    }

    @GetMapping("/reservation")
    public String reservation() {
        return "admin/reservation-new";
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservationAsAdmin(
            @Valid @RequestBody final ReservationRequest request) {
        final ReservationResponse response = reservationService.create(request);
        return ResponseEntity.created(URI.create("/reservations/" + response.getId()))
                .body(response);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("id") final long id,
                                                  final LoginMember loginMember) {
        reservationService.deleteByAdmin(id, loginMember);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/time")
    public String time() {
        return "admin/time";
    }

    @PostMapping("/times")
    public ResponseEntity<ReservationTimeResponse> createTime(
            @Valid @RequestBody final ReservationTimeRequest request) {
        final ReservationTimeResponse response = reservationTimeService.create(request);
        return ResponseEntity.created(URI.create("/times/" + response.getId()))
                .body(response);
    }

    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> deleteTime(@PathVariable("id") final long id) {
        reservationTimeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/theme")
    public String theme() {
        return "admin/theme";
    }

    @PostMapping("/themes")
    public ResponseEntity<ThemeResponse> createTheme(
            @Valid @RequestBody final ThemeRequest request) {
        final ThemeResponse response = themeService.create(request);
        return ResponseEntity.created(URI.create("/themes/" + response.getId()))
                .body(response);
    }

    @DeleteMapping("/themes/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable("id") final long id) {
        themeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/waiting")
    public String waiting() {
        return "admin/waiting";
    }

    @DeleteMapping("/waitings/{id}")
    public ResponseEntity<Void> deleteWaiting(@PathVariable("id") final long id,
                                              final LoginMember loginMember) {
        waitingService.deleteByAdmin(id, loginMember);
        return ResponseEntity.noContent().build();
    }
}
