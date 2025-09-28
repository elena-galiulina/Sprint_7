package apiEzscooterTests;

import apiEzscooter.ApiOrder;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import model.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static generator.OrderGenerator.randomOrder;
import static org.hamcrest.Matchers.*;

@Feature("Управление заказами")
@DisplayName("Тесты API для работы с заказами")
public class OrderTests {
    private final ApiOrder apiOrder = new ApiOrder();

    @ParameterizedTest
    @MethodSource("courierDataLogin")
    @DisplayName("Создание заказа с разными комбинациями цветов")
    @Description("Проверка создания заказа с валидными данными и разными вариантами цветов")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Создание нового заказа")
    void testCreateOrderWithColors(List<String> colors) {
        Order order = randomOrder();
        order.setDeliveryDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        order.setColor(colors);
        Response response = apiOrder.createOrder(order);
        response.then()
                .statusCode(201)
                .body("track", notNullValue());
    }

    private static Stream<List<String>> courierDataLogin() {
        return Stream.of(
                List.of("BLACK", "GREY"),
                List.of("GREY"),
                List.of("BLACK"),
                List.of());
    }

    @Test
    @DisplayName("Получение списка заказов")
    @Description("Проверка, что запрос списка заказов возвращает непустой список")
    @Severity(SeverityLevel.NORMAL)
    @Story("Получение списка заказов")
    public void testGetOrdersListReturnsOrders() {
        Order order = randomOrder();
        order.setDeliveryDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        Response response = apiOrder.getOrdersList();
        response.then()
                .statusCode(200)
                .body("orders", not(empty()));
    }
}