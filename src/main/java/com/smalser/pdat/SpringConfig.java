package com.smalser.pdat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;

@EnableWebMvc
@Configuration
@ComponentScan("com.smalser.pdat.*")
public class SpringConfig
{
    @Autowired
    DataSource ds;

    @Bean
    InMemoryDBInitializer dbInitializer()
    {
        return new InMemoryDBInitializer(ds);
    }

    //moved to xml declaration to support IDE XML validation
//    @Bean(name = "dataSource")
//    DriverManagerDataSource dataSource()
//    {
//        DriverManagerDataSource ds = new DriverManagerDataSource("jdbc:derby:memory:pdatDB;create=true");
//        ds.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
//        return ds;
//    }
}
