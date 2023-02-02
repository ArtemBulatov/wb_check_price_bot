package wb.check.price.bot.dto;

import lombok.Data;

import java.util.List;

@Data
public class WbCardDTO {
    private CardData data;

    @Data
    public static class CardData {
        private List<ProductDTO> products;
    }

    @Data
    public static class ProductDTO {
        private long id;
        private String name;
        private int priceU;
        private int salePriceU;

        private String brand;
        private String brandId;
        private String siteBrandId;

        private String root;
        private String kindId;
        private String subjectId;
        private String subjectParentId;
        private String supplierId;
        private String sale;
        private int logisticsCost;

        public int getRealPrice() {
            return salePriceU != 0 ? salePriceU : priceU;
        }
    }
}
