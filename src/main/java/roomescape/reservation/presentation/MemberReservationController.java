package roomescape.reservation.presentation;

import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import roomescape.auth.login.presentation.dto.LoginMemberInfo;
import roomescape.auth.login.presentation.dto.annotation.LoginMember;
import roomescape.common.exception.handler.dto.ExceptionResponse;
import roomescape.member.presentation.dto.MyReservationResponse;
import roomescape.reservation.presentation.dto.ReservationRequest;
import roomescape.reservation.presentation.dto.ReservationResponse;
import roomescape.reservation.presentation.dto.WaitingResponse;
import roomescape.reservation.service.ReservationService;

@RestController
public class MemberReservationController {

    private final ReservationService reservationService;

    public MemberReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(
        @Valid @RequestBody final ReservationRequest request,
        @LoginMember final LoginMemberInfo memberInfo)
    {
        ReservationResponse response = reservationService.createReservation(request, memberInfo.id());
        return ResponseEntity.created(URI.create("/reservation")).body(response);
    }

    @PostMapping("/reservations/waitings")
    public ResponseEntity<WaitingResponse> createWaiting(
        @RequestBody final ReservationRequest request,
        @LoginMember final LoginMemberInfo memberInfo
    )
    {
        WaitingResponse response = reservationService.createWaiting(request, memberInfo.id());
        return ResponseEntity.created(URI.create("/reservations/waiting")).body(response);
    }

    @DeleteMapping("/reservations/waitings/{id}")
    public ResponseEntity<Void> deleteWaiting(@PathVariable("id") Long id) {
        reservationService.deleteWaiting(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reservations/me")
    public ResponseEntity<List<MyReservationResponse>> getMyReservations(@LoginMember LoginMemberInfo loginMemberInfo) {
        List<MyReservationResponse> response = reservationService.getMemberReservations(loginMemberInfo);

        return ResponseEntity.ok().body(response);
    }

    @ExceptionHandler(value = DateTimeParseException.class)
    public ResponseEntity<ExceptionResponse> noMatchDateType(final HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
            400, "[ERROR] 요청 날짜 형식이 맞지 않습니다.", request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> checkNull(final MethodArgumentNotValidException e, final HttpServletRequest request) {
        String message = Optional.ofNullable(e.getBindingResult().getFieldError())
            .map(FieldError::getDefaultMessage)
            .orElse("유효하지 않은 요청입니다.");

        ExceptionResponse exceptionResponse = new ExceptionResponse(
            400, message, request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(exceptionResponse);
    }
}
