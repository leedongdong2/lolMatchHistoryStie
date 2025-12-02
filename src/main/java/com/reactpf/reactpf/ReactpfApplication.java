package com.reactpf.reactpf;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.reactpf.reactpf.mapper")
@SpringBootApplication
public class ReactpfApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactpfApplication.class, args);
	}

}
