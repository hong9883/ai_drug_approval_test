package com.drugapproval;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 의약품 허가심사 검토 프로그램 메인 애플리케이션
 */
@SpringBootApplication
@EnableJpaAuditing
public class DrugApprovalApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrugApprovalApplication.class, args);
    }
}
