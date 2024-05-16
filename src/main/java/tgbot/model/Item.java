package tgbot.model;

public class Item {
    private String name;
    private String category;
    private String description;
    private String price;
    private int dormitory;
    private String tg;

    public Item(String name, String category, String description, String price, int dormitory, String tg) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.dormitory = dormitory;
        this.tg = tg;
    }


    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public int getDormitory() {
        return dormitory;
    }

    public String getTg() {
        return tg;
    }
}
