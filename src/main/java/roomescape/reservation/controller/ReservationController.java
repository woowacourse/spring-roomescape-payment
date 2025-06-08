package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import roomescape.member.auth.vo.MemberInfo;
import roomescape.reservation.controller.dto.AvailableReservationTimeWebResponse;
import roomescape.reservation.controller.dto.CreateReservationWebRequest;
import roomescape.reservation.controller.dto.CreateReservationWithPaymentWebRequest;
import roomescape.reservation.controller.dto.ReservationWaitWebResponse;
import roomescape.reservation.controller.dto.ReservationWebResponse;
import roomescape.reservation.controller.dto.ReservationWithStatusResponse;

public interface ReservationController {

    @Tag(name = "예약 API")
    @Operation(summary = "내 예약 목록 조회", security = @SecurityRequirement(name = "loginAuth"))
    List<ReservationWithStatusResponse> getAllWithReservationWait(@Parameter(hidden = true) MemberInfo memberInfo);

    @Tag(name = "예약 API")
    @Operation(summary = "예약 가능 여부 및 시간 목록 조회", security = @SecurityRequirement(name = "loginAuth"))
    List<AvailableReservationTimeWebResponse> getAvailable(
            @Parameter(description = "yyyy-MM-dd") LocalDate date,
            Long themeId
    );

    @Tag(name = "예약 API")
    @Operation(summary = "예약 생성 및 결제", security = @SecurityRequirement(name = "loginAuth"))
    ResponseEntity<ReservationWebResponse> create(
            @RequestBody(required = true) CreateReservationWithPaymentWebRequest request,
            @Parameter(hidden = true) MemberInfo memberInfo
    );

    @Tag(name = "예약 API")
    @Operation(summary = "예약 삭제", security = @SecurityRequirement(name = "loginAuth"))
    ResponseEntity<Void> delete(Long id);

    @Tag(name = "대기 API")
    @Operation(summary = "예약 대기 생성", security = @SecurityRequirement(name = "loginAuth"))
    @RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(implementation = CreateReservationWebRequest.class),
                    examples = {
                            @ExampleObject(name = "sample",
                                    value = "{\"date\":\"2025-09-20\",\"timeId\":\"1\",\"themeId\":\"1\"}")
                    }
            )
    )
    ResponseEntity<ReservationWaitWebResponse> createReservationWait(
            CreateReservationWebRequest createReservationWebRequest,
            @Parameter(hidden = true) MemberInfo memberInfo
    );

    @Tag(name = "대기 API")
    @Operation(summary = "예약 대기 삭제", security = @SecurityRequirement(name = "loginAuth"))
    ResponseEntity<Void> deleteReservationWait(Long id);
}
