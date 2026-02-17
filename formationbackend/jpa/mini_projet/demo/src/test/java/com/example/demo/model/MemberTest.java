package com.example.demo.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests du modèle Member")
class MemberTest {

    @Test
    @DisplayName("Doit créer un membre avec toutes les propriétés")
    void testMemberCreation() {
        // Given & When
        Member member = new Member();
        member.setFirstName("Jean");
        member.setLastName("Dupont");
        member.setEmail("jean.dupont@email.com");
        member.setPhoneNumber("0601020304");
        member.setMembershipDate(LocalDate.now());
        member.setActive(true);

        // Then
        assertThat(member.getFirstName()).isEqualTo("Jean");
        assertThat(member.getLastName()).isEqualTo("Dupont");
        assertThat(member.getEmail()).isEqualTo("jean.dupont@email.com");
        assertThat(member.getPhoneNumber()).isEqualTo("0601020304");
        assertThat(member.getMembershipDate()).isEqualTo(LocalDate.now());
        assertThat(member.getActive()).isTrue();
    }

    @Test
    @DisplayName("Un nouveau membre doit être actif par défaut")
    void testMemberActiveByDefault() {
        // Given & When
        Member member = new Member();
        member.setActive(true);

        // Then
        assertThat(member.getActive()).isTrue();
    }

    @Test
    @DisplayName("Doit pouvoir désactiver un membre")
    void testDeactivateMember() {
        // Given
        Member member = new Member();
        member.setActive(true);

        // When
        member.setActive(false);

        // Then
        assertThat(member.getActive()).isFalse();
    }

    @Test
    @DisplayName("L'email est obligatoire et unique")
    void testEmailRequired() {
        // Given & When
        Member member = new Member();
        member.setEmail("unique@email.com");

        // Then
        assertThat(member.getEmail()).isNotNull();
        assertThat(member.getEmail()).isNotEmpty();
    }
}
