package roomescape.member.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.global.exception.custom.BadRequestException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberEmail;
import roomescape.member.domain.MemberName;
import roomescape.member.domain.Password;
import roomescape.member.dto.MemberResponse;
import roomescape.member.dto.SignupRequest;
import roomescape.member.repository.MemberRepository;

@Service
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberResponse createMember(final SignupRequest member) {
        log.info("회원 가입 요청 - email: {}", member.email());
        final MemberName name = new MemberName(member.name());
        if (memberRepository.existsByName(name)) {
            throw new BadRequestException("이미 사용중인 이름입니다.");
        }
        final MemberEmail email = new MemberEmail(member.email());
        if (memberRepository.existsByEmail(email)) {
            throw new BadRequestException("이미 사용중인 이메일입니다.");
        }
        final Password password = new Password(member.password());
        final Member savedMember = memberRepository.save(Member.register(name, email, password));
        log.info("회원 가입 완료 - memberId: {}, email: {}", savedMember.getId(), savedMember.getEmail().getValue());
        return new MemberResponse(savedMember);
    }

    public List<MemberResponse> findAll() {
        final List<Member> members = memberRepository.findAll();
        return members.stream()
                .map(MemberResponse::new)
                .toList();
    }
}
