# TelegramBotCB

This Telegram bot allows you to get the current exchange rate from the Central Bank of Russia.  
I developed it for my personal convenience, deployed it to a remote server, and use it regularly 🙂

The bot is available on Telegram: [@Central_Bank_Exchange_Rate_bot](https://t.me/Central_Bank_Exchange_Rate_bot)

It can:

- Show the current exchange rate of a selected currency from a JSON file.
- Retrieve weekly data in XML format and display it as text.
- Display the weekly exchange rate as a chart.

## Tech Stack

- **Java 21**
- **Spring Boot**
- **Lombok** (for convenient Java class handling)
- **Jackson** (for deserializing Java objects from JSON and XML)
- **TelegramBots** (Telegram API)
- **JFreeChart** (for chart generation)
- **SLF4J** (logging)
- **JUnit 5** (testing)
- **Mockito** (testing)

## 📌 **Documentation**

The bot is documented using **Javadoc**.  
You can generate it as HTML using Maven.

## Installation and Running

### 1. Clone the Repository

```
git clone https://github.com/belnitskii/TelegramBotCB.git
```

### 2. Configuration

Add your bot credentials to `application.properties`:

```
bot.name=your_telegram_bot_name
bot.token=your_telegram_bot_token
```

### 3. Build and Run

From the root project directory, run:

```
mvn clean package
mvn spring-boot:run
```


### Generate Documentation

Run the command:

```
mvn javadoc:javadoc
```

The generated documentation will be located at:

```
target/reports/apidocs/index.html
```

Open `index.html` in your browser to view the documentation.

## Usage

- Use command buttons to select a currency.
- Choose a time period.
- Choose a display format (text or chart).

