import org.springframework.boot.SpringApplication;
import ru.veselov.companybot.CompanyBotApplication;
import ru.veselov.companybot.config.PostgresTestContainersConfiguration;

public class CompanyBotLocalDevApplication {
    /**
     * Run application with self created and managed docker container with basic configuration
     */
    public static void main(String[] args) {
        SpringApplication.from(CompanyBotApplication::main)
                .with(PostgresTestContainersConfiguration.class)
                .run(args);
    }

}
