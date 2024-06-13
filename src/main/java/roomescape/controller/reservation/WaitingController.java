package roomescape.controller.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.auth.AuthenticationPrincipal;
import roomescape.domain.reservation.Waiting;
import roomescape.dto.MemberResponse;
import roomescape.dto.auth.LoginMember;
import roomescape.dto.reservation.AutoReservedFilter;
import roomescape.dto.reservation.MemberReservationSaveRequest;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationSaveRequest;
import roomescape.dto.reservation.ReservationTimeResponse;
import roomescape.dto.theme.ThemeResponse;
import roomescape.service.AutoReserveService;
import roomescape.service.MemberService;
import roomescape.service.ReservationTimeService;
import roomescape.service.ThemeService;
import roomescape.service.WaitingService;

@Tag(name = "예약대기 API(공용)")
@RestController
@RequestMapping("/waiting")
public class WaitingController {

    private final MemberService memberService;
    private final WaitingService waitingService;
    private final ReservationTimeService reservationTimeService;
    private final ThemeService themeService;
    private final AutoReserveService autoReserveService;

    public WaitingController(final MemberService memberService,
                             final WaitingService waitingService,
                             final ReservationTimeService reservationTimeService,
                             final ThemeService themeService,
                             final AutoReserveService autoReserveService) {
        this.memberService = memberService;
        this.waitingService = waitingService;
        this.reservationTimeService = reservationTimeService;
        this.themeService = themeService;
        this.autoReserveService = autoReserveService;
    }

    @Operation(summary = "예약대기 생성")
    @PostMapping
    public ResponseEntity<ReservationResponse> createWaiting(@AuthenticationPrincipal final LoginMember loginMember,
                                                             @RequestBody final MemberReservationSaveRequest request) {
        final MemberResponse memberResponse = memberService.findById(loginMember.id());
        final ReservationSaveRequest saveRequest = request.generateReservationSaveRequest(memberResponse);
        final ReservationTimeResponse reservationTimeResponse = reservationTimeService.findById(request.timeId());
        final ThemeResponse themeResponse = themeService.findById(request.themeId());

        final Waiting waiting = saveRequest.toWaiting(memberResponse, themeResponse, reservationTimeResponse);
        ReservationResponse response = waitingService.create(waiting);

        AutoReservedFilter filter = AutoReservedFilter.from(response);
        autoReserveService.reserveWaiting(filter);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "예약대기 취소")
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<ReservationResponse> cancelWaiting(@AuthenticationPrincipal final LoginMember loginMember,
                                                             @PathVariable final Long reservationId) {
        waitingService.cancel(loginMember.id(), reservationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
