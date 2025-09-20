package hu.bsstudio.raktr;

import hu.bsstudio.raktr.config.GeneralDataProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@SpringBootApplication
@EnableJpaAuditing
@EnableConfigurationProperties(GeneralDataProperties.class)
public class RaktrApplication {

    public static void main(final String[] args) {
        SpringApplication.run(RaktrApplication.class, args);
    }

}
