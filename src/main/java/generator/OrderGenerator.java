package generator;

import model.Order;

import java.util.Random;

import static utils.Utils.randomString;

public class OrderGenerator {
    public static Order randomOrder() {
        Random random = new Random();
        int minValue = 1;
        int maxValue = 7;
        int rentTime = minValue + random.nextInt(maxValue - minValue + 1);
        return new Order()
                .setFirstName(randomString())
                .setLastName(randomString())
                .setAddress(randomString())
                .setMetroStation(randomString())
                .setPhone(randomString())
                .setRentTime(rentTime)
                .setComment(randomString());
    }
}
