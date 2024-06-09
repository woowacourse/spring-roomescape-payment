package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.controller.request.MemberLoginRequest;
import roomescape.exception.AuthenticationException;
import roomescape.exception.NotFoundException;
import roomescape.model.Member;
import roomescape.repository.MemberRepository;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class MemberReadService {

    private final MemberRepository memberRepository;

    public MemberReadService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member getMemberByEmailAndPassword(MemberLoginRequest request) {
        return memberRepository.findByEmailAndPassword(request.email(), request.password())
                .orElseThrow(() -> new AuthenticationException(
                        "사용자(email: %s, password: %s)가 존재하지 않습니다.".formatted(request.email(), request.password())));
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("id가 %s인 사용자가 존재하지 않습니다."));
    }

    public List<Member> findAllMembers() {
        return memberRepository.findAll();
    }
}
