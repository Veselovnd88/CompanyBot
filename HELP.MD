Запуск через докер
___________________________
Создать image из докерфайла
docker build -t mybit . (точка в конце говорит что докер файл находится в текущей директории)
#создаем том для хранения логов на хосте
docker volume create bot
#прикрепление отдельно нашего тома, но можно сразу прописать в команде создания
--mount source=bot,destination=/opt/log
#создаем контейнер, network=host-> для того чтобы контейнер подключился к базе на текущем хосте
#присоединяем два тома - первый для логов, второй для файла конфигурации
docker create --network=host --name mybot \
-v /home/nikolay/log/:/opt/log \
-v /home/nikolay/git/CompanyBot/application.yml:/opt/app/application.yml \
-e "POSTGRES_NAME=${POSTGRES_NAME}" \
-e "POSTGRES_PASSWORD=${POSTGRES_PASSWORD}" \
-e "PL_BOT_NAME=${PL_BOT_NAME}" -e "PL_BOT_TOKEN=${PL_BOT_TOKEN}" \
-p 9100:9100 -t companybot:latest
#запуск бота
docker start mybot
#Просмотр логов
docker logs mybot
#Остановить
docker stop mybot
#просмотр запущенных контейнеров
docker ps
#просмотр всех созданных контейнеров
docker ps
#Удалить контейнер
docker rm <имя>
#Список томов
docker volume list
#удалить том
docker volume rm <имя тома>
#зайти в интерактивном режиме в контейнер
docker exec -it <имя конт> sh

по умолчанию тома создаются /var/lib/docker