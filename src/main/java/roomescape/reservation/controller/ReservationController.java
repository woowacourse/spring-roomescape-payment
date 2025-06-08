package roomescape.reservation.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoginMember;
import roomescape.payment.domain.PaymentMethod;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.reservation.dto.request.FilteringReservationRequest;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.request.ReservationPaymentRequest;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.BookedReservationTimeResponse;
import roomescape.reservation.dto.response.MyReservationsResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.service.ReservationService;

@Slf4j
@RequestMapping("/reservations")
@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @Valid @RequestBody final ReservationPaymentRequest request,
            final LoginMember loginMember
    ) {
        log.info("사용자 예약 생성 요청: memberId={}, date={}, timeId={}, themeId={}, amount={}",
                loginMember.id(), request.date(), request.timeId(), request.themeId(), request.amount());

        ReservationCreateRequest createRequest = ReservationCreateRequest.from(
                new ReservationRequest(
                        request.date(),
                        request.timeId(),
                        request.themeId()
                ), loginMember
        );
        PaymentRequest paymentRequest = new PaymentRequest(
                request.paymentKey(),
                request.orderId(),
                request.amount(),
                PaymentMethod.TOSS
        );
        ReservationResponse response = reservationService.createWithPayment(createRequest, paymentRequest);

        return ResponseEntity.created(URI.create("/reservations/" + response.id()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> readAllReservations() {
        List<ReservationResponse> response = reservationService.getAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filtering")
    public ResponseEntity<List<ReservationResponse>> readFilteredReservations(
            @ModelAttribute @Valid final FilteringReservationRequest request
    ) {
        final List<ReservationResponse> reservationResponses =
                reservationService.getFilteredReservations(request);
        return ResponseEntity.ok(reservationResponses);
    }

    @GetMapping("/times/available")
    public ResponseEntity<List<BookedReservationTimeResponse>> readAvailableReservationTimes(
            @RequestParam("date") final LocalDate date,
            @RequestParam("themeId") final Long themeId
    ) {
        List<BookedReservationTimeResponse> responses = reservationService.getSortedAvailableTimes(date, themeId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/my")
    public ResponseEntity<List<MyReservationsResponse>> readMyReservations(final @Valid LoginMember loginMember) {
        List<MyReservationsResponse> response = reservationService.getAllMyReservations(loginMember);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> delete(@PathVariable("reservationId") final Long reservationId) {
        log.info("예약 삭제 요청: reservationId={}", reservationId);
        reservationService.delete(reservationId);
        return ResponseEntity.noContent().build();
    }
}
