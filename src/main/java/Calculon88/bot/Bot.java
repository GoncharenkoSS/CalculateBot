package Calculon88.bot;

import Calculon88.bot.Model.UserTG;
import Calculon88.bot.Model.Values;
import Calculon88.bot.Repository.UserRepository;
import Calculon88.bot.Repository.ValuesRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
public class Bot extends TelegramLongPollingBot {

    //Здесь можно добавить ID администратора.
    List<Long> administrators = List.of(583954324L);

    private final UserRepository userRepository;
    private final ValuesRepository valuesRepository;

    private UserTG user = new UserTG();
    private Values values;
    boolean session = false;
    int count = 0;
    String balance, risk, open, currency, exception = "";
    double bet = 0;
    Date lastDate;

    public Bot(UserRepository userRepository, ValuesRepository valuesRepository) {
        this.userRepository = userRepository;
        this.valuesRepository = valuesRepository;
    }

    @PostConstruct
    public void switchboard() throws TelegramApiException {

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

        try {
            telegramBotsApi.registerBot(new Bot(userRepository, valuesRepository));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "Calculon88_bot";
    }

    @Override
    public String getBotToken() {
        return "";
    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (userRepository.findByIdTG(message.getFrom().getId()).isEmpty()) {
            user.setIdTG(message.getFrom().getId());
            user.setFirst_name(message.getFrom().getFirstName());
            user.setLast_name(message.getFrom().getLastName());
            user.setUname(message.getFrom().getUserName());
            Date firstDate = new Date();
            user.setFirstTime(String.valueOf(firstDate));
            userRepository.save(user);
        }

        if (message != null && message.hasText() && !session) {
            values = new Values();
            switch (message.getText()) {
                case "/start":
                    choiceStart(message, "Здравствуйте, выберете необходимые действия");
                    System.out.println(message.getText());
                    break;
                case "О боте":
                    choiceStart(message, """
                            Данный бот поможет тебе сохранить депозит и подскажет рекомендуемую ставку.
                            Пример использования рассчета.\s
                            Размер депозита - 0.3
                            Риск на сделку % - 1
                            Цена входа - 93.63""");
                    System.out.println(message.getText());
                    break;
                case "Рассчитать стоимость":
                    choiceСurrency(message, "Выберите валюту");
                    System.out.println(message.getText());
                    break;
                case "RU":
                    calculation(message, "Введите Ваш баланс в рублях(в дроби ставить точку)\n" +
                            "\n" +
                            "Например:\n" +
                            "15000");
                    session = true;
                    currency = "Рубль";
                    values.setCurrency("Рубль");
                    System.out.println(message.getText());
                    break;
                case "USD":
                    calculation(message, "Введите Ваш баланс в долларах(в дроби ставить точку)\n" +
                            "\n" +
                            "Например:\n" +
                            "500");
                    session = true;
                    currency = "Доллар";
                    values.setCurrency("Доллар");
                    System.out.println(message.getText());
                    break;
                case "BTC":
                    calculation(message, "Введите Ваш баланс в биткоинах(в дроби ставить точку)\n" +
                            "\n" +
                            "Например:\n" +
                            "0.3");
                    session = true;
                    currency = "Биткоин";
                    values.setCurrency("Биткоин");
                    System.out.println(message.getText());
                    break;
                case "/get_last_req_user":
                    List<String> strUser = new ArrayList<>();
                    if(administrators.contains(message.getFrom().getId())) {
                        for (UserTG user : userRepository.findLast10userTG()) {
                            strUser.add("Имя: " + user.getFirst_name() + " ID: " + user.getIdTG());
                        }
                        calculation(message, strUser.toString());
                    }else calculation(message, " У вас нет доступа к данному функционалу");
                    strUser.clear();
                    break;
                case "/get_last_orders":
                    List<String> strValues = new ArrayList<>();
                    if(administrators.contains(message.getFrom().getId())) {
                        for (Values val : valuesRepository.findLast5values()) {
                            strValues.add("Имя: " + val.getOwner().getFirst_name() + "\n" +
                                    " Валюта: " + val.getCurrency() + "\n" +
                                    " Баланс: " + val.getBalance() + "\n" +
                                    " Риск: " + val.getRisk() + "\n" +
                                    " Вхождение: " + val.getOpen() + "\n" +
                                    " Ставка: " + val.getBet() + "\n" + " " + "\n");
                        }
                        calculation(message, strValues.toString());
                    }else calculation(message, " У вас нет доступа к данному функционалу");
                    strValues.clear();
                    break;
                default:
                    choiceStart(message, "Я Вас не понял! Отправьте /start чтобы начать работать с ботом");
                    System.out.println(message.getText());
                    break;
            }
        }

        if (message != null && message.hasText() && session) {
            count++;
            balance = message.getText();
            values.setBalance(message.getText());
            switch (count) {
                case 2:
                    calculation(message, "Теперь введите риск(0-100)%\n" +
                            "Не рекомендуем ставить риск более 1%. \n" +
                            "(В дроби ставьте точку)\n" +
                            "\n" +
                            "Например:\n" +
                            "1");
                    risk = message.getText();
                    values.setRisk(message.getText());
                    break;
                case 3:
                    calculation(message, "2. Введите сумму вхождения\n" +
                            "Пример вводимых данных:\n" +
                            "Рубли - 100\n" +
                            "Доллары - 1.5\n" +
                            "Биткоин - 0.0001");
                    open = message.getText();
                    values.setOpen(message.getText());
                    break;
                case 4:
                    switch (currency) {
                        case "Рубль":
                            try {
                                bet = Double.parseDouble(balance) / 10 * (Double.parseDouble(risk) / 100) + Double.parseDouble(open);
                            } catch (NumberFormatException e) {
                                bet = 0;
                            }
                            break;
                        case "Доллар":
                            try {
                                bet = Double.parseDouble(balance) / 7 * (Double.parseDouble(risk) / 100) + Double.parseDouble(open);
                            } catch (NumberFormatException e) {
                                bet = 0;
                            }
                            break;
                        case "Биткоин":
                            try {
                                bet = Double.parseDouble(balance) / 30 * (Double.parseDouble(risk) / 100) + Double.parseDouble(open) / 2;
                            } catch (NumberFormatException e) {
                                bet = 0;
                            }
                            break;
                    }
                    values.setBet(String.valueOf(bet));
                    if (bet == 0) exception = "Введены некорректные значения";
                    choiceStart(message, "Ваши данные:\n" +
                            "Тип расчёта по " + currency + "\n" +
                            "Баланс: " + balance + "\n" +
                            "Риск: " + risk + "\n" +
                            "Вход: " + open + "\n" +
                            "\n" +
                            "Рекомендуемая ставка: " + bet + "\n" + exception.toUpperCase());
                    System.out.println(message.getText());
                    count = 0;
                    session = false;
                    exception = "";
                    values.setOwner(user);
                    valuesRepository.save(values);
                    Date lastDate=new Date();
                    user.setLastTime(String.valueOf(lastDate));
                    userRepository.save(user);
                    break;
            }
        }
    }

    public void choiceStart(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        // Создаем клавиатуру
        ReplyKeyboardMarkup button = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(button);
        button.setSelective(true);
        button.setResizeKeyboard(true);
        button.setOneTimeKeyboard(true);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add("О боте");
        keyboardFirstRow.add("Рассчитать стоимость");
        keyboard.add(keyboardFirstRow);

        button.setKeyboard(keyboard);

        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void choiceСurrency(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        // Создаем клавиатуру
        ReplyKeyboardMarkup button = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(button);
        button.setSelective(true);
        button.setResizeKeyboard(true);
        button.setOneTimeKeyboard(true);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add("RU");
        keyboardFirstRow.add("USD");
        keyboardFirstRow.add("BTC");
        keyboard.add(keyboardFirstRow);

        button.setKeyboard(keyboard);

        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void calculation(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
