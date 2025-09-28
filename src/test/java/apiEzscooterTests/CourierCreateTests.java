package apiEzscooterTests;

import apiEzscooter.ApiCourier;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import model.CourierCredsByIdResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import io.restassured.response.Response;
import model.Courier;
import model.CourierCreateResponseSucces;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static generator.CourierGenerator.randomCourier;
import static model.CourierCred.fromCourier;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Feature("Создание курьера")
@DisplayName("Тесты создания курьера в системе")
public class CourierCreateTests {
    private final ApiCourier apiCourier = new ApiCourier();
    private Courier courier;

    @BeforeEach
    void setUpCourier(){
        courier = randomCourier();
    }

    @Test
    @DisplayName("Успешное создание курьера")
    @Description("Проверка успешного создания курьера с валидными данными")
    @Severity(SeverityLevel.BLOCKER)
    @Story("Создание нового курьера")
    public void testCreateCourier() {
        Response response = apiCourier.createCourier(courier);
        assertEquals(SC_CREATED, response.statusCode(), "Неверный статус-код");
    }

    @Test
    @DisplayName("Создание дубликата курьера")
    @Description("Проверка ошибки при попытке создания курьера с уже существующим логином")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Обработка дубликатов курьеров")
    public void testCannotCreateDuplicateCourier() {
        Response response = apiCourier.createCourier(courier);
        assertEquals(SC_CREATED, response.statusCode(), "Неверный статус-код, должен быть 201");
        Response dublicateResponse = apiCourier.createCourier(courier);
        dublicateResponse.then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @ParameterizedTest
    @MethodSource("courierData")
    @DisplayName("Создание курьера без обязательных полей")
    @Description("Проверка ошибки при создании курьера без заполнения обязательных полей")
    @Severity(SeverityLevel.NORMAL)
    @Story("Валидация данных курьера")
    void testCannotCreateWithoutRequiredField(String login, String password, String firstName) {
        Courier courier = new Courier();
        courier.setLogin(login);
        courier.setPassword(password);
        courier.setFirstName(firstName);
        Response response = apiCourier.createCourier(courier);
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    private static Stream<Arguments> courierData() {
        return Stream.of(
                Arguments.of(null, "123qwery", "firstNameExample1"),
                Arguments.of("loginExampleEG1", null, "firstNameExample2"));
    }

    @Test
    @DisplayName("Проверка успешного ответа при создании курьера")
    @Description("Проверка, что ответ при успешном создании курьера содержит поле ok=true")
    @Severity(SeverityLevel.MINOR)
    @Story("Создание нового курьера")
    public void createCourierResponseOk() {
        Response response = apiCourier.createCourier(courier);
        String ok = response.as(CourierCreateResponseSucces.class).getOk();
        assertEquals("true", ok, "Неверный ok");
    }

    @AfterEach
    @DisplayName("Очистка после теста")
    @Description("Удаление созданного курьера после выполнения каждого теста")
    public void tearDown() {
        Response loginResponse = apiCourier.loginCourier(fromCourier(courier));
        String id = loginResponse.as(CourierCredsByIdResponse.class).getId();
        apiCourier.delete(id);
    }
}