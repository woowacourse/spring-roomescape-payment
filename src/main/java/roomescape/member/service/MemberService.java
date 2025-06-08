package roomescape.member.service;

import static roomescape.global.exception.ErrorMessage.INTERNAL_SERVER_ERROR;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.global.config.Performance;
import roomescape.member.domain.Member;
import roomescape.member.dto.MemberRequest;
import roomescape.member.dto.MemberResponse;
import roomescape.member.dto.MemberResponses;
import roomescape.member.exception.MemberNotFoundException;
import roomescape.member.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void save(final MemberRequest memberRequest) {
        if (memberRepository.existsByEmail(memberRequest.email())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다. email=" + memberRequest.email());
        }
        memberRepository.save(
                Member.withDefaultRole(memberRequest.name(), memberRequest.email(), memberRequest.password()));
    }

    @Performance
    public MemberResponses findAllMember() {
        List<MemberResponse> members = memberRepository.findAll()
                .stream()
                .map(MemberResponse::new)
                .toList();

        return new MemberResponses(members);
    }

    public Member getMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("회원 ID 찾을 수 없음 - memberId = {}", memberId);
                    return new MemberNotFoundException(INTERNAL_SERVER_ERROR.getMessage());
                });
    }

    public Member getMemberByEmail(final String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("회원 이메일 찾을 수 없음 - email = {}", email);
                    return new MemberNotFoundException(INTERNAL_SERVER_ERROR.getMessage());
                });
    }
}
