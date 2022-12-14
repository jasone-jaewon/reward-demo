package com.example.rewarddemo.member.entity;

import com.example.rewarddemo.adapter.jpa.common.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseTimeEntity {
    @Id @GeneratedValue
    private Long no;

    @Column(unique = true, nullable = false)
    private String memberId;

    @Column(nullable = false)
    private String password;

    private Long point = 0L;

    public Member(String memberId, String password) {
        this.memberId = memberId;
        this.password = password;
        this.point = 0L;
    }

    /**
     * 포인트 적립
     * @param point 적립할 포인트
     */
    public void earnPoint(long point) {
        this.point += point;
    }
}
