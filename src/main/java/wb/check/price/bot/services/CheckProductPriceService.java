package wb.check.price.bot.services;

import wb.check.price.bot.WbCheckPriceBot;
import wb.check.price.bot.connectors.WbConnector;
import wb.check.price.bot.dto.WbCardDTO;
import wb.check.price.bot.repositories.Product;
import wb.check.price.bot.repositories.User;
import wb.check.price.bot.utils.ButtonsUtil;
import wb.check.price.bot.utils.ReservedCharacters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class CheckProductPriceService {
    private final WbCheckPriceBot bot;
    private final UserService userService;
    private final ProductService productService;
    private final WbConnector wbConnector;

    public CheckProductPriceService(WbCheckPriceBot bot, UserService userService, ProductService productService, WbConnector wbConnector) {
        this.bot = bot;
        this.userService = userService;
        this.productService = productService;
        this.wbConnector = wbConnector;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    private void checkPrice() {
        productService.getAll().forEach(product -> {
            WbCardDTO dto = wbConnector.getProductFromWb(product.getWbId());
            if (dto != null) {
                int oldPrice = product.getPrice();
                int newPrice = dto.getData().getProducts().get(0).getRealPrice();
                product.setPrice(newPrice);
                product.setName(dto.getData().getProducts().get(0).getName());
                productService.save(product);

                log.info("oldPrice " + oldPrice + "    newPrice: " + newPrice);
                if (newPrice < oldPrice) {
                    sendProductInfo(product.getUserId(), getPriceInfo(product, oldPrice), product.getId());
                }
            }
            randomSleep();
        });
    }

    private String getPriceInfo(Product product, int oldPrice) {
        User user = userService.get(product.getUserId());

        String price = "\n\uD83D\uDCB5 ";
        String discountPrice = "\n\uD83D\uDCB8 с учётом Вашей скидки " + user.getDiscount() + "% ";
        if (oldPrice != 0) {
            price = price + " ~" + oldPrice/100 + "~ ";
            discountPrice = discountPrice + " ~" + (oldPrice/100 - oldPrice/100 * user.getDiscount()/100) + "~ ";
        }
        price = price + " *" + product.getPrice()/100 + "* руб";
        discountPrice = discountPrice+ " *" + (product.getPrice()/100 - product.getPrice()/100 * user.getDiscount()/100) + "* руб";
        return "❗ Изменилась цена"
                + "\n\n\uD83D\uDC49 *" + ReservedCharacters.replace(product.getName()) + "* "
                + "\n" + price
                + discountPrice
                + ReservedCharacters.replace("\n\n https://www.wildberries.ru/catalog/" + product.getWbId() + "/detail.aspx");
    }

    private void sendProductInfo(long chatId, String text, UUID productId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.enableMarkdownV2(true);
        sendMessage.setReplyMarkup(ButtonsUtil.getUnsubscribeButton(productId));
        bot.sendTheMessage(sendMessage);
    }

    private void randomSleep() {
        try {
            int randomNum = ThreadLocalRandom.current().nextInt(1, 3);
            Thread.sleep(1000L * randomNum);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
