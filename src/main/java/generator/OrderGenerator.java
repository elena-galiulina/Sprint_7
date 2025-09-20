package generator;

import model.Order;

import java.util.Random;

import static utils.Utils.randomString;

public class OrderGenerator {
    public static Order randomOrder() {
        Random random = new Random();
        return new Order()
                .setFirstName(randomString())
                .setLastName(randomString())
                .setAddress(randomString())
                .setMetroStation(randomString())
                .setPhone(randomString())
                .setRentTime(random.nextInt(1,7))
                .setComment(randomString());
    }
}
