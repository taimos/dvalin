package de.taimos.dvalin.jpa.config;

import de.taimos.daemon.spring.conditional.OnSystemProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;


@Configuration
@OnSystemProperty(propertyName = "ds.type", propertyValue = "MYSQL")
public class DatabaseMYSQL {

    @Value("jdbc:mysql://${ds.mysql.host}:${ds.mysql.port}/${ds.mysql.db}")
    private String url;

    @Value("${ds.mysql.user}")
    private String username;

    @Value("${ds.mysql.password}")
    private String password;


    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(this.url);
        dataSource.setUsername(this.username);
        dataSource.setPassword(this.password);
        return dataSource;
    }


}
