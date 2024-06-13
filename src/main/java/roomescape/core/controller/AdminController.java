package roomescape.core.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import roomescape.core.dto.reservation.AdminReservationRequest;
import roomescape.core.dto.reservation.ReservationResponse;
import roomescape.core.dto.reservationtime.ReservationTimeRequest;
import roomescape.core.dto.reservationtime.ReservationTimeResponse;
import roomescape.core.dto.theme.ThemeRequest;
import roomescape.core.dto.theme.ThemeResponse;
import roomescape.core.service.ReservationService;
import roomescape.core.service.ReservationTimeService;
import roomescape.core.service.ThemeService;

@Tag(name = "관리자용 API")
@Controller
@RequestMapping("/admin")
public class AdminController {
    private final ReservationService reservationService;
    private final ReservationTimeService reservationTimeService;
    private final ThemeService themeService;

    public AdminController(final ReservationService reservationService,
                           final ReservationTimeService reservationTimeService,
                           final ThemeService themeService) {
        this.reservationService = reservationService;
        this.reservationTimeService = reservationTimeService;
        this.themeService = themeService;
    }

    @GetMapping
    public String admin() {
        return "admin/index";
    }

    @GetMapping("/reservation")
    public String reservation() {
        return "admin/reservation-new";
    }

    @GetMapping("/time")
    public String time() {
        return "admin/time";
    }

    @GetMapping("/theme")
    public String theme() {
        return "admin/theme";
    }

    @GetMapping("/waiting")
    public String waiting() {
        return "admin/waiting";
    }

    @Operation(summary = "관리자가 직접 예약 추가", description = "관리자가 예약을 추가할 때는 결제가 필요 없습니다.")
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody final AdminReservationRequest adminReservationRequest) {
        ReservationResponse reservationResponse = reservationService.create(adminReservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.getId()))
                .body(reservationResponse);
    }

    @Operation(summary = "예약 가능한 시간 추가")
    @PostMapping("/times")
    public ResponseEntity<ReservationTimeResponse> createTime(
            @Valid @RequestBody final ReservationTimeRequest request) {
        final ReservationTimeResponse response = reservationTimeService.create(request);
        return ResponseEntity.created(URI.create("/times/" + response.getId()))
                .body(response);
    }

    @Operation(summary = "예약 가능한 테마 추가")
    @PostMapping("/themes")
    public ResponseEntity<ThemeResponse> createTheme(@Valid @RequestBody final ThemeRequest request) {
        final ThemeResponse response = themeService.create(request);
        return ResponseEntity.created(URI.create("/themes/" + response.getId()))
                .body(response);
    }

    @Operation(summary = "예약 가능한 시간 삭제")
    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> deleteTime(@PathVariable("id") final long id) {
        reservationTimeService.delete(id);
        return ResponseEntity.noContent()
                .build();
    }

    @Operation(summary = "예약 가능한 테마 삭제")
    @DeleteMapping("/themes/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable("id") final long id) {
        themeService.delete(id);
        return ResponseEntity.noContent()
                .build();
    }
}
