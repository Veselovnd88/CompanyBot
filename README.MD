<img alt="GitHub Actions Workflow Status" src="https://img.shields.io/github/actions/workflow/status/Veselovnd88/CompanyBot/gradle.yml"> <img alt="GitHub commit activity" src="https://img.shields.io/github/commit-activity/w/Veselovnd88/CompanyBot">



Бот-ассистент для приема заявок от клиентов.<br>
Пользователь выбирает тему запроса(отдел кому будет адресован запрос), далее вводит сам запрос (любое поддерживаемое сообщение Telegram).<br>
Далее предлагается ввести контактные данные для обратной связи (показывается клавиатура с кнопками, пользователь выбирает тип контакта, вводит данные).<br>
Реализована валидация e-mail и телефонного номера.<br>
Обязателен ввод хотя бы 1 контакта и имени.<br>
По умолчанию бот отправляет данный запрос с контактными данными администратору бота (его adminId указывается в application.yml).<br>
Администратор бота может присоединить бота к каналу в качестве администратора - тогда бот будет отправлять запрос в этот канал.<br>
Администратор бота может добавить и отредактировать ответственного менеджера и назначить ему темы (отделы), по которым будет получать оповещения (mention) в канале.<br>
Администратор бота может управлять темами(отделами).<br>
Администратор устанавливает описание компании (команда /about)

DAO реализованы отдельно для лучшего понимания механизмов работы Hibernate.
_________________
Технологии:   
- Java 17.0.5   
- Spring Boot 2.7.5   
- Maven 3.8.6
- Spring Data JPA
- Postgresql 14.6
- TelegramApi для Java: https://github.com/rubenlagus/TelegramApi


_________________
Бот реализован на вебхуках, для работы требуется назначить переменные среды:
- имя бота: PL_BOT_NAME=
- токен: PL_BOT_TOKEN=
- логин Postgresql: POSTGRES_NAME=
- пароль Postgresql: POSTGRES_PASSWORD=  
  В application.yml указать:
- spring.datasource.url:адрес базы данных;
- bot.adminId: телеграм id администратора;
- bot.webhookpath: ссылка на вебхук;
- chat-interval: интервал между отправлениями сообщений ботом в один канал
______________________________________
Мой бот крутится на VDS-сервере (Ubuntu) в качестве отдельного linux-сервиса.<br>
Т.к. мощностей сервера для докера не хватает, в рабочем варианте бота всё запускается отдельно.<br>
Как запустить:<br>
Будем считать, что VDS-сервер уже настроен, созданы соответствующие юзеры с определенными правами, установлен Postgres, и apache2.<br>
Получены имя и токен у BotFather<br>
1) Клонируем в выбранную папку проект через git clone.
2) Сборка через Maven: mvn clean install "-Dmaven.test.skip=true', чтобы пропустить тесты.
3) Создаем переменные окружения, на linux: sudo nano ~/.bash_profile, далее source ~/.bash_profile для обновления
4) Создаем сервис linux sudo nano /etc/systemd/system/<имя_сервиса>.service, при каждом внесении изменений в сервис не забываем делать systemctl daemon-reload
5) Открываем порт на котором будет работать бот
6) Обновляем конфигурационный файл в папке apache2/sites-enabled, добавив ProxyPass
7) Запускаем сервис systemctl start <Имя сервиса>
8) Устанавливаем вебхук https://api.telegram.org/bot<Имя_бота>:<Токен>/setWebhook?url=<URL>

RELEASE 1.0<br>
Рабочая версия работает: https://t.me/AllImpexBot
____________________________________________
Запуск через Docker
1) Сначала собрать приложение mvn clean install "-Dmaven.test.skip=true"
2) Создание образа docker build -t <name> .
3) Создание контейнера
   docker create --network=host --name <name> \
   -v <source>:/opt/log \
   -v <source>/application.yml:/opt/app/application.yml \
   -e "POSTGRES_NAME=${POSTGRES_NAME}" \
   -e "POSTGRES_PASSWORD=${POSTGRES_PASSWORD}" \
   -e "PL_BOT_NAME=${PL_BOT_NAME}" -e "PL_BOT_TOKEN=${PL_BOT_TOKEN}" \
   -p 9100:9100 -t companybot:latest
4) Запуск контейнера
docker start <name>
5) Просмотр логов
docker logs <name>
6) Остановка контейнера
docker stop <name>
