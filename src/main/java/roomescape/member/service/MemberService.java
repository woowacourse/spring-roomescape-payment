package roomescape.member.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.global.auth.dto.UserInfo;
import roomescape.global.auth.service.MyPasswordEncoder;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.member.dto.request.SignupRequest;
import roomescape.member.dto.response.MemberResponse;
import roomescape.member.dto.response.SignUpResponse;
import roomescape.member.exception.MemberDuplicatedException;
import roomescape.member.exception.MemberNotFoundException;
import roomescape.member.repository.MemberRepository;

@Slf4j
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final MyPasswordEncoder myPasswordEncoder;

    public MemberService(final MemberRepository memberRepository, final MyPasswordEncoder myPasswordEncoder) {
        this.memberRepository = memberRepository;
        this.myPasswordEncoder = myPasswordEncoder;
    }

    public SignUpResponse signup(final SignupRequest signupRequest) {
        String encodedPassword = myPasswordEncoder.encode(signupRequest.password());
        Member member = new Member(signupRequest.name(), signupRequest.email(), encodedPassword, MemberRole.USER);
        if (memberRepository.existsByEmail(signupRequest.email())) {
            log.info("이미 가입된 이메일로 인한 회원가입 실패 email = {}", signupRequest.email());
            throw new MemberDuplicatedException("이미 존재하는 회원입니다.");
        }
        return SignUpResponse.from(memberRepository.save(member));
    }

    public List<MemberResponse> findAllUsers() {
        return memberRepository.findByMemberRole(MemberRole.USER).stream()
                .map(member -> new MemberResponse(member.getId(), member.getName()))
                .toList();
    }

    public Member getMember(final UserInfo userInfo) {
        return memberRepository.findById(userInfo.id())
                .orElseThrow(() -> {
                    log.info("존재하지 않은 회원 아이디 id = {}", userInfo.id());
                    return new MemberNotFoundException("존재하지 않은 멤버입니다.");
                });
    }

    public Member findUserByMemberId(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.info("존재하지 않은 회원 아이디 id = {}", memberId);
                    return new MemberNotFoundException("멤버를 찾을 수 없습니다.");
                });
    }
}
