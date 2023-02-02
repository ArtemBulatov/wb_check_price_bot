package wb.check.price.bot.dto;

import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.util.ArrayList;
import java.util.List;

@Data
public class MessageDTO {
    private int messageId = 0;
    private long chatId = 0;
    private String text = "";
    private String callbackQueryId = "";

    private long userId = 0;
    private String userName = "";
    private String firstName = "";
    private String lastName = "";

    private List<PhotoSize> photoSizeList = new ArrayList<>();
}
