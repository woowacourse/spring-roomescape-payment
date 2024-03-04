package roomescape.member;

public class LoginMember {
    public Long memberId;

    public LoginMember(Long memberId) {
        this.memberId = memberId;
    }

    public Long getMemberId() {
        return memberId;
    }
}
