package roomescape.controller.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.MemberResponse;
import roomescape.dto.reservation.ReservationFilterParam;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationSaveRequest;
import roomescape.dto.reservation.ReservationTimeResponse;
import roomescape.dto.theme.ThemeResponse;
import roomescape.service.MemberService;
import roomescape.service.ReservationService;
import roomescape.service.ReservationTimeService;
import roomescape.service.ThemeService;

import java.util.List;

@Tag(name = "예약 API(어드민전용)")
@RequestMapping("/admin/reservations")
@RestController
public class AdminReservationController {

    private final MemberService memberService;
    private final ReservationService reservationService;
    private final ReservationTimeService reservationTimeService;
    private final ThemeService themeService;

    public AdminReservationController(final MemberService memberService,
                                      final ReservationService reservationService,
                                      final ReservationTimeService reservationTimeService,
                                      final ThemeService themeService) {
        this.memberService = memberService;
        this.reservationService = reservationService;
        this.reservationTimeService = reservationTimeService;
        this.themeService = themeService;
    }

    @Operation(summary = "예약 생성")
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody final ReservationSaveRequest request) {
        final MemberResponse memberResponse = memberService.findById(request.memberId());
        final ReservationTimeResponse reservationTimeResponse = reservationTimeService.findById(request.timeId());
        final ThemeResponse themeResponse = themeService.findById(request.themeId());

        Reservation reservation = request.toReservation(memberResponse, themeResponse, reservationTimeResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.create(reservation));
    }

    @Operation(summary = "예약 목록")
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findReservations(
            @ModelAttribute final ReservationFilterParam reservationFilterParam) {
        return ResponseEntity.ok(reservationService.findAllBy(reservationFilterParam));
    }
}
