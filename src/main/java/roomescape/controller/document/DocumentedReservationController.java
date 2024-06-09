package roomescape.controller.document;

import static roomescape.exception.ExceptionType.DUPLICATE_RESERVATION;
import static roomescape.exception.ExceptionType.NOT_FOUND_MEMBER;
import static roomescape.exception.ExceptionType.NOT_FOUND_RESERVATION;
import static roomescape.exception.ExceptionType.NOT_FOUND_RESERVATION_TIME;
import static roomescape.exception.ExceptionType.NOT_FOUND_THEME;
import static roomescape.exception.ExceptionType.PAST_TIME_RESERVATION;
import static roomescape.exception.ExceptionType.PERMISSION_DENIED;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import roomescape.annotation.ApiSuccessResponse;
import roomescape.annotation.Auth;
import roomescape.annotation.ErrorApiResponse;
import roomescape.dto.LoginMemberReservationResponse;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;

public interface DocumentedReservationController {

    @Operation(summary = "예약 생성", description = "회원이 자신의 예약을 생성할 때 사용하는 API")
    @ErrorApiResponse({NOT_FOUND_RESERVATION_TIME, NOT_FOUND_THEME, NOT_FOUND_MEMBER, DUPLICATE_RESERVATION,
            PAST_TIME_RESERVATION})
    @ApiSuccessResponse(status = HttpStatus.CREATED, bodyType = ReservationResponse.class, body = """
            {
              "id": 1,
              "name": "예약자 이름",
              "date": "2024-06-08",
              "time": {
                "id": 1,
                "startAt": "19:30"
              },
              "theme": {
                "id": 1,
                "name": "테마 이름",
                "description": "테마 설명",
                "thumbnail": "https://thumbnail"
              }
            }
            """)
    ResponseEntity<ReservationResponse> saveReservation(@Auth long memberId,
                                                        @RequestBody ReservationRequest reservationRequest);

    @Operation(summary = "전체 예약 조회", description = "전체 예약을 조회할 때 사용하는 API")
    @ApiSuccessResponse(bodyType = ReservationResponse.class, body = """
            [
                {
                  "id": 1,
                  "name": "예약자 이름",
                  "date": "2024-06-08",
                  "time": {
                    "id": 1,
                    "startAt": "19:30"
                  },
                  "theme": {
                    "id": 1,
                    "name": "테마 이름",
                    "description": "테마 설명",
                    "thumbnail": "https://thumbnail"
                  }
                },
                {
                  "id": 2,
                  "name": "예약자 이름2",
                  "date": "2024-06-09",
                  "time": {
                    "id": 1,
                    "startAt": "19:30"
                  },
                  "theme": {
                    "id": 1,
                    "name": "테마 이름",
                    "description": "테마 설명",
                    "thumbnail": "https://thumbnail"
                  }
                }
            ]
            """)
    List<ReservationResponse> findAllReservations();

    @Operation(summary = "회원 예약 목록 조회", description = "회원이 자신의 예약 목록을 조회할 때 사용하는 API")
    @ApiSuccessResponse(bodyType = LoginMemberReservationResponse.class, body = """
            [
                {
                  "reservationId": 1,
                  "theme": "테마 이름 1",
                  "date": "2024-06-08",
                  "time": "19:30",
                  "status": "WAITING_PAYMENT",
                  "paymentKey": "paymentKey_d363d9c5de7a",
                  "amount": 2000
                },
                {
                  "reservationId": 2,
                  "theme": "테마 이름 2",
                  "date": "2024-06-09",
                  "time": "19:30",
                  "status": "SUCCESS",
                  "paymentKey": "paymentKey_d363d9c5de7a",
                  "amount": 1000
                }
            ]
            """)
    List<LoginMemberReservationResponse> findLoginMemberReservations(@Auth long memberId);

    @Operation(summary = "예약 취소", description = "예약을 취소할 때 사용하는 API")
    @ErrorApiResponse({PERMISSION_DENIED, NOT_FOUND_RESERVATION, NOT_FOUND_MEMBER})
    @ApiSuccessResponse(status = HttpStatus.NO_CONTENT)
    ResponseEntity<Void> delete(@Auth long memberId, @PathVariable("id") long reservationId);
}
