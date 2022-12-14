package com.example.rewarddemo.adapter.querydsl.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class QuerydslConfigurationTest {
    @Autowired
    JPAQueryFactory jpaQueryFactory;

    @Test
    @DisplayName("jpaQueryFactory bean 등록 test")
    public void japQueryFactoryBeanTest() throws Exception {
        assertThat(this.jpaQueryFactory).isNotNull();
    }
}