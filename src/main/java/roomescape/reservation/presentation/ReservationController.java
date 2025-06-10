package roomescape.reservation.presentation;

import static roomescape.reservation.presentation.ReservationController.RESERVATION_BASE_URL;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.argumentResolver.Login;
import roomescape.common.exceptionHandler.dto.ExceptionResponse;
import roomescape.member.dto.request.LoginMember;
import roomescape.reservation.dto.request.ReservationConditionRequest;
import roomescape.reservation.dto.request.ReservationWithPaymentRequest;
import roomescape.reservation.dto.response.MyReservationWithPaymentResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.ReservationWithPaymentResponse;
import roomescape.reservation.service.ReservationPaymentFacade;
import roomescape.reservation.service.ReservationService;

@RestController
@RequestMapping(RESERVATION_BASE_URL)
public class ReservationController {

    public static final String RESERVATION_BASE_URL = "/reservations";
    private static final String SLASH = "/";

    private final ReservationService reservationService;
    private final ReservationPaymentFacade reservationPaymentFacade;

    public ReservationController(ReservationService reservationService, ReservationPaymentFacade reservationPaymentFacade) {
        this.reservationService = reservationService;
        this.reservationPaymentFacade = reservationPaymentFacade;
    }

    @GetMapping
    @Operation(summary = "전체 예약 조회 API")
    public ResponseEntity<List<ReservationResponse>> getReservations(
            @ModelAttribute ReservationConditionRequest request) {
        List<ReservationResponse> response = reservationService.getReservations(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "예약 추가 API")
    public ResponseEntity<ReservationWithPaymentResponse> createReservation(
            @RequestBody final ReservationWithPaymentRequest request,
            @Login final LoginMember loginMember
    ) {

        ReservationWithPaymentResponse response = reservationPaymentFacade.createReservationAndSavePayment(request, loginMember);

        URI locationUri = URI.create(RESERVATION_BASE_URL + SLASH + response.id());
        return ResponseEntity.created(locationUri).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "예약 삭제 API")
    public ResponseEntity<Void> deleteReservationById(@PathVariable("id") final Long id) {
        reservationService.deleteReservationById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(value = DateTimeParseException.class)
    public ResponseEntity<ExceptionResponse> noMatchDateType(final HttpServletRequest request) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                "[ERROR] 요청 날짜 형식이 맞지 않습니다.", request.getRequestURI()
        );
        return ResponseEntity.badRequest().body(exceptionResponse);
    }

    @GetMapping("/mine")
    @Operation(summary = "본인 예약 조회 API")
    public ResponseEntity<List<MyReservationWithPaymentResponse>> getMyReservations(@Login LoginMember loginMember) {
        List<MyReservationWithPaymentResponse> myReservationWithPaymentResponse = reservationService.getMyReservations(loginMember.id());
        return ResponseEntity.ok().body(myReservationWithPaymentResponse);
    }
}
