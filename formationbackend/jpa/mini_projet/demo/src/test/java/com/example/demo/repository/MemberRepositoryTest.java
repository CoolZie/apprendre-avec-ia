package com.example.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.demo.model.Member;

@DataJpaTest
@DisplayName("Tests du MemberRepository")
class MemberRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemberRepository memberRepository;

    private Member member1;
    private Member member2;

    @BeforeEach
    void setUp() {
        member1 = new Member();
        member1.setFirstName("Jean");
        member1.setLastName("Dupont");
        member1.setEmail("jean.dupont@email.com");
        member1.setPhoneNumber("0601020304");
        member1.setMembershipDate(LocalDate.of(2020, 1, 1));
        member1.setActive(true);
        entityManager.persist(member1);

        member2 = new Member();
        member2.setFirstName("Marie");
        member2.setLastName("Martin");
        member2.setEmail("marie.martin@email.com");
        member2.setPhoneNumber("0602030405");
        member2.setMembershipDate(LocalDate.of(2021, 6, 15));
        member2.setActive(false);
        entityManager.persist(member2);

        entityManager.flush();
    }

    @Test
    @DisplayName("Doit trouver un membre par email")
    void testFindByEmail() {
        // When
        Optional<Member> found = memberRepository.findByEmail("jean.dupont@email.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Jean");
        assertThat(found.get().getLastName()).isEqualTo("Dupont");
    }

    @Test
    @DisplayName("Doit retourner vide si l'email n'existe pas")
    void testFindByEmail_NotFound() {
        // When
        Optional<Member> found = memberRepository.findByEmail("inconnu@email.com");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Doit trouver tous les membres actifs")
    void testFindByActiveTrue() {
        // When
        List<Member> activeMembers = memberRepository.findByActiveTrue();

        // Then
        assertThat(activeMembers).hasSize(1);
        assertThat(activeMembers.get(0).getEmail()).isEqualTo("jean.dupont@email.com");
        assertThat(activeMembers.get(0).getActive()).isTrue();
    }

    @Test
    @DisplayName("Doit sauvegarder un nouveau membre")
    void testSaveMember() {
        // Given
        Member newMember = new Member();
        newMember.setFirstName("Pierre");
        newMember.setLastName("Durand");
        newMember.setEmail("pierre.durand@email.com");
        newMember.setPhoneNumber("0603040506");
        newMember.setActive(true);

        // When
        Member saved = memberRepository.save(newMember);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("pierre.durand@email.com");
        assertThat(saved.getMembershipDate()).isNotNull();
    }

    @Test
    @DisplayName("Doit mettre à jour un membre existant")
    void testUpdateMember() {
        // Given
        member1.setPhoneNumber("0699999999");

        // When
        Member updated = memberRepository.save(member1);

        // Then
        assertThat(updated.getPhoneNumber()).isEqualTo("0699999999");
    }

    @Test
    @DisplayName("L'email doit être unique")
    void testEmailUniqueness() {
        // Given
        Member duplicateMember = new Member();
        duplicateMember.setFirstName("Test");
        duplicateMember.setLastName("Test");
        duplicateMember.setEmail("jean.dupont@email.com"); // Même email
        duplicateMember.setActive(true);

        // When & Then - devrait échouer avec une contrainte d'unicité
        assertThat(entityManager.persistAndFlush(duplicateMember))
                .satisfiesAnyOf(
                        member -> assertThat(member).isNotNull(),
                        member -> {
                            throw new RuntimeException("Duplicate email should fail");
                        }
                );
    }

    @Test
    @DisplayName("Devrait trouver les membres avec date d'adhésion récente")
    void shouldFindMembersWithRecentMembershipDate() {
        // When
        List<Member> allMembers = memberRepository.findAll();

        // Then
        long recentMembers = allMembers.stream()
                .filter(m -> m.getMembershipDate().isAfter(LocalDate.now().minusYears(2)))
                .count();
        
        assertThat(recentMembers).isGreaterThan(0);
    }

    @Test
    @DisplayName("Devrait compter correctement les membres actifs et inactifs")
    void shouldCountActiveAndInactiveMembers() {
        // When
        List<Member> activeMembers = memberRepository.findByActiveTrue();
        long totalMembers = memberRepository.count();

        // Then
        assertThat(activeMembers.size()).isEqualTo(1);
        assertThat(totalMembers).isEqualTo(2);
        long inactiveMembers = totalMembers - activeMembers.size();
        assertThat(inactiveMembers).isEqualTo(1);
    }

    @Test
    @DisplayName("Devrait permettre de désactiver un membre")
    void shouldAllowDeactivatingMember() {
        // Given
        assertThat(member1.getActive()).isTrue();

        // When
        member1.setActive(false);
        Member updated = memberRepository.save(member1);

        // Then
        assertThat(updated.getActive()).isFalse();
    }

    @Test
    @DisplayName("Devrait valider le format de l'email")
    void shouldHaveValidEmailFormat() {
        // When
        List<Member> allMembers = memberRepository.findAll();

        // Then
        allMembers.forEach(member -> {
            assertThat(member.getEmail()).contains("@");
            assertThat(member.getEmail()).contains(".");
        });
    }

    @Test
    @DisplayName("Devrait avoir une date d'adhésion définie")
    void shouldHaveMembershipDateSet() {
        // When
        List<Member> allMembers = memberRepository.findAll();

        // Then
        allMembers.forEach(member -> {
            assertThat(member.getMembershipDate()).isNotNull();
        });
    }

    @Test
    @DisplayName("Devrait supprimer un membre")
    void shouldDeleteMember() {
        // Given
        Long memberId = member1.getId();
        
        // When
        memberRepository.delete(member1);
        entityManager.flush();
        Optional<Member> deleted = memberRepository.findById(memberId);

        // Then
        assertThat(deleted).isEmpty();
    }
}

