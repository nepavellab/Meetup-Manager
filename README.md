# <img src="https://img.shields.io/badge/Описание_приложения-8A2BE2" width="300"/>
Основной функционал данного мобильного приложения - простая и удобная организация локальный встреч и мероприятий для небольшой группы людей, таких как:
- Встреча выпускников
- День рождения
- Поход на квиз компанией друзей
- ...
<br /><br />

# <img src="https://img.shields.io/badge/Главные_функции_приложения-8A2BE2" width="350"/>
- Создание встреч
- Добавление и удаление гостей
- Отправка приглашений с QR кодом
- Контроль посещаемости по QR коду (сканивароние QR и его обработка)

В качестве отдельного функционала реализована работа с группами гостей
<br /><br />

# <img src="https://img.shields.io/badge/Стек_технологий-8A2BE2" width="200"/>
<div align="left">
    <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/jetpackcompose/jetpackcompose-original-wordmark.svg" width="70"/>
    <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/firebase/firebase-original-wordmark.svg" width="70"/>
    <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/java/java-original-wordmark.svg" width="70"/>
    <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/gradle/gradle-original.svg" width="70" />
    <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/xml/xml-plain.svg" width="70"/>
    <img src="https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/google/google-original-wordmark.svg" width="70"/>
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

# <img src="https://img.shields.io/badge/Работа_приложения-8A2BE2" width="250"/>