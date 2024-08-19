# <img src="https://img.shields.io/badge/Описание_приложения-8A2BE2" width="300"/>
Основной функционал данного мобильного приложения - простая и удобная организация локальный встреч и мероприятий для небольшой группы людей, таких как:
- Встреча выпускников
- День рождения
- Поход на квиз компанией друзей
- ...
<br /><br />

# <img src="https://img.shields.io/badge/Главные_функции_приложения-8A2BE2" width="350"/>
- Добавление и удаление
- Добавление и удаление гостей
- Редактирование карточек гостя
- Отправка приглашений с QR кодом
- Отслеживание статуса гостя на мероприятии
- Контроль входа и выхода по QR коду

В качестве отдельного функционала реализована работа с группами гостей
<br /><br />

# <img src="https://img.shields.io/badge/Стек_технологий-8A2BE2" width="200"/>
<div align="left">
    <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/android/android-plain-wordmark.svg" width="70"/>
    <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/firebase/firebase-original-wordmark.svg" width="70"/>
    <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/java/java-original-wordmark.svg" width="70"/>
    <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/gradle/gradle-original.svg" width="70" />
    <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/xml/xml-plain.svg" width="70"/>
</div><br /><br />

# <img src="https://img.shields.io/badge/Для_разработчиков-8A2BE2" width="230"/>
Чтобы успешно синхронизировать gradle, добавьте внутри файла build.gradle (Module:app) в блок `dependencies`:

```gradle
implementation 'com.google.android.gms:play-services-auth:21.2.0'
implementation 'com.google.android.material:material:1.6.0'
implementation 'io.github.muddz:styleabletoast:2.4.0'
implementation 'com.journeyapps:zxing-android-embedded:4.3.0'
```

Классы, расширяющие интерфейс `Serializable`, имеют пустой конструктор по умолчанию для поддержания интерфейса сереализуемых объектов, которые объявлены как

```Java
public ClassName() {
    // default to interface
}
```
<br />

# <img src="https://img.shields.io/badge/Работа_приложения-8A2BE2" width="250"/>

## Регистрация и авторизация в приложении
Приложение поддерживает регистрацию с помощью почты, авторизацию черее почту и через аккаунт Google, а так же имеет функцию "Забыл пароль".
<br />

## Создание мероприятий
Пользователь может создавать мероприятия, указывая название мепроприятия, время, место встречи, а так же указывать наличие контроля входа и выхода и количество входов и выходов для гостей.
<br /><img src="https://github.com/user-attachments/assets/72b897f5-01ff-4230-9c42-465cccc29987" width="200" /><br />

## Страница пользователя с созданными мероприятиями
Здесь отображается список карточек всех мероприятий пользователя.
<br /><img src="https://github.com/user-attachments/assets/42173445-a355-4f52-8c91-aecd5bcb0054" width="200" /><br />

## Страница с гостями и группами гостей
Здесь отображается список карточек всех гостей и групп гостей, приглашённых на указанное мероприятие.
<div align="left">
    <img src="https://github.com/user-attachments/assets/e337ba37-5e4d-4641-a5ac-236dd35fad70" width="200" />
    <img src="https://github.com/user-attachments/assets/38e472e2-78cc-4e33-a3b3-6bbf461b2913" width="200" />
</div><br />

## Страница редактирования информации о госте
Здесь можно редактировать информацию о госте, а так же увидеть QR код, сгенерированный для него. Данный QR код можно отправить в популярные мессенджеры или на почту. Вместе с QR автоматически генерируется пригласительное письмо.
<br /><img src="https://github.com/user-attachments/assets/29e92800-ba9f-4503-9f8d-81a8fa1a05df" width="200" /><br />
Вид пригласительного письма:
- Для гостя
```
<Имя гостя>, вы приглашены на мероприятие: "<название мероприятия>",
Адрес: "<адрес мероприятия>"
К сообщению прикреплён QR код, покажите его при входе
Начало в <время начала мероприятия>, не опаздывайте!
<Имя пользователя (создателя мероприятия)> ждёт вас!
```
- Для группы
```
Группа <название группы> приглашена на мероприятие "<название мероприятия>" по адресу: <адрес мероприятия>.
Не опаздывайте, начало в <время начала мероприятия>
```
<br />

## Режим сканирования
Вход в данный режим возможен без регистрации и осуществляется с помощью ввода ключевого слова мероприятия, контроль посещения которого осуществляется в данный момент.
<br /><img src="https://github.com/user-attachments/assets/cdb4f74c-b0f2-42dc-acb5-275761134c44" width="200" /><br />

## Просмотр статуса гостя
У гостя есть 3 статуса:
- Не присустсовал на мероприятии
- Сейчас на мероприятии
- Вышел с мероприятия

После очередного сканирования статус гостя автоматически изменяется и организатор может посмотреть его в карточке гостя в приложении.
<div align="left">
    <img src="https://github.com/user-attachments/assets/88d42021-2e83-4a7c-a125-decd22c52121" width="200" />
    <img src="https://github.com/user-attachments/assets/88183ebe-d3f2-4742-9266-47a1966fd5b5" width="200" />
</div>
