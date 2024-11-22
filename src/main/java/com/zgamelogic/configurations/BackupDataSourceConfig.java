package com.zgamelogic.configurations;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Collections;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.zgamelogic.data.repositories.backup",
        entityManagerFactoryRef = "backupEntityManagerFactory",
        transactionManagerRef = "backupTransactionManager"
)
public class BackupDataSourceConfig {
    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @Bean
    public EntityManagerFactoryBuilder entityManagerFactoryBuilder(JpaVendorAdapter jpaVendorAdapter, JpaProperties jpaProperties) {
        return new EntityManagerFactoryBuilder(jpaVendorAdapter, jpaProperties.getProperties(), null);
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean(name = "backupEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean backupEntityManagerFactory(
            @Qualifier("backupDataSource") DataSource dataSource,
            EntityManagerFactoryBuilder builder
    ) {
        return builder
                .dataSource(dataSource)
                .packages("com.zgamelogic.data.entities")
                .persistenceUnit("backup")
                .properties(Collections.singletonMap("hibernate.hbm2ddl.auto", ddlAuto))
                .build();
    }

    @Bean
    public PlatformTransactionManager backupTransactionManager(
            @Qualifier("backupEntityManagerFactory") LocalContainerEntityManagerFactoryBean backupEntityManagerFactory) {
        return new JpaTransactionManager(backupEntityManagerFactory.getObject());
    }

    @Bean(name = "backupDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.backup")
    public DataSource backupDataSource() {
        return DataSourceBuilder.create().build();
    }
}
