package ru.veselov.companybot;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.veselov.companybot.bot.CompanyBot;

@SpringBootTest
@ActiveProfiles("test")
class CompanyBotApplicationTests {
	@MockBean
	CompanyBot bot;
	@Test
	void contextLoads() {
	}

}
