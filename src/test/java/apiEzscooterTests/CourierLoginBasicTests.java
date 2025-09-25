package apiEzscooterTests;

import apiEzscooter.ApiCourier;
import io.qameta.allure.*;
import io.restassured.response.Response;
import model.Courier;
import model.CourierCred;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static generator.CourierGenerator.randomCourier;
import static org.hamcrest.Matchers.equalTo;

@Feature("Авторизация курьера")
@DisplayName("Тесты авторизации курьера в системе без его создания")
public class CourierLoginBasicTests {

    private final ApiCourier apiCourier = new ApiCourier();

    @Test
    @DisplayName("Авторизация несуществующего курьера")
    @Description("Проверка ошибки при попытке авторизации под несуществующими учетными данными")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Неуспешная авторизация")
    public void loginNonExistingCourier() {
        Courier courier = randomCourier();
        CourierCred creds = CourierCred.fromCourier(courier);
        Response responselogin = apiCourier.loginCourier(creds);
        responselogin.then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @ParameterizedTest
    @MethodSource("courierDataLogin")
    @DisplayName("Авторизация без обязательных полей")
    @Description("Проверка ошибки при попытке авторизации без заполнения обязательных полей")
    @Severity(SeverityLevel.NORMAL)
    @Story("Неуспешная авторизация")
    void testCannotLoginWithoutRequiredField(String login, String password) {
        CourierCred creds = new CourierCred(login, password);
        Response response = apiCourier.loginCourier(creds);
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    private static Stream<Arguments> courierDataLogin() {
        return Stream.of(
                Arguments.of(null, "123qwery"),
                Arguments.of("NNinja1234", ""));
    }

}