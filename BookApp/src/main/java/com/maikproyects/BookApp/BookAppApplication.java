package com.maikproyects.BookApp;

import com.maikproyects.BookApp.principal.Principal;
import com.maikproyects.BookApp.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookAppApplication implements CommandLineRunner {

	@Autowired
	private MenuService menuService;

	public static void main(String[] args) {
		SpringApplication.run(BookAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(menuService);
		principal.EjecutarAplicacion();
	}
}

