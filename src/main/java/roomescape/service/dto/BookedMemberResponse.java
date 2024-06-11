package roomescape.service.dto;

import roomescape.domain.reservation.BookedMember;

public record BookedMemberResponse(
        Long reservationId,
        Long memberId
) {
    public static BookedMemberResponse from(BookedMember bookedMember) {
        return new BookedMemberResponse(bookedMember.getReservation().getId(), bookedMember.getMember().getId());
    }
}
