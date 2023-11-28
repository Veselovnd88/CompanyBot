package ru.veselov.companybot.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "bot")
@NoArgsConstructor
public class BotProperties {

    private String name;

    private String token;

    private String adminId;

}
