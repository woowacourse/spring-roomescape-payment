package roomescape.reservation.presentation;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.annotation.LoginMember;
import roomescape.auth.dto.info.LoginMemberInfo;
import roomescape.reservation.application.ReservationService;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.request.ReservationWithPaymentRequest;
import roomescape.reservation.dto.response.ReservationMineResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.WaitingResponse;

@Slf4j
@RestController
@RequestMapping("/reservations")
public class MemberReservationController {

    private final ReservationService reservationService;

    public MemberReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody final ReservationWithPaymentRequest request,
            @LoginMember final LoginMemberInfo memberInfo) {

        log.info("예약 요청 수신: memberId={}, themeId={}, date={}, timeId={}",
                memberInfo.id(), request.themeId(), request.date(), request.timeId());
        ReservationResponse response = reservationService.createReservationWithPayment(request, memberInfo.id());

        log.info("예약 생성 완료: reservationId={}", response.id());
        return ResponseEntity.created(URI.create("/reservation")).body(response);
    }

    @PostMapping("/waitings")
    public ResponseEntity<WaitingResponse> createWaiting(
            @RequestBody final ReservationRequest request,
            @LoginMember final LoginMemberInfo memberInfo) {
        log.info("예약 대기 요청 수신: memberId={}, themeId={}, date={}, timeId={}",
                memberInfo.id(), request.themeId(), request.date(), request.timeId());
        WaitingResponse response = reservationService.createWaiting(request, memberInfo.id());

        log.info("예약 대기 생성 완료: waitingId={}", response.id());
        return ResponseEntity.created(URI.create("/reservation")).body(response);
    }

    @DeleteMapping("/waitings/{id}")
    public ResponseEntity<Void> deleteWaiting(@PathVariable("id") final Long id) {
        log.info("예약 대기 삭제 요청 수신: waitingId={}", id);
        reservationService.deleteWaiting(id);

        log.info("예약 대기 삭제 완료: waitingId={}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<List<ReservationMineResponse>> getMyReservations(
            @LoginMember final LoginMemberInfo loginMemberInfo) {
        log.info("나의 예약 목록 조회 요청: memberId={}", loginMemberInfo.id());
        List<ReservationMineResponse> response = reservationService.getMemberReservations(loginMemberInfo);

        log.debug("조회된 예약/대기 수: {}", response.size());
        return ResponseEntity.ok().body(response);
    }
}
