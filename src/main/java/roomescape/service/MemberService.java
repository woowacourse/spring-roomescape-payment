package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.exception.AuthenticationException;
import roomescape.exception.NotFoundException;
import roomescape.model.Member;
import roomescape.model.Role;
import roomescape.repository.MemberRepository;
import roomescape.request.MemberLoginRequest;
import roomescape.request.RegisterRequest;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member findMemberByEmailAndPassword(final MemberLoginRequest request) {
        return memberRepository.findByEmailAndPassword(request.email(), request.password())
                .orElseThrow(() -> new AuthenticationException(
                        "사용자(email: %s, password: %s)가 존재하지 않습니다.".formatted(request.email(), request.password())));
    }

    public String findMemberNameById(final Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("id가 %s인 사용자가 존재하지 않습니다.".formatted(id)));
        return member.getName();
    }

    public Member findMemberById(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("id가 %s인 사용자가 존재하지 않습니다."));
    }

    public List<Member> findAllMembers() {
        return memberRepository.findAll();
    }

    public Member register(final RegisterRequest request) {
        return memberRepository.save(new Member(request.name(), Role.MEMBER, request.email(), request.password()));
    }
}
