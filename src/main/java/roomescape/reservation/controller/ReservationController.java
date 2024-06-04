package roomescape.reservation.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import roomescape.admin.dto.AdminReservationDetailResponse;
import roomescape.admin.dto.AdminReservationRequest;
import roomescape.auth.annotation.Authenticated;
import roomescape.member.domain.LoginMember;
import roomescape.application.service.ReservationApplicationService;
import roomescape.reservation.dto.ReservationDetailResponse;
import roomescape.reservation.dto.ReservationPaymentRequest;
import roomescape.reservation.dto.ReservationPaymentResponse;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationService;

@RestController
public class ReservationController {
    private final ReservationService reservationService;
    private final ReservationApplicationService reservationApplicationService;

    public ReservationController(ReservationService reservationService, ReservationApplicationService reservationApplicationService) {
        this.reservationService = reservationService;
        this.reservationApplicationService = reservationApplicationService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationPaymentResponse> saveReservation(@Authenticated LoginMember loginMember,
                                                                      @RequestBody ReservationPaymentRequest request) {
        ReservationPaymentResponse response = reservationApplicationService.reservationPayment(loginMember, request);
        return ResponseEntity.created(URI.create("/reservations/" + response.reservationResponse().id()))
                .body(response);
    }

    @PostMapping("/reservations-waiting")
    public ResponseEntity<ReservationResponse> saveReservationWaiting(@Authenticated LoginMember loginMember,
                                                                      @RequestBody ReservationRequest reservationRequest) {
        ReservationResponse savedReservationResponse = reservationService.saveWaiting(loginMember, reservationRequest);
        return ResponseEntity.created(URI.create("/reservations-waiting/" + savedReservationResponse.id()))
                .body(savedReservationResponse);
    }

    @GetMapping("/reservations")
    public List<ReservationResponse> findAllReservations() {
        return reservationService.findAllReservations();
    }

    @GetMapping("/reservations/search")
    public List<ReservationResponse> searchReservation(@RequestParam Long themeId,
                                                       @RequestParam Long memberId,
                                                       @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}")
                                                       LocalDate dateFrom,
                                                       @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}")
                                                       LocalDate dateTo) {
        return reservationService.searchReservation(themeId, memberId, dateFrom, dateTo);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        reservationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/reservations-waiting/{id}")
    public ResponseEntity<Void> deleteReservationWaiting(@Authenticated LoginMember loginMember, @PathVariable long id) {
        reservationService.deleteByMemberIdAndId(loginMember, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("admin/reservations-waiting")
    public List<AdminReservationDetailResponse> findAllWaitingReservations() {
        return reservationService.findAllWaitingReservations();
    }

    @PostMapping("admin/reservations")
    public ResponseEntity<ReservationResponse> saveReservationByAdmin(
            @RequestBody AdminReservationRequest reservationRequest) {
        ReservationResponse reservationResponse = reservationService.saveByAdmin(reservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @DeleteMapping("admin/reservations-waiting/{id}")
    public ResponseEntity<Void> deleteReservationWaitingByAdmin(@PathVariable long id) {
        reservationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/member/reservation")
    public List<ReservationDetailResponse> findMemberReservations(@Authenticated LoginMember loginMember) {
        return reservationService.findAllByMemberId(loginMember.getId());
    }
}
