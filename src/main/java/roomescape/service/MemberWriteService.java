package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.controller.request.RegisterRequest;
import roomescape.model.Member;
import roomescape.model.Role;
import roomescape.repository.MemberRepository;

@Transactional
@Service
public class MemberWriteService {

    private final MemberRepository memberRepository;

    public MemberWriteService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member register(RegisterRequest request) {
        return memberRepository.save(new Member(request.name(), Role.MEMBER, request.email(), request.password()));
    }
}
