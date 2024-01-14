[![](https://img.shields.io/badge/Spring%20Boot%20Version-3.2.0-orange)](/build.gradle) [![](https://img.shields.io/badge/Java%20Version-17-orange)](/build.gradle) <img alt="GitHub Actions Workflow Status" src="https://img.shields.io/github/actions/workflow/status/Veselovnd88/CompanyBot/gradle.yml?branch=main&label=test-and-build"> <img alt="GitHub commit activity" src="https://img.shields.io/github/commit-activity/y/Veselovnd88/CompanyBot">

 <img alt="GitHub top language" src="https://img.shields.io/github/languages/top/Veselovnd88/CompanyBot">

Бот-ассистент для приема заявок от клиентов.<br>
Пользователь выбирает тему запроса(отдел кому будет адресован запрос), далее вводит сам запрос (любое поддерживаемое
сообщение Telegram).<br>
Далее предлагается ввести контактные данные для обратной связи (показывается клавиатура с кнопками, пользователь
выбирает тип контакта, вводит данные).<br>
Реализована валидация e-mail и телефонного номера.<br>
Обязателен ввод хотя бы 1 контакта и имени.<br>
По умолчанию бот отправляет данный запрос с контактными данными администратору бота.<br>
Администратор бота может присоединить бота к каналу в качестве администратора - тогда бот будет отправлять запрос в этот
канал.<br>
Администратор устанавливает описание компании (команда /about)

Учтены ограничения по частоте отправки сообщений в чаты
_________________
Технологии:

- Java 17.0.5
- Spring Boot 3.2.0
- Gradle 8.4
- Spring Data JPA
- Validation API
- Spring Cache (Caffeine)
- Liquibase
- Lombok
- Mapstruct
- JUnit5, Mockito, TestContainers
- Postgresql 16.0
- TelegramApi для Java: https://github.com/rubenlagus/TelegramApi
- Покрытие тестами 93%
- Docker, Docker-compose

_________________
Бот реализован как LongPolling:
Для запуска необходимо скопировать .env.example и переименовать его в .env.config:
cp ./.env.example ./.env.config
Оставляем только следующие переменные: <br>
- APP_PORT= - порт на котором будет работать приложение
- BOT_ADMIN= - Телеграм ID администратора
- POSTGRES_DB="cbotDB" - название базы данных
- POSTGRES_USER= - юзернейм в PG
- POSTGRES_PASSWORD= - пароль в PG

для старта контейнеров запускаем скрипт start.sh <br>
bash start.sh ${BRANCH_NAME} C_BOT_NAME=${BOTNAME} C_BOT_TOKEN=${BOT_TOKEN} <br>
где

- ${BRANCH_NAME} имя ветки, с которой будет билдиться проект
- ${BOTNAME} - имя бота (из BotFather)
- ${C_BOT_TOKEN} - токен бота (из BotFather)

для остановки контейнеров запускаем скрипт stop.sh
