package roomescape.member.infrastructure;

import org.springframework.stereotype.Repository;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberRepositoryImpl implements MemberRepository {

    private final JpaMemberRepository jpaMemberRepository;

    public MemberRepositoryImpl(JpaMemberRepository jpaMemberRepository) {
        this.jpaMemberRepository = jpaMemberRepository;
    }

    @Override
    public Member save(final Member member) {
        return jpaMemberRepository.save(member);
    }

    @Override
    public boolean existsByEmail(final String email) {
        return jpaMemberRepository.existsByEmail(email);
    }

    @Override
    public Optional<Member> findByEmailAndPassword(final String email, final String password) {
        return jpaMemberRepository.findByEmailAndPassword(email, password);
    }

    @Override
    public Optional<Member> findById(final Long memberId) {
        return jpaMemberRepository.findById(memberId);
    }

    @Override
    public List<Member> findAll() {
        return jpaMemberRepository.findAll();
    }
}
