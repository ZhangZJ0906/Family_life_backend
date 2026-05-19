package com.example.Family_life_backend.repositary;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Family_life_backend.entity.group;

public interface GroupRepository extends JpaRepository<group, Long>{
    boolean existsByInviteCode(String inviteCode);

}
