package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Member;
import com.example.demo.service.StatisticsService.MemberStatistics;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);

    List<Member> findByActiveTrue();
    @Query("select m from Member m JOIN FETCH m.loans where m.active = true ")
    List<Member> findMembersWithActiveLoans();



    @Query("SELECT new com.example.demo.service.StatisticsService$MemberStatistics(CONCAT(m.firstName, ' ', m.lastName), COUNT(l)) " +
           "FROM Member m LEFT JOIN m.loans l " +
           "GROUP BY m.id, m.firstName ,m.lastName" +
           "ORDER BY COUNT(l) DESC")
    List<MemberStatistics> getMostActiveMembers();
}
