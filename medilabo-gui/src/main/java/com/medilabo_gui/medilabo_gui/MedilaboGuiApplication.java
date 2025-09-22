package com.medilabo_gui.medilabo_gui;

import com.medilabo_gui.medilabo_gui.controller.PatientController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class MedilaboGuiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedilaboGuiApplication.class, args);
	}

}
