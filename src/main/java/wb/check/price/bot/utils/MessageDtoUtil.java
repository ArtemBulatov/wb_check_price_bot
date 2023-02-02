package wb.check.price.bot.utils;

import wb.check.price.bot.dto.MessageDTO;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class MessageDtoUtil {

    public static MessageDTO get(Update update) {
        MessageDTO messageDTO = new MessageDTO();

        if (update.hasMessage()) {
            messageDTO.setChatId(update.getMessage().getChatId());
            if (update.getMessage().getPhoto() != null) {
                messageDTO.setPhotoSizeList(update.getMessage().getPhoto());
            }
            if (update.getMessage().getText() != null) {
                messageDTO.setText(update.getMessage().getText());
            }

            messageDTO.setUserId(update.getMessage().getFrom().getId());
            messageDTO.setUserName(update.getMessage().getFrom().getUserName());
            messageDTO.setFirstName(update.getMessage().getFrom().getFirstName());
            messageDTO.setLastName(update.getMessage().getFrom().getLastName());
        }
        else if (update.hasCallbackQuery()) {
            messageDTO.setChatId(update.getCallbackQuery().getFrom().getId());
            messageDTO.setCallbackQueryId(update.getCallbackQuery().getId());
            messageDTO.setText(update.getCallbackQuery().getData());
            messageDTO.setMessageId(update.getCallbackQuery().getMessage().getMessageId());

            messageDTO.setUserId(update.getCallbackQuery().getFrom().getId());
            messageDTO.setUserName(update.getCallbackQuery().getFrom().getUserName());
            messageDTO.setFirstName(update.getCallbackQuery().getFrom().getFirstName());
            messageDTO.setLastName(update.getCallbackQuery().getFrom().getLastName());
        }
        return messageDTO;
    }
}
