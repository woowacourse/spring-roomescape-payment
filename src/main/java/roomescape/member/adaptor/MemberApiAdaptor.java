package roomescape.member.adaptor;

import org.springframework.stereotype.Component;
import roomescape.member.docs.*;
import roomescape.member.dto.request.LoginMember;
import roomescape.member.dto.request.LoginRequest;
import roomescape.member.dto.request.SignupRequest;
import roomescape.member.dto.response.LoginCheckResponse;
import roomescape.member.dto.response.MemberResponse;
import roomescape.member.dto.response.ReservationMemberResponse;
import roomescape.member.dto.response.SignupResponse;

@Component
public class MemberApiAdaptor {

    public SignupRequest toSignupRequest(SignupRequestDocs dto) {
        return dto.toSignupRequest();
    }

    public LoginRequest toLoginRequest(LoginRequestDocs dto) {
        return dto.toLoginRequest();
    }

    public LoginMember toLoginMember(LoginMemberDocs dto) {
        return dto.toLoginMember();
    }

    public SignupResponseDocs toSignupResponseDocs(SignupResponse response) {
        return SignupResponseDocs.from(response);
    }

    public MemberResponseDocs toMemberResponseDocs(MemberResponse response) {
        return MemberResponseDocs.from(response);
    }

    public LoginCheckResponseDocs toLoginCheckResponseDocs(LoginCheckResponse response) {
        return LoginCheckResponseDocs.from(response);
    }

    public ReservationMemberResponseDocs toReservationMemberResponseDocs(ReservationMemberResponse response) {
        return ReservationMemberResponseDocs.from(response);
    }
} 