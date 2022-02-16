package de.taimos.dvalin.jpa.config;

/*-
 * #%L
 * JPA support for dvalin using Hibernate
 * %%
 * Copyright (C) 2015 - 2017 Taimos GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import javax.sql.DataSource;

import de.taimos.daemon.spring.conditional.OnSystemProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;


@Configuration
@OnSystemProperty(propertyName = "ds.type", propertyValue = "POSTGRESQL")
public class DatabasePOSTGRESQL {

    @Value("jdbc:postgresql://${ds.pgsql.host}:${ds.pgsql.port}/${ds.pgsql.db}${ds.pgsql.additionalparams:}")
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
