# TelegramBotCB

Этот бот для Telegram позволяет получать актуальный курс валют с сайта Центробанка РФ. Я разработал его для собственного удобства, залил на удаленный сервер и регулярно им пользуюсь 🙂
Бот доступен в Telegram по ссылке: [@Central_Bank_Exchange_Rate_bot](https://t.me/Central_Bank_Exchange_Rate_bot)

Он умеет:

- Показывать текущий курс выбранной валюты из JSON файла.
- Получать данные за неделю в формате XML и отображать в виде текста
- Отображать недельный курс в виде графика.

## Стек технологий

- **Java 21**
- **Spring Boot**
- **Lombok** (для удобства работы с Java-классами)
- **Jackson** (десириализация java объектов из JSON и XML)
- **TelegramBots** (Telegram API)
- **JFreeChart** (построение графика)
- **SLF4J** (логирование)

## 📌 **Документация**
К боту написана документация в формате **Javadoc**.  
Вы можете сгенерировать её в виде HTML с помощью Maven:

## Установка и запуск

### 1. Клонирование репозитория

```
git clone https://github.com/belnitskii/TelegramBotCB.git
```

### 2. Конфигурация

Добавьте данные своего бота в application.properties:

```
bot.name=your_telegram_bot_name
bot.token=your_telegram_bot_token
```

### 3. Сборка и запуск

В корневой папке проекта выполните команды

```
mvn clean package
mvn spring-boot:run
```

### 🔧 **Как сгенерировать документацию?**
1. Выполните команду:
   ```sh
   mvn javadoc:javadoc

2. Готовая документация появится в каталоге:
   ```sh
   target/reports/apidocs/index.html

3. Откройте index.html в браузере, чтобы просмотреть документацию.


## Использование

- Используйте команды кнопки для выбора валюты.
- Выберите период.
- Выберите способ отображения (текст или график)

## Дальнейшие планы

- Добавление тестов.
