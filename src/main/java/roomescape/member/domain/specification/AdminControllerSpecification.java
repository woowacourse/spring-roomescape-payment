package roomescape.member.domain.specification;

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import roomescape.auth.controller.dto.MemberResponse;
import roomescape.auth.domain.AuthInfo;
import roomescape.exception.ErrorType;
import roomescape.exception.ErrorTypeGroup;
import roomescape.global.annotation.ApiErrorResponses;
import roomescape.reservation.controller.dto.MemberReservationRequest;
import roomescape.reservation.controller.dto.ReservationResponse;

import java.util.List;

public interface AdminControllerSpecification {

    @ApiErrorResponses(value = ErrorType.INVALID_REQUEST_ERROR, groups = ErrorTypeGroup.ADMIN)
    ReservationResponse create(@Valid MemberReservationRequest memberReservationRequest);

    @ApiResponse(description = "삭제를 성공했습니다.", responseCode = "204")
    @ApiErrorResponses(value = ErrorType.RESERVATION_NOT_DELETED, groups = ErrorTypeGroup.ADMIN)
    void delete(@Min(1) long reservationId);

    @ApiErrorResponses(groups = ErrorTypeGroup.ADMIN)
    List<MemberResponse> findAll();

    @ApiErrorResponses(groups = {ErrorTypeGroup.ADMIN, ErrorTypeGroup.WAITING_RESERVATION})
    void approve(AuthInfo authInfo, @Min(1) long memberReservationId);

    @ApiErrorResponses(groups = {ErrorTypeGroup.ADMIN, ErrorTypeGroup.WAITING_RESERVATION})
    void deny(AuthInfo authInfo, @Min(1) long memberReservationId);
}

