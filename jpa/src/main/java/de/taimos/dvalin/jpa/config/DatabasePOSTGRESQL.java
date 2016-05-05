package de.taimos.dvalin.jpa.config;

import de.taimos.daemon.spring.conditional.OnSystemProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;


@Configuration
@OnSystemProperty(propertyName = "ds.type", propertyValue = "POSTGRESQL")
public class DatabasePOSTGRESQL {

    @Value("jdbc:postgresql://${ds.pgsql.host}:${ds.pgsql.port}/${ds.pgsql.db}")
    private String url;

    @Value("${ds.pgsql.user}")
    private String username;

    @Value("${ds.pgsql.password}")
    private String password;


    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl(this.url);
        dataSource.setUsername(this.username);
        dataSource.setPassword(this.password);
        return dataSource;
    }


}
