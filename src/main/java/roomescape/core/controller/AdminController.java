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
import roomescape.core.dto.reservation.ReservationRequest;
import roomescape.core.dto.reservation.ReservationResponse;
import roomescape.core.dto.reservationtime.ReservationTimeRequest;
import roomescape.core.dto.reservationtime.ReservationTimeResponse;
import roomescape.core.dto.theme.ThemeRequest;
import roomescape.core.dto.theme.ThemeResponse;
import roomescape.core.service.ReservationService;
import roomescape.core.service.ReservationTimeService;
import roomescape.core.service.ThemeService;

@Tag(name = "어드민 API", description = "어드민 페이지 관련 API 입니다.")
@Controller
@RequestMapping("/admin")
public class AdminController {
    private final ReservationService adminReservationService;
    private final ReservationTimeService reservationTimeService;
    private final ThemeService themeService;

    public AdminController(final ReservationService adminReservationService,
                           final ReservationTimeService reservationTimeService,
                           final ThemeService themeService) {
        this.adminReservationService = adminReservationService;
        this.reservationTimeService = reservationTimeService;
        this.themeService = themeService;
    }

    @Operation(summary = "어드민 메인 페이지")
    @GetMapping
    public String admin() {
        return "admin/index";
    }

    @Operation(summary = "어드민 예약 관리 페이지")
    @GetMapping("/reservation")
    public String reservation() {
        return "admin/reservation-new";
    }

    @Operation(summary = "어드민 예약 API")
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservationAsAdmin(
            @Valid @RequestBody final ReservationRequest request) {
        final ReservationResponse result = adminReservationService.create(request);
        return ResponseEntity.created(URI.create("/reservations/" + result.getId()))
                .body(result);
    }

    @Operation(summary = "어드민 시간 관리 페이지")
    @GetMapping("/time")
    public String time() {
        return "admin/time";
    }

    @Operation(summary = "어드민 시간 추가 API")
    @PostMapping("/times")
    public ResponseEntity<ReservationTimeResponse> createTime(
            @Valid @RequestBody final ReservationTimeRequest request) {
        final ReservationTimeResponse response = reservationTimeService.create(request);
        return ResponseEntity.created(URI.create("/times/" + response.getId()))
                .body(response);
    }

    @Operation(summary = "어드민 시간 삭제 API")
    @DeleteMapping("/times/{id}")
    public ResponseEntity<Void> deleteTime(@PathVariable("id") final long id) {
        reservationTimeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "어드민 테마 관리 페이지")
    @GetMapping("/theme")
    public String theme() {
        return "admin/theme";
    }

    @Operation(summary = "어드민 테마 추가 API")
    @PostMapping("/themes")
    public ResponseEntity<ThemeResponse> createTheme(@Valid @RequestBody final ThemeRequest request) {
        final ThemeResponse response = themeService.create(request);
        return ResponseEntity.created(URI.create("/themes/" + response.getId()))
                .body(response);
    }

    @Operation(summary = "어드민 테마 삭제 API")
    @DeleteMapping("/themes/{id}")
    public ResponseEntity<Void> deleteTheme(@PathVariable("id") final long id) {
        themeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "어드민 예약 대기 관리 페이지")
    @GetMapping("/waiting")
    public String waiting() {
        return "admin/waiting";
    }
}
