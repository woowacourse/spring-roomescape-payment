package roomescape.reservation.controller;

import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.auth.annotation.RequireRole;
import roomescape.global.auth.dto.UserInfo;
import roomescape.member.domain.MemberRole;
import roomescape.reservation.dto.request.AdminReservationRequest;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.response.MyReservationResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.ReservationResponseWithPayment;
import roomescape.reservation.service.ReservationFacadeService;

@Slf4j
@RestController
public class ReservationController {

    private final ReservationFacadeService reservationFacadeService;

    public ReservationController(final ReservationFacadeService reservationFacadeService) {
        this.reservationFacadeService = reservationFacadeService;
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> findReservations(
            @RequestParam(required = false) Long themeId,
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) LocalDate dateFrom,
            @RequestParam(required = false) LocalDate dateTo
    ) {
        return ResponseEntity.ok(reservationFacadeService.findReservations(themeId, memberId, dateFrom, dateTo));
    }

    @RequireRole(MemberRole.USER)
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponseWithPayment> createReservation(
            @RequestBody ReservationCreateRequest request,
            UserInfo userInfo
    ) {
        log.info("예약 생성 시도 memberId = {}, date = {}, timeId = {}, themeId = {}", userInfo.id(), request.getDate(),
                request.getTimeId(), request.getThemeId());
        ReservationResponseWithPayment dto = reservationFacadeService.create(request, userInfo.id());
        log.info("예약 생성 성공 memberId = {}, reservationId = {}", userInfo.id(), dto.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @RequireRole(MemberRole.ADMIN)
    @PostMapping("/admin/reservations")
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestBody AdminReservationRequest request
    ) {
        log.info("관리자 예약 생성 시도 date = {}, timeId = {}, themeId = {}", request.date(), request.timeId(),
                request.themeId());
        ReservationResponse dto = reservationFacadeService.createForAdmin(request.getReservationRequest(),
                request.memberId());
        log.info("관리자 예약 생성 성공");
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @RequireRole(MemberRole.USER)
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> deleteReservations(
            @PathVariable("id") Long id
    ) {
        log.info("예약 삭제 시도 reservationId = {}", id);
        reservationFacadeService.deleteReservation(id);
        log.info("예약 삭제 성공 reservationId = {}", id);
        return ResponseEntity.noContent().build();
    }

    @RequireRole(MemberRole.USER)
    @GetMapping("/reservations-mine")
    public ResponseEntity<List<MyReservationResponse>> findMyReservations(UserInfo userInfo) {
        List<MyReservationResponse> myReservations = reservationFacadeService.findMyReservations(userInfo);

        return ResponseEntity.ok().body(myReservations);
    }

}
