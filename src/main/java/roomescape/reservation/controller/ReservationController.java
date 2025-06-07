package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import roomescape.auth.service.dto.LoginMember;
import roomescape.reservation.service.CreateReservationWithPaymentService;
import roomescape.reservation.service.DeleteReservationService;
import roomescape.reservation.service.ReservationQueryService;
import roomescape.reservation.service.dto.request.FilteringReservationRequest;
import roomescape.reservation.service.dto.request.ReservationWithPaymentRequest;
import roomescape.reservation.service.dto.response.MyReservationsResponse;
import roomescape.reservation.service.dto.response.ReservationResponse;
import roomescape.reservation.service.dto.response.ReservationTimeWithBookedResponse;
import roomescape.reservation.service.dto.response.ReservationWithPaymentResponse;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "예약 관리")
@RequestMapping("/reservations")
@RestController
public class ReservationController {

    private final ReservationQueryService reservationQueryService;
    private final CreateReservationWithPaymentService createReservationWithPaymentService;
    private final DeleteReservationService deleteReservationService;

    public ReservationController(
            final ReservationQueryService reservationQueryService,
            final CreateReservationWithPaymentService createReservationWithPaymentService,
            final DeleteReservationService deleteReservationService
    ) {
        this.reservationQueryService = reservationQueryService;
        this.createReservationWithPaymentService = createReservationWithPaymentService;
        this.deleteReservationService = deleteReservationService;
    }

    @Operation(summary = "전체 예약 내역 조회")
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> readAllReservations() {
        List<ReservationResponse> response = reservationQueryService.getAll();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "예약 가능 시간 조회", description = "주어진 조건 내에서 생성 가능한 모든 예약 시간을 조회한다.")
    @GetMapping("/times")
    public ResponseEntity<List<ReservationTimeWithBookedResponse>> readAvailableReservationTimes(
            @RequestParam("date") final LocalDate date,
            @RequestParam("themeId") final Long themeId
    ) {
        List<ReservationTimeWithBookedResponse> responses = reservationQueryService.getReservationTimesWithBooked(date, themeId);

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "예약 및 결제 요청", description = "로그인 유저의 결제 정보로 결제를 진행하고, 원하는 예약을 생성한다.")
    @PostMapping
    public ResponseEntity<ReservationResponse> createWithPayment(
            @Valid @RequestBody ReservationWithPaymentRequest request,
            final LoginMember loginMember
    ) {
        ReservationWithPaymentResponse response = createReservationWithPaymentService.create(request, loginMember);

        return ResponseEntity.ok(ReservationResponse.from(response));
    }

    @Operation(summary = "예약 삭제", description = "로그인 유저가 생성한 예약을 삭제한다. 타인의 예약은 삭제할 수 없다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") final Long id, LoginMember loginMember) {
        deleteReservationService.delete(id, loginMember);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "예약 내역 조건 검색", description = "조건에 해당하는 모든 예약 내역을 조회한다.")
    @GetMapping("/filtering")
    public ResponseEntity<List<ReservationResponse>> findAllByFilter(
            @ModelAttribute @Valid final FilteringReservationRequest request
    ) {
        final List<ReservationResponse> reservationResponses = reservationQueryService.findReservationByFiltering(request);

        return ResponseEntity.ok(reservationResponses);
    }

    @Operation(summary = "내 예약 조회", description = "로그인 유저가 생성한 모든 예약 내역을 조회한다.")
    @GetMapping("/my")
    public ResponseEntity<List<MyReservationsResponse>> getMyReservations(@Valid LoginMember loginMember) {
        List<MyReservationsResponse> response = reservationQueryService.getAllLoginMemberReservations(loginMember);
        return ResponseEntity.ok(response);
    }
}
