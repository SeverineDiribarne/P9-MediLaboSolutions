package com.medilabo.medilabo_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class MedilaboGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedilaboGatewayApplication.class, args);
	}

	@Bean
	public RouteLocator routerBuilder(RouteLocatorBuilder routeLocatorBuilder){
		return routeLocatorBuilder.routes()
				.route("medilabo-list",r->r.path("/api/patient/list")
						.uri("http://localhost:8082/"))
				.route("medilabo-details",r->r.path("/api/patient/details/{id}")
						.filters(f -> f.addRequestParameter("id", "defaultId"))
						.uri("http://localhost:8082/"))
				.route("medilabo-addvalidate",r->r.path("/api/patient/addvalidate")
						.uri("http://localhost:8082/"))
//				.route("medilabo-update",r->r.path("/api/patient/update/{id}")
//						.filters(f -> f.addRequestParameter("id", "defaultId"))
//						.uri("http://localhost:8082/"))
				.build();
	}
}
