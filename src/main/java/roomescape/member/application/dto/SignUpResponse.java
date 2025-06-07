package roomescape.member.application.dto;

public class SignUpResponse {
    private final Long id;

    public SignUpResponse(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
