package com.example.rewarddemo.member.entity;

import com.example.rewarddemo.adapter.jpa.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member extends BaseTimeEntity {
    @Id @GeneratedValue
    private Long no;

    @Column(unique = true, nullable = false)
    private String memberId;

    @Column(nullable = false)
    private String password;

    public Member(String memberId, String password) {
        this.memberId = memberId;
        this.password = password;
    }
}
