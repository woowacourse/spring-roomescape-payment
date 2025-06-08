package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.annotation.RequiredAdmin;
import roomescape.auth.service.AuthService;
import roomescape.auth.service.dto.LoginMember;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.service.CreateReservationService;
import roomescape.reservation.service.dto.request.AdminReservationRequest;
import roomescape.reservation.service.dto.request.ReservationCreateRequest;
import roomescape.reservation.service.dto.response.ReservationResponse;

import java.net.URI;

@RequestMapping("/admin/reservations")
@RestController
@Tag(name = "관리자 예약 컨트롤러", description = "관리자 예약 관련 API 모음")
public class AdminReservationController {

    private static final Logger log = LoggerFactory.getLogger(AdminReservationController.class);

    private final AuthService authService;
    private final CreateReservationService createReservationService;

    public AdminReservationController(
            final AuthService authService,
            final CreateReservationService createReservationService
    ) {
        this.authService = authService;
        this.createReservationService = createReservationService;
    }

    @RequiredAdmin
    @PostMapping
    @Operation(summary = "관리자 예약 생성",description = "결제 없이 예약이 이루어 집니다.")
    public ResponseEntity<ReservationResponse> create(
            @Valid @RequestBody final AdminReservationRequest request
    ) {
        log.info(
                "[POST /AdminReservationCreate.Request] memberId={},date={},timeId={},themeId={}",
                request.memberId(),
                request.date(),
                request.timeId(),
                request.themeId()
                );

        LoginMember member = authService.findLoginMemberById(request.memberId());
        ReservationCreateRequest reservationCreateRequest =
                new ReservationCreateRequest(request.date(), request.timeId(), request.themeId(), member);
        Reservation reservation = createReservationService.create(reservationCreateRequest);
        ReservationResponse response = ReservationResponse.fromWithoutPayment(reservation);

        log.info("[POST / AdminReservationCreate.Response] [SUCCESS] id={}, member={}, date={}, time={}, theme={}, payment={}",
                response.id(),
                response.member(),
                response.date(),
                response.time(),
                response.theme(),
                response.paymentResponse()
                );

        return ResponseEntity.created(URI.create("/reservations/" + response.id()))
                .body(response);

    }
}
