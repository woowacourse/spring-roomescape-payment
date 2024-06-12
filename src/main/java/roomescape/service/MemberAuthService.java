package roomescape.service;

import org.springframework.stereotype.Service;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberEmail;
import roomescape.domain.member.MemberName;
import roomescape.domain.member.MemberPassword;
import roomescape.domain.repository.MemberRepository;
import roomescape.service.request.MemberSignUpDto;
import roomescape.service.response.MemberDto;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MemberAuthService {
    private final MemberRepository memberRepository;

    public MemberAuthService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberDto signUp(MemberSignUpDto request) {
        if (memberRepository.existsByEmail(new MemberEmail(request.email()))) {
            throw new IllegalStateException("해당 이메일의 회원이 이미 존재합니다.");
        }

        Member newMember = Member.createUser(
                new MemberName(request.name()),
                new MemberEmail(request.email()),
                new MemberPassword(request.password()));

        Member savedMember = memberRepository.save(newMember);
        return new MemberDto(savedMember.getId(), savedMember.getName().getName(),
                savedMember.getRole().name());
    }

    public MemberDto findMemberByEmail(String email) {
        return memberRepository.findByEmail(new MemberEmail(email))
                .map(member -> new MemberDto(member.getId(), member.getName().getName(),
                        member.getRole().name()))
                .orElseThrow(() -> new NoSuchElementException("회원 정보를 찾지 못했습니다. 다시 로그인 해주세요."));
    }

    public List<MemberDto> findAll() {
        return memberRepository.findAll().stream()
                .map(member -> new MemberDto(member.getId(), member.getName().getName(),
                        member.getRole().name()))
                .toList();
    }

    public boolean isExistsMemberByEmailAndPassword(String email, String password) {
        if (memberRepository.existsByEmailAndPassword(new MemberEmail(email), new MemberPassword(password))) {
            return true;
        }
        throw new IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다.");
    }
}
