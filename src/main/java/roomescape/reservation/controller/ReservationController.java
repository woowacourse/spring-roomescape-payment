package roomescape.reservation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import roomescape.auth.domain.AuthInfo;
import roomescape.global.annotation.LoginUser;
import roomescape.reservation.controller.dto.MyReservationResponse;
import roomescape.reservation.controller.dto.ReservationPaymentRequest;
import roomescape.reservation.controller.dto.ReservationQueryRequest;
import roomescape.reservation.controller.dto.ReservationRequest;
import roomescape.reservation.controller.dto.ReservationResponse;
import roomescape.reservation.controller.dto.WaitingRequest;
import roomescape.reservation.domain.specification.ReservationSpecification;
import roomescape.reservation.service.ReservationApplicationService;
import roomescape.reservation.service.dto.MemberReservationCreate;
import roomescape.reservation.service.dto.WaitingCreate;

@RestController
@RequestMapping("/reservations")
public class ReservationController implements ReservationSpecification {

    private final ReservationApplicationService reservationApplicationService;

    public ReservationController(ReservationApplicationService reservationApplicationService) {
        this.reservationApplicationService = reservationApplicationService;
    }

    @GetMapping
    public List<ReservationResponse> reservations(
            @RequestParam(value = "themeId", required = false) Long themeId,
            @RequestParam(value = "memberId", required = false) Long memberId,
            @RequestParam(value = "dateFrom", required = false) LocalDate startDate,
            @RequestParam(value = "dateTo", required = false) LocalDate endDate
    ) {
        return reservationApplicationService.findMemberReservations(
                new ReservationQueryRequest(themeId, memberId, startDate, endDate));
    }

    @PostMapping("/payment")
    public ReservationResponse pay(@LoginUser AuthInfo authInfo,
                                   @RequestBody @Valid ReservationPaymentRequest reservationPaymentRequest) {
        return reservationApplicationService.publish(authInfo, reservationPaymentRequest);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse create(@LoginUser AuthInfo authInfo,
                                      @RequestBody @Valid ReservationRequest reservationRequest) {
        MemberReservationCreate memberReservationCreate = new MemberReservationCreate(
                authInfo.getId(),
                reservationRequest.themeId(),
                reservationRequest.timeId(),
                reservationRequest.paymentKey(),
                reservationRequest.orderId(),
                reservationRequest.amount(),
                reservationRequest.date()
        );
        return reservationApplicationService.createMemberReservation(memberReservationCreate);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@LoginUser AuthInfo authInfo,
                       @PathVariable("id") @Min(1) long reservationMemberId) {
        reservationApplicationService.deleteMemberReservation(authInfo, reservationMemberId);
    }

    @GetMapping("/my")
    public List<MyReservationResponse> getMyReservations(@LoginUser AuthInfo authInfo) {
        return reservationApplicationService.findMyReservations(authInfo)
                .stream()
                .map(MyReservationResponse::from)
                .toList();
    }

    @GetMapping("/waiting")
    public List<ReservationResponse> getWaiting() {
        return reservationApplicationService.getWaiting();
    }

    @PostMapping("/waiting")
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse addWaiting(@LoginUser AuthInfo authInfo,
                                          @RequestBody @Valid WaitingRequest waitingRequest) {
        WaitingCreate waitingCreate = new WaitingCreate(
                authInfo.getId(),
                waitingRequest.date(),
                waitingRequest.timeId(),
                waitingRequest.themeId()
        );
        return reservationApplicationService.addWaiting(waitingCreate);
    }

    @DeleteMapping("/{id}/waiting")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWaiting(@LoginUser AuthInfo authInfo,
                              @PathVariable("id") @Min(1) long reservationMemberId) {
        reservationApplicationService.deleteWaiting(authInfo, reservationMemberId);
    }
}
