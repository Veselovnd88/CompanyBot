package ru.veselov.companybot.bot;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "bot")
@NoArgsConstructor
public class BotProperties {

    private String name;

    private String token;

    private String adminId;

    private Long botId;

}
