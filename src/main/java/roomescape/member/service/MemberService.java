package roomescape.member.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.global.config.Performance;
import roomescape.member.domain.Member;
import roomescape.member.dto.MemberRequest;
import roomescape.member.dto.MemberResponse;
import roomescape.member.dto.MemberResponses;
import roomescape.member.repository.MemberRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public void save(final MemberRequest request) {
        log.info("[회원가입 요청] email: {}, name: {}", request.email(), request.name());

        if (memberRepository.existsByEmail(request.email())) {
            log.warn("[회원가입 실패] 중복 이메일 가입 - duplicateEmail: {}", request.email());
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        memberRepository.save(
                Member.withDefaultRole(request.name(), request.email(), request.password()));

        log.info("[회원가입 성공] email: {}, name: {}", request.email(), request.name());
    }

    @Performance
    public MemberResponses findAllMember() {
        List<MemberResponse> members = memberRepository.findAll()
                .stream()
                .map(MemberResponse::new)
                .toList();

        return new MemberResponses(members);
    }
}
