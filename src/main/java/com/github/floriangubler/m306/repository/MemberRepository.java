package com.github.floriangubler.m306.repository;

import com.github.floriangubler.m306.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.rmi.server.UID;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<MemberEntity, UUID> {
    List<MemberEntity> findAll();

    Optional<MemberEntity> findByCardId(String cardid);

    void deleteById(UUID memberid);
}
