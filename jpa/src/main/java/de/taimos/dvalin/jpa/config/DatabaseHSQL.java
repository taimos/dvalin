package de.taimos.dvalin.jpa.config;

import de.taimos.daemon.spring.conditional.OnSystemProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseFactoryBean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;


@Configuration
@OnSystemProperty(propertyName = "ds.type", propertyValue = "HSQL")
public class DatabaseHSQL {

    @Bean
    public DataSource dataSource() {
        EmbeddedDatabaseFactoryBean factoryBean = new EmbeddedDatabaseFactoryBean();
        factoryBean.setDatabaseName("dataSource");
        factoryBean.setDatabaseType(EmbeddedDatabaseType.HSQL);
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }

}
