package roomescape.reservation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import roomescape.auth.dto.LoginMember;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.entity.MemberReservation;
import roomescape.reservation.domain.entity.Reservation;
import roomescape.reservation.domain.entity.ReservationStatus;

import java.time.LocalDate;

@Schema(description = "예약 생성 요청 DTO")
public record ReservationCreateRequest(
        @Schema(description = "사용자 pk", example = "1") @NotNull(message = "사용자는 비어있을 수 없습니다.")
        Long memberId,
        @Schema(description = "예약 날짜", example = "2024-06-29") @NotNull(message = "예약 날짜는 비어있을 수 없습니다.")
        LocalDate date,
        @Schema(description = "예약 시간 pk", example = "1") @NotNull(message = "예약 시간은 비어있을 수 없습니다.")
        Long timeId,
        @Schema(description = "테마 pk", example = "1") @NotNull(message = "테마는 비어있을 수 없습니다.")
        Long themeId,
        @Schema(description = "예약 상태", example = "CONFIRMATION") @NotNull(message = "예약 상태는 비어있을 수 없습니다.")
        ReservationStatus status
) {

    public ReservationCreateRequest(Long memberId, LocalDate date, Long timeId, Long themeId) {
        this(memberId, date, timeId, themeId, ReservationStatus.CONFIRMATION);
    }

    public static ReservationCreateRequest of(MemberReservationCreateRequest request, LoginMember loginMember) {
        return new ReservationCreateRequest(loginMember.id(), request.date(), request.timeId(), request.themeId(), request.status());
    }

    public MemberReservation toMemberReservation(Member member, Reservation reservation) {
        return new MemberReservation(member, reservation, status);
    }
}
