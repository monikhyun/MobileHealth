package mobile.health.healine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "mobile.health.healine.Entity")
public class HealineApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealineApplication.class, args);
	}

}
