package roomescape.controller.reservation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.client.PaymentClient;
import roomescape.controller.auth.AuthenticationPrincipal;
import roomescape.domain.reservation.Reservation;
import roomescape.dto.MemberResponse;
import roomescape.dto.auth.LoginMember;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.payment.PaymentResponse;
import roomescape.dto.reservation.AutoReservedFilter;
import roomescape.dto.reservation.MemberReservationSaveRequest;
import roomescape.dto.reservation.MyReservationResponse;
import roomescape.dto.reservation.MyReservationsResponse;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.dto.reservation.ReservationSaveRequest;
import roomescape.dto.reservation.ReservationTimeResponse;
import roomescape.dto.theme.ThemeResponse;
import roomescape.service.AutoReserveService;
import roomescape.service.MemberService;
import roomescape.service.ReservationService;
import roomescape.service.ReservationTimeService;
import roomescape.service.ThemeService;
import roomescape.service.WaitingService;

import java.util.List;

@Tag(name = "예약 API(공용)")
@RequestMapping("/reservations")
@RestController
public class ReservationController {

    private final PaymentClient paymentClient;
    private final MemberService memberService;
    private final ReservationService reservationService;
    private final WaitingService waitingService;
    private final ReservationTimeService reservationTimeService;
    private final ThemeService themeService;
    private final AutoReserveService autoReserveService;

    public ReservationController(final PaymentClient paymentClient,
                                 final MemberService memberService,
                                 final ReservationService reservationService,
                                 final WaitingService waitingService,
                                 final ReservationTimeService reservationTimeService,
                                 final ThemeService themeService,
                                 final AutoReserveService autoReserveService) {
        this.paymentClient = paymentClient;
        this.memberService = memberService;
        this.reservationService = reservationService;
        this.waitingService = waitingService;
        this.reservationTimeService = reservationTimeService;
        this.themeService = themeService;
        this.autoReserveService = autoReserveService;
    }

    @Operation(summary = "예약생성 및 결제",description = "예약생성과 결제승인을 진행합니다.")
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(@AuthenticationPrincipal final LoginMember loginMember,
                                                                 @RequestBody final MemberReservationSaveRequest request) {
        final MemberResponse memberResponse = memberService.findById(loginMember.id());
        final ReservationSaveRequest saveRequest = request.generateReservationSaveRequest(memberResponse);

        final ReservationTimeResponse reservationTimeResponse = reservationTimeService.findById(request.timeId());
        final ThemeResponse themeResponse = themeService.findById(request.themeId());

        final PaymentRequest paymentRequest = request.toPaymentRequest();
        final PaymentResponse paymentResponse = paymentClient.pay(paymentRequest);

        final Reservation reservation = saveRequest.toReservation(memberResponse, themeResponse, reservationTimeResponse, paymentResponse);

        final ReservationResponse response = reservationService.create(reservation);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "예약결제",description = "미결제 예약에 대한 결제승인을 진행합니다.")
    @PatchMapping("/{id}")
    public ResponseEntity<MyReservationResponse> payReservation(@AuthenticationPrincipal final LoginMember loginMember,
                                                                @PathVariable final Long id,
                                                                @RequestBody final PaymentRequest request) {
        reservationService.checkMyReservation(id, loginMember);
        PaymentResponse paymentResponse = paymentClient.pay(request);
        MyReservationResponse reservationResponse = reservationService.updatePayment(id, paymentResponse);
        return ResponseEntity.ok(reservationResponse);
    }

    @Operation(summary = "예약 목록")
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findReservations() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @Operation(summary = "예약 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable final Long id) {
        final ReservationResponse response = reservationService.delete(id);
        final AutoReservedFilter filter = AutoReservedFilter.from(response);
        autoReserveService.reserveWaiting(filter);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "내 예약 목록",description = "주어진 토큰 정보로 자신의 예약 목록을 불러옵니다.")
    @GetMapping("/mine")
    public ResponseEntity<List<MyReservationResponse>> findMyReservations(@AuthenticationPrincipal final LoginMember loginMember) {
        final List<MyReservationResponse> myReservations = reservationService.findMyReservations(loginMember.id());
        final List<MyReservationResponse> myWaitings = waitingService.findMyWaitings(loginMember.id());
        final List<MyReservationResponse> responses = MyReservationsResponse.combine(myReservations, myWaitings);
        return ResponseEntity.ok(responses);
    }
}
