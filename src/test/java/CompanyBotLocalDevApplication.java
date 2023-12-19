import org.springframework.boot.SpringApplication;
import ru.veselov.companybot.CompanyBotApplication;
import ru.veselov.companybot.config.LocalDevPostgresTestContainersConfiguration;

public class CompanyBotLocalDevApplication {
    /**
     * Run application with self created and managed docker container with basic configuration
     *
     * @param args array with arguments
     */
    public static void main(String[] args) {
        SpringApplication.from(CompanyBotApplication::main)
                .with(LocalDevPostgresTestContainersConfiguration.class)
                .run(args);
    }

}
