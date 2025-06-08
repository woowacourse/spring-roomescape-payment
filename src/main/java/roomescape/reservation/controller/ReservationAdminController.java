package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import roomescape.member.controller.dto.SignupRequest;
import roomescape.reservation.controller.dto.CreateReservationByAdminWebRequest;
import roomescape.reservation.controller.dto.ReservationSearchWebRequest;
import roomescape.reservation.controller.dto.ReservationWaitWebResponse;
import roomescape.reservation.controller.dto.ReservationWebResponse;

@Tag(name = "예약 API [관리자 권한]")
public interface ReservationAdminController {

    @Operation(summary = "예약 목록 조회", security = @SecurityRequirement(name = "loginAuth"))
    List<ReservationWebResponse> getAll();

    @Operation(summary = "예약 대기 목록 조회", security = @SecurityRequirement(name = "loginAuth"))
    List<ReservationWaitWebResponse> getAllReservationWait();

    @Operation(summary = "예약 검색", security = @SecurityRequirement(name = "loginAuth"))
    ResponseEntity<List<ReservationWebResponse>> getReservationsByAdmin(
            ReservationSearchWebRequest reservationSearchWebRequest
    );

    @Operation(summary = "예약 생성", security = @SecurityRequirement(name = "loginAuth"))
    @RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(implementation = CreateReservationByAdminWebRequest.class),
                    examples = {
                            @ExampleObject(name = "sample",
                                    value = "{\"memberId\":\"1\",\"date\":\"2025-09-20\",\"timeId\":\"1\",\"themeId\":\"1\"}")
                    }
            )
    )
    ResponseEntity<ReservationWebResponse> createReservationByAdmin(
            CreateReservationByAdminWebRequest createReservationByAdminWebRequest
    );
}
