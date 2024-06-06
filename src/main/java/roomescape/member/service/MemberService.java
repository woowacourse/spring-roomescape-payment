package roomescape.member.service;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.domain.Member;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.member.dto.MemberResponse;
import roomescape.member.dto.MembersResponse;
import roomescape.system.exception.ErrorType;
import roomescape.system.exception.RoomEscapeException;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public MembersResponse findAllMembers() {
        List<MemberResponse> response = memberRepository.findAll().stream()
                .map(MemberResponse::fromEntity)
                .toList();

        return new MembersResponse(response);
    }

    @Transactional(readOnly = true)
    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new RoomEscapeException(ErrorType.MEMBER_NOT_FOUND,
                        String.format("[memberId: %d]", memberId), HttpStatus.BAD_REQUEST));
    }

    @Transactional(readOnly = true)
    public Member findMemberByEmailAndPassword(String email, String password) {
        return memberRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new RoomEscapeException(ErrorType.MEMBER_NOT_FOUND,
                        String.format("[email: %s, password: %s]", email, password), HttpStatus.BAD_REQUEST));
    }
}
