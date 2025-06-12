package roomescape.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Email;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.dto.member.request.MemberRequest;
import roomescape.dto.member.response.MemberResponse;
import roomescape.exception.member.MemberException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    private static void validateEmailExists(final boolean emailExist) {
        if (emailExist) {
            throw new MemberException("중복되는 이메일입니다.");
        }
    }

    @Transactional
    public MemberResponse save(final MemberRequest request) {
        boolean emailExist = memberRepository.existsByEmail(new Email(request.email()));
        validateEmailExists(emailExist);

        Member member = memberRepository.save(
                Member.createWithoutId(request.name(), request.email(), request.password()));

        return new MemberResponse(member.id(), member.getName());
    }

    public Member findByEmail(final String email) {
        return memberRepository.findByEmail(new Email(email))
                .orElseThrow(() -> new MemberException("멤버를 찾을 수 없습니다."));
    }

    public boolean isExistsByEmail(final String email) {
        return memberRepository.existsByEmail(new Email(email));
    }

    public Member findById(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberException("멤버를 찾을 수 없습니다."));
    }

    public List<MemberResponse> findAll() {
        return memberRepository.findAll().stream()
                .map(member -> new MemberResponse(member.id(), member.getName()))
                .toList();
    }
}
