package com.hitachi.library.service;

import com.hitachi.library.entity.Member;
import com.hitachi.library.exception.ResourceNotFoundException;
import com.hitachi.library.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private Member member;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        member = new Member();
        member.setName("Member Name");
    }

    @Test
    void testGetAllMembers() {
        when(memberRepository.findAll()).thenReturn(Collections.singletonList(member));

        List<Member> members = memberService.getAllMembers();

        assertNotNull(members);
        assertEquals(1, members.size());
        verify(memberRepository, times(1)).findAll();
    }

    @Test
    void testGetMemberById_Success() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        Member foundMember = memberService.getMemberById(1L);

        assertNotNull(foundMember);
        assertEquals("Member Name", foundMember.getName());
    }

    @Test
    void testGetMemberById_NotFound() {
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> memberService.getMemberById(1L));
    }

    @Test
    void testCreateMember() {
        when(memberRepository.save(member)).thenReturn(member);

        Member createdMember = memberService.createMember(member);

        assertNotNull(createdMember);
        assertEquals("Member Name", createdMember.getName());
    }

    @Test
    void testUpdateMember() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(memberRepository.save(member)).thenReturn(member);

        Member updatedMember = memberService.updateMember(1L, member);

        assertNotNull(updatedMember);
        assertEquals("Member Name", updatedMember.getName());
    }

    @Test
    void testDeleteMember() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        memberService.deleteMember(1L);

        verify(memberRepository, times(1)).delete(member);
    }
}
