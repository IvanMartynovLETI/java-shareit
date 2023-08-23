# java-shareit

Проект представляет собой реализацию бэкенда платформы для аренды вещей и позволяет осуществлять следующие действия:
* Добавлять новые вещи. Пользователь, добавивший вещь, считается ее владельцем. При добавлении вещи указываются ее
  наименование и краткое описание. Также вещь может быть добавлена в ответ на запрос.
* Производить поиск требуемой вещи. Поиск будет осуществляться как по наименованию вещи, так и по ее описанию.
* Осуществлять запрос на добавление отсутствующей требуемой вещи. Другие пользователи могут добавить требуемую вещь.
* Арендовать вещь на определенное время, указывая время и дату начала и конца аренды. Аренда вещи обязательно
  подтверждается ее владельцем.
* После окончания аренды пользователь может оставить отзыв на арендованную вещь.

Проект выполнен на основе микросервисной архитектуры и разворачивается в трех контейнерах: gateway_container,
server_container и shareIt_db_container.
#
С целью реализации представленных выше возможностей были созданы:

<details><summary>Эндпойнты пути /users:</summary>

* POST /users - создание пользователя.
* GET /users{id} - получение пользователя по его id.
* GET /users - получение списка всех пользователей.
* PATCH /users/{id} - обновление данных пользователя.
* DELETE /users/{id} - удаление пользователя по id.

</details>

<details><summary>Эндпойнты пути /requests:</summary>

* POST /requests - добавление нового запроса необходимой вещи. Основной частью запроса является его текст, в
  в котором пользователь описывает, какая именно вещь ему нужна.
* GET /requests - получение списка своих запросов вместе с данными об ответах на них. Для каждого запроса указываются:
  описание, дата и время создания, а также список ответов в формате: id вещи, ее название, описание description,
  requestId запроса и признак доступности вещи available.
* GET /requests/all?from={from}&size={size} - получение списка запросов, созданных другими пользователями. Пользователи
  могут просматривать существующие запросы, на которые они могли бы ответить.Запросы сортируются по дате создания от
  более новых к более старым. Организован постраничный вывод результатов.
* GET /requests/{requestId} - получение данных об одном конкретном запросе вместе с данными об ответах на него в том же
  формате, что и в эндпойнте GET /requests. Посмотреть данные об отдельном запросе может любой пользователь.

</details>

<details><summary>Эндпойнты пути /items:</summary>

* POST /items - добавление новой вещи. Идентификатор пользователя, создающего вещь, передается в заголовке
  X-Sharer-User-Id. Именно этот пользователь является владельцем созданной вещи.
* PATCH /items/{id} - редактирование вещи. Изменить можно название, описание и статус доступа к аренде. Редактировать
  вещь может только ее владелец, id которого передается в заголовке X-Sharer-User-Id.
* GET /items/{itemId} - просмотр информации о конкретной вещи по ее идентификатору. Информацию о вещи может
  просмотреть любой пользователь.
* GET /items?from={from}&size={size} - просмотр владельцем списка всех его вещей с указанием названия и описания
  каждой. идентификатор владельца передается с помощью параметра X-Sharer-User_Id. Организован постраничный вывод
  результатов.
* GET /items/search?text={text}&from={from}&size={size} - поиск вещи потенциальным арендатором. Пользователь передает в
  строке запроса текст и система ищет доступные для аренды вещи, содержащие этот текст в названии или в описании.
  Организован постраничный вывод результатов.
* POST /items/{itemId}/comment - создание отзыва по результатам пользования вещью. Отзыв можно оставить только тогда,
  когда пользователь брал вещь в аренду и срок ее аренды закончился. Идентификатор пользователя передается в заголовке
  X-Sharer-User_Id.

</details>

<details><summary>Эндпойнты пути /bookings:</summary>

* POST /bookings - добавление нового запроса на аренду вещи. Запрос может быть создан любым пользователем,
  идентификатор которого передается в заголовке X-Sharer-User-Id.
* PATCH /bookings/{bookingId}?approved={approved} - подтверждение или отклонение запроса на бронирование. Может быть
  выполнено только владельцем вещи, идентификатор которого передается в заголовке X-Sharer-User-Id. Параметр approved
  может принимать значения true или false.
* GET bookings/{bookingId} - получение данных о конкретном бронировании, включая его статус. Может быть выполнено
  либо автором бронирования, либо владельцем вещи, к которой относится бронирование.
* GET /bookings?state={state}&from={from}&size={size} - получение списка всех бронирований текущего пользователя,
  идентификатор которого передается в заголовке X-Sharer-User-Id. Параметр state необязательный и по умолчанию равен
  ALL. Также он может принимать значения CURRENT, PAST, FUTURE, WAITING и REJECTED. Бронирования возвращаются по дате
  от более новых к более старым. Организован постраничный вывод результатов.
* GET /bookings/owner?state={state}&from={from}&size={size} - получение списка всех бронирований владельца вещей,
  идентификатор которого передается в заголовке X-Sharer-User-Id. параметр state имеет тот же смысл, что и в предыдущем
  эндпойнте. Организован постраничный вывод результатов.

</details>

#
Проект реализован на Java 11 с использованием следующих технологий:
* Spring Boot
* Spring Framework
* JPA (Hibernate ORM)
* Lombok
* Maven
* PostgreSQL
* H2
* SLF4J
* Mockito
* Docker
* JUnit

#
<p align="center">Схема базы данных</p>

![Диаграмма базы данных](/dbdiagram.png)

Примечания:
* В таблице users осуществляется проверка уникальности поля email средствами базы данных.
* В таблице bookings тип поля status - varchar с ограничениями на принимаемые значения (WAITING, APPROVED, REJECTED или
  CANCELED)

Перед запуском приложения необходимо убедиться, что на компьютере установлены Intellij IDEA и Docker.
Запуск приложения может осуществляться в следующей последовательности:
* В командной строке Intellij IDEA выполнить команду mvn clean package для сборки приложения.
* Исполнить команду docker-compose up в командной строке, тем самым осуществив запуск приложения.