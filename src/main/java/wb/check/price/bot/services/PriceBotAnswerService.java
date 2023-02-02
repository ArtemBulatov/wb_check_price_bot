package wb.check.price.bot.services;

import wb.check.price.bot.connectors.WbConnector;
import wb.check.price.bot.dto.AnswerDTO;
import wb.check.price.bot.dto.InlineButtonDTO;
import wb.check.price.bot.dto.MessageDTO;
import wb.check.price.bot.dto.WbCardDTO;
import wb.check.price.bot.repositories.Product;
import wb.check.price.bot.repositories.User;
import wb.check.price.bot.utils.ReservedCharacters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PriceBotAnswerService implements AnswerService {
    private static final String ENTER_DISCOUNT = "Введите Вашу скидку (число от 1 до 100)";

    private final UserService userService;
    private final ProductService productService;
    private final WbConnector wbConnector;
    private final LastAnswerService lastAnswerService;

    public PriceBotAnswerService(UserService userService,
                                 ProductService productService,
                                 WbConnector wbConnector,
                                 LastAnswerService lastAnswerService) {
        this.userService = userService;
        this.productService = productService;
        this.wbConnector = wbConnector;
        this.lastAnswerService = lastAnswerService;
    }


    @Override
    public AnswerDTO getAnswer(MessageDTO dto) {
        AnswerDTO answer = new AnswerDTO();

        if (dto.getText().matches("https://(www.)?wildberries.ru/catalog/\\d+/detail.aspx\\.*")) {
            String url = dto.getText().contains("www") ? "https://www.wildberries.ru/catalog/" : "https://wildberries.ru/catalog/";
            long productWbId = Long.parseLong(dto.getText().replace(url, "")
                    .split("/detail.aspx")[0]);

            WbCardDTO cardDTO = wbConnector.getProductFromWb(productWbId);
            if (cardDTO == null) {
                answer.addAnswer(getSendMessage("К сожалению, не удалось добавить Ваш товар", dto.getChatId()));
                return answer;
            }
            Product product = new Product();
            product.setWbId(productWbId);
            product.setUserId(dto.getUserId());
            product.setName(cardDTO.getData().getProducts().get(0).getName());
            product.setPrice(cardDTO.getData().getProducts().get(0).getRealPrice());
            product = productService.save(product);

            User user = userService.get(dto.getUserId());

            String text = "Товар *" + ReservedCharacters.replace(product.getName())
                    + "* добавлен для отслеживания цены\\. "
                    + "\n\uD83D\uDCB5 Текущая цена: *" + product.getPrice()/100 + "* руб\\."
                    + "\n\uD83D\uDCB8 с учётом Вашей скидки " + user.getDiscount() + "% "
                    + "*" + (product.getPrice()/100 - product.getPrice()/100 * user.getDiscount()/100) + "* руб\\.";

            answer.addAnswer(getSendMessageWithUnsubscribeButton(dto.getChatId(), text, product.getId()));
        }
        else if (dto.getText().contains("unsubscribe:")) {
            UUID productId = UUID.fromString(dto.getText().split(":", 2)[1]);
            productService.delete(productId);
            answer.addAnswer(getSendMessage("Вы успешно отписались от отслеживания этого товара", dto.getChatId()));
        }
        else if (dto.getText().equals("/start")){
            lastAnswerService.clearAnswers(dto.getChatId());
            answer.addAnswer(getSendMessage("Добро пожаловать! \uD83D\uDC4B" +
                    "\nОтправьте мне ссылку на интересующий Вас товар из Wildberries, " +
                    "и при изменении цены я обязательно сообщу \uD83D\uDE0A", dto.getChatId()));
        }
        else if (dto.getText().equals("/unsubscribe")) {
            lastAnswerService.clearAnswers(dto.getChatId());
            productService.deleteAllByUserId(dto.getUserId());
            answer.addAnswer(getSendMessage("Вы успешно отписались от всех товаров!", dto.getChatId()));
        }
        else if (dto.getText().equals("/my_discount")) {
            lastAnswerService.clearAnswers(dto.getChatId());
            User user = userService.get(dto.getUserId());
            answer.addAnswer(getDiscountMessage(user));
        }
        else if (dto.getText().equals("change_discount")) {
            answer.addAnswer(getSendMessage(ENTER_DISCOUNT, dto.getChatId()));
            lastAnswerService.addLastAnswer(ENTER_DISCOUNT, dto.getChatId());
        }
        else if (lastAnswerService.readLastAnswer(dto.getChatId()).equals(ENTER_DISCOUNT)) {
            if (dto.getText().matches("\\d*")) {
                int discount = Integer.parseInt(dto.getText());
                if (discount >= 0 && discount <= 100) {
                    lastAnswerService.deleteLastAnswer(dto.getChatId());
                    User user = userService.get(dto.getUserId());
                    user.setDiscount(discount);
                    userService.save(user);
                    answer.addAnswer(getDiscountMessage(user));
                }
                else {
                    answer.addAnswer(getSendMessage("Скидка должны быть от 1 до 100", dto.getChatId()));
                }
            }
            else {
                answer.addAnswer(getSendMessage("Введите число от 1 до 100", dto.getChatId()));
            }
        }
        else {
            answer.addAnswer(getSendMessage("Похоже, Вы неверно отправили ссылку. Нужна только ссылка на конкретный товар.", dto.getChatId()));
        }

        return answer;
    }

    private SendMessage getSendMessageWithUnsubscribeButton(long chatId, String text, UUID productId) {
        List<InlineButtonDTO> buttonDTOList = new ArrayList<>();
        buttonDTOList.add(new InlineButtonDTO("Отписаться от товара", "unsubscribe:"  + productId));
        return getSendMessageWithInlineButtons(text, buttonDTOList, 1, true, chatId);
    }

    private SendMessage getDiscountMessage(User user) {
        String message = "\uD83D\uDCB8 Ваша индивидуальная скидка *" + user.getDiscount() + "%*" +
                "\n\n\\* свою скидку можно посмотреть в личном кабинете WB в разделе \"Скидка покупателя\"";

        List<InlineButtonDTO> buttonDTOList = new ArrayList<>();
        buttonDTOList.add(new InlineButtonDTO("Изменить скидку", "change_discount"));
        return getSendMessageWithInlineButtons(message, buttonDTOList, 1, true, user.getId());
    }
}
