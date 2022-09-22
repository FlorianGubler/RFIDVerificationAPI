package com.github.floriangubler.m306.controller;

import com.github.floriangubler.m306.entity.MemberEntity;
import com.github.floriangubler.m306.repository.MemberRepository;
import com.github.floriangubler.m306.exception.UserNotFoundException;
import com.google.common.hash.Hashing;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.rmi.server.UID;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/members")
@Tag(name = "Members", description = "RFID Users management endpoints")
public class MemberController {

    private final MemberRepository repository;

    public MemberController(MemberRepository repository) {
        this.repository = repository;
    }

    @Operation(
            summary = "Get Members",
            description = "Get all Members"
    )
    @GetMapping
    List<MemberEntity> loadUsers() {
        return repository.findAll();
    }

    @Operation(
            summary = "Verify RFID UID",
            description = "Verifys a UID from a RFID Card Reader and return User"
    )
    @GetMapping("/verify/{cardid}")
    ResponseEntity<MemberEntity> verifyCardId(@PathVariable String cardid) {
        if(repository.findByCardId(getSHA256(cardid)).isPresent()){
            return new ResponseEntity<>(repository.findByCardId(getSHA256(cardid)).get(), HttpStatus.OK);
        } else{
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(
            summary = "Create a Member",
            description = "Create a new Member"
    )
    @PostMapping
    ResponseEntity<Void> createmember(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Member", required = true)
            @RequestBody(required = true)
            MemberEntity member) {
        try{
            member.setId(UUID.randomUUID());
            member.setCardId(getSHA256(member.getCardId()));
            repository.save(member);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(UserNotFoundException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(
            summary = "Delete a Member",
            description = "Delete a Member"
    )
    @DeleteMapping("/{memberid}")
    ResponseEntity<Void> deletemember(
            @Parameter(description = "MemberID", required = true)
            @PathVariable(name = "memberid", required = true)
            UUID memberid) {
            try{
                if(repository.findById(memberid).isEmpty()){
                    throw new UserNotFoundException("User with given ID not found");
                } else{
                    repository.deleteById(memberid);
                    return new ResponseEntity<>(HttpStatus.OK);
                }
            } catch(UserNotFoundException e){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
    }

    private String getSHA256(String inp){
        return Hashing.sha256()
                .hashString(inp, StandardCharsets.UTF_8)
                .toString();
    }
}
