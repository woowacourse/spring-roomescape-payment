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
import roomescape.reservation.service.CreateReservationService;
import roomescape.reservation.service.ReservationService;
import roomescape.reservation.service.dto.request.FilteringReservationRequest;
import roomescape.reservation.service.dto.request.ReservationWithPaymentRequest;
import roomescape.reservation.service.dto.response.MyReservationsResponse;
import roomescape.reservation.service.dto.response.ReservationResponse;
import roomescape.reservation.service.dto.response.ReservationTimeWithBookedResponse;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/reservations")
@RestController
@Tag(name = "예약 컨트롤러", description = "예약에 관한 API 모음")
public class ReservationController {

    private final ReservationService reservationService;
    private final CreateReservationService createReservationService;

    public ReservationController(
            final ReservationService reservationService,
            final CreateReservationService createReservationService
    ) {
        this.reservationService = reservationService;
        this.createReservationService = createReservationService;
    }

    @GetMapping
    @Operation(summary = "예약 전체 조회", description = "전체 예약 목록을 조회합니다.")
    public ResponseEntity<List<ReservationResponse>> readAllReservations() {
        List<ReservationResponse> response = reservationService.getAll();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/times")
    @Operation(summary = "예약 가능 시간 조회", description = "선택날짜, 테마에 따른 예약 가능한 시간을 조회합니다.")
    public ResponseEntity<List<ReservationTimeWithBookedResponse>> readAvailableReservationTimes(
            @RequestParam("date") final LocalDate date,
            @RequestParam("themeId") final Long themeId
    ) {
        List<ReservationTimeWithBookedResponse> responses = reservationService.getReservationTimesWithBooked(date, themeId);

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/filtering")
    @Operation(summary = "예약 목록 필터링 조회", description = "선택한 테마, 멤버, 시작 날짜 ~ 종료 날짜에 따른 예약 목록을 조회합니다.")
    public ResponseEntity<List<ReservationResponse>> findAllByFilter(
            @ModelAttribute @Valid final FilteringReservationRequest request
    ) {
        final List<ReservationResponse> reservationResponses =
                reservationService.findReservationByFiltering(request);

        return ResponseEntity.ok(reservationResponses);
    }

    @GetMapping("/my")
    @Operation(summary = "나의 예약 목록 조회", description = "로그인한 멤버의 전체 예약 목록을 가져옵니다.")
    public ResponseEntity<List<MyReservationsResponse>> getMyReservations(@Valid LoginMember loginMember) {
        List<MyReservationsResponse> response = reservationService.getAllLoginMemberReservations(loginMember);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "결제를 포함한 예약 생성", description = "예약 및 결제를 생성합니다. 선 예약 후 결제를 진행합니다.")
    public ResponseEntity<ReservationResponse> createWithPayment(
            @Valid @RequestBody ReservationWithPaymentRequest request,
            final LoginMember loginMember
    ) {
        ReservationResponse reservationResponse = createReservationService.createWithPayment(request, loginMember);
        return ResponseEntity.ok(reservationResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "예약 삭제", description = "어드민 권한을 가진 사람이라면 선택한 예약을 삭제합니다. ")
    public ResponseEntity<Void> delete(@PathVariable("id") final Long id, LoginMember loginMember) {
        reservationService.delete(id, loginMember);

        return ResponseEntity.noContent().build();
    }
}
