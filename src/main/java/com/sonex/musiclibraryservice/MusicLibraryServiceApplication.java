package com.sonex.musiclibraryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})

public class MusicLibraryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicLibraryServiceApplication.class, args);
    }

}
