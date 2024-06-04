package roomescape.reservation.presentation;

import jakarta.validation.Valid;
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
import roomescape.auth.Authenticated;
import roomescape.auth.dto.Accessor;
import roomescape.payment.TossPaymentClient;
import roomescape.payment.dto.request.PaymentConfirmRequest;
import roomescape.payment.dto.response.PaymentConfirmResponse;
import roomescape.reservation.dto.MemberReservationAddRequest;
import roomescape.reservation.dto.MemberReservationStatusResponse;
import roomescape.reservation.dto.MemberReservationWithPaymentAddRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationService;

@RestController
public class ReservationController {

    private final ReservationService reservationService;
    private final TossPaymentClient tossPaymentClient;

    public ReservationController(ReservationService reservationService, TossPaymentClient tossPaymentClient) {
        this.reservationService = reservationService;
        this.tossPaymentClient = tossPaymentClient;
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getReservationList() {
        return ResponseEntity.ok(reservationService.findAllReservation());
    }

    @GetMapping("/reservations/{id}")
    public ResponseEntity<MemberReservationStatusResponse> getReservationById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(reservationService.findById(id));
    }

    @GetMapping(path = "/reservations", params = {"memberId", "themeId", "dateFrom", "dateTo"})
    public ResponseEntity<List<ReservationResponse>> findAllByMemberAndThemeAndPeriod(
            @RequestParam(name = "memberId") Long memberId,
            @RequestParam(name = "themeId") Long themeId,
            @RequestParam(name = "dateFrom") LocalDate dateFrom,
            @RequestParam(name = "dateTo") LocalDate dateTo) {
        return ResponseEntity.ok(
                reservationService.findAllByMemberAndThemeAndPeriod(memberId, themeId, dateFrom, dateTo)
        );
    }

    @GetMapping("/reservations/my")
    public ResponseEntity<List<MemberReservationStatusResponse>> findMemberReservationStatus(
            @Authenticated Accessor accessor) {
        return ResponseEntity.ok(reservationService.findAllByMemberId(accessor.id()));
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> saveMemberReservation(
            @Authenticated Accessor accessor,
            @Valid @RequestBody MemberReservationWithPaymentAddRequest memberReservationWithPaymentAddRequest) {

        MemberReservationAddRequest memberReservationAddRequest = new MemberReservationAddRequest(
                memberReservationWithPaymentAddRequest.date(),
                memberReservationWithPaymentAddRequest.timeId(),
                memberReservationWithPaymentAddRequest.timeId()
        );

        PaymentConfirmResponse paymentConfirmResponse = tossPaymentClient.confirmPayments(new PaymentConfirmRequest(
                memberReservationWithPaymentAddRequest.paymentKey(),
                memberReservationWithPaymentAddRequest.orderId(),
                memberReservationWithPaymentAddRequest.amount()
        ));

        ReservationResponse saveResponse = reservationService.saveMemberReservation(accessor.id(),
                memberReservationAddRequest);

        URI createdUri = URI.create("/reservations/" + saveResponse.id());
        return ResponseEntity.created(createdUri).body(saveResponse);
    }

    @PostMapping("/reservations/waiting")
    public ResponseEntity<ReservationResponse> saveMemberWaitingReseravtion(
            @Authenticated Accessor accessor,
            @Valid @RequestBody MemberReservationAddRequest memberReservationAddRequest) {
        ReservationResponse saveResponse = reservationService.saveMemberWaitingReservation(accessor.id(),
                memberReservationAddRequest);
        URI createdUri = URI.create("/reservations/" + saveResponse.id());
        return ResponseEntity.created(createdUri).body(saveResponse);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> removeReservation(@PathVariable("id") Long id) {
        reservationService.removeReservation(id);
        return ResponseEntity.noContent().build();
    }
}