package wb.check.price.bot;

import wb.check.price.bot.dto.AnswerDTO;
import wb.check.price.bot.dto.MessageDTO;
import wb.check.price.bot.repositories.User;
import wb.check.price.bot.services.AnswerService;
import wb.check.price.bot.services.PriceBotAnswerService;
import wb.check.price.bot.services.ProductService;
import wb.check.price.bot.services.UserService;
import wb.check.price.bot.utils.MessageDtoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@EnableScheduling
@Component
public class WbCheckPriceBot extends TelegramLongPollingBot {

    private final String botUserName;
    private final String botToken;
    private final UserService userService;
    private final ProductService productService;
    private final AnswerService answerService;

    WbCheckPriceBot(@Value("${bot.name}") String botUserName,
                    @Value("${bot.token}") String botToken,
                    UserService userService,
                    ProductService productService,
                    PriceBotAnswerService priceBotAnswerService) {
        this.botUserName = botUserName;
        this.botToken = botToken;
        this.userService = userService;
        this.productService = productService;
        this.answerService = priceBotAnswerService;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {

        MessageDTO dto = MessageDtoUtil.get(update);
        log.info(dto.toString());

        User user = userService.get(dto.getUserId());
        user.setId(dto.getUserId());
        user.setUserName(dto.getUserName());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        userService.save(user);

        sendAnswers(answerService.getAnswer(dto));
    }

    public void sendAnswers(AnswerDTO dto) {
        dto.getDeleteMessages().forEach(this::sendTheDeleteMessage);
        dto.getMessages().forEach(this::sendTheMessage);
        dto.getEditMessages().forEach(this::sendTheEditMessage);
        dto.getPhotoMessages().forEach(this::sendThePhoto);
        dto.getVideoMessages().forEach(this::sendTheVideo);
        dto.getEditMessageMedia().forEach(this::sendTheMedia);
        dto.getEditMessageCaptions().forEach(this::sendEditMessage);
        dto.getCallbackQueries().forEach(this::sendTheCallbackQuery);
        dto.getSendDocuments().forEach(this::sendTheDocument);
        dto.getSendLocations().forEach(this::sendTheLocation);
    }

    public void sendTheMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendTheEditMessage(EditMessageText editMessageText) {
        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendEditMessage(EditMessageCaption message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendThePhoto(SendPhoto sendPhoto) {
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendTheVideo(SendVideo sendVideo) {
        try {
            execute(sendVideo);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendTheDocument(SendDocument sendDocument) {
        try {
            execute(sendDocument);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendTheMedia(EditMessageMedia editMessageMedia) {
        try {
            execute(editMessageMedia);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendTheCallbackQuery(AnswerCallbackQuery callbackQuery) {
        try {
            execute(callbackQuery);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendTheLocation(SendLocation sendLocation) {
        try {
            execute(sendLocation);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    public void sendTheDeleteMessage(DeleteMessage deleteMessage) {
        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
