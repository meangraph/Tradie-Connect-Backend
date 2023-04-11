package CSIT3214.GroupProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableWebSecurity
@ConditionalOnExpression("'${disable-security}'!='false'")
public class TradieFinderApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradieFinderApplication.class, args);
	}

}
