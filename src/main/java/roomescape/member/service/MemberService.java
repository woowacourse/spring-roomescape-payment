package roomescape.member.service;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.stereotype.Service;
import roomescape.exception.RoomEscapeException;
import roomescape.exception.model.MemberExceptionCode;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.member.dto.MemberLoginCheckResponse;
import roomescape.member.dto.MemberResponse;
import roomescape.member.repository.MemberRepository;

@Tag(name = "멤버 서비스", description = "멤버 로그인 정보를 찾거나 멤버의 id로 역할을 찾는 등 멤버와 관련된 로직을 수행한다.")
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberLoginCheckResponse findLoginMemberInfo(long id) {
        Member member = memberRepository.findMemberById(id)
                .orElseThrow(() -> new RoomEscapeException(MemberExceptionCode.ID_AND_PASSWORD_NOT_MATCH_OR_EXIST));

        return new MemberLoginCheckResponse(member.getName());
    }

    public List<MemberResponse> findMembersId() {
        List<Member> members = memberRepository.findAll();

        return members.stream()
                .map(member -> new MemberResponse(member.getId()))
                .toList();
    }

    public MemberRole findMemberRole(long id) {
        Member member = memberRepository.findMemberById(id)
                .orElseThrow(() -> new RoomEscapeException(MemberExceptionCode.MEMBER_ROLE_NOT_EXIST_EXCEPTION));

        return MemberRole.findMemberRole(member.getRole().name());
    }
}
