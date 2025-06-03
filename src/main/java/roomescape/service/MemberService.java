package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.Member;
import roomescape.domain.repository.MemberRepository;
import roomescape.dto.request.MemberRequest;
import roomescape.dto.response.MemberResponse;
import roomescape.exception.DuplicatedEmailException;

import java.util.List;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> findAllMembers() {
        return memberRepository.findAll()
                .stream()
                .map(MemberResponse::from)
                .toList();
    }

    public MemberResponse createMember(MemberRequest memberRequest) {
        boolean existsMember = memberRepository.existsByEmail(memberRequest.email());
        if (existsMember) {
            throw new DuplicatedEmailException();
        }
        Member savedMember = memberRepository.save(memberRequest.toMember());
        return MemberResponse.from(savedMember);
    }
}
