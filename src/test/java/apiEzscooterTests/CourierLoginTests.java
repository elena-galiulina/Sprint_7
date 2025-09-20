package apiEzscooterTests;

import apiEzscooter.ApiCourier;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import io.restassured.response.Response;
import model.Courier;
import model.CourierCreateResponseSucces;
import model.CourierCred;
import model.CourierCredsByIdResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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

@Feature("Авторизация курьера")
@DisplayName("Тесты авторизации курьера в системе")
public class CourierLoginTests {

    private ApiCourier apiCourier = new ApiCourier();
    private Courier courier;
    private String id;

    @Test
    @DisplayName("Авторизация несуществующего курьера")
    @Description("Проверка ошибки при попытке авторизации под несуществующими учетными данными")
    @Severity(SeverityLevel.CRITICAL)
    @Story("Неуспешная авторизация")
    public void loginNonExistingCourier() {
        courier = randomCourier();
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

    @Nested
    @DisplayName("Тесты авторизации с предварительным созданием курьера")
    @Feature("Авторизация существующего курьера")
    public class NestedCourierLoginTests {

        @BeforeEach
        @DisplayName("Подготовка: создание тестового курьера")
        @Description("Создание курьера перед каждым тестом для проверки авторизации")
        public void setUpCourier() {
            courier = randomCourier();
            Response response = apiCourier.createCourier(courier);
            assertEquals(SC_CREATED, response.statusCode(), "Неверный статус-код");
        }

        @Test
        @DisplayName("Успешная авторизация курьера")
        @Description("Проверка успешной авторизации с валидными учетными данными")
        @Severity(SeverityLevel.BLOCKER)
        @Story("Успешная авторизация")
        public void loginCourierSuccses() {
            CourierCred creds = CourierCred.fromCourier(courier);
            Response responselogin = apiCourier.loginCourier(creds);
            assertEquals(SC_OK, responselogin.statusCode(), "Неверный статус-код");
        }

        @Test
        @DisplayName("Неуспешная авторизация с неверными данными")
        @Description("Проверка ошибок авторизации с неверным логином и паролем")
        @Severity(SeverityLevel.NORMAL)
        @Story("Неуспешная авторизация")
        public void loginCourierUnSuccses() {
            CourierCred credsWhithWrongLogin = new CourierCred("blabla", courier.getPassword());
            Response responseloginFirst = apiCourier.loginCourier(credsWhithWrongLogin);
            responseloginFirst.then()
                    .statusCode(404)
                    .body("message", equalTo("Учетная запись не найдена"));

            CourierCred credsWhithWrongPassword = new CourierCred(courier.getLogin(), "wrongPassword");
            Response responseloginSecond = apiCourier.loginCourier(credsWhithWrongPassword);
            responseloginSecond.then()
                    .statusCode(404)
                    .body("message", equalTo("Учетная запись не найдена"));
        }

        @Test
        @DisplayName("Получение ID курьера при авторизации")
        @Description("Проверка, что ответ при успешной авторизации содержит ID курьера")
        @Severity(SeverityLevel.CRITICAL)
        @Story("Успешная авторизация")
        public void loginCourierResponseHasId() {
            Response loginResponse = apiCourier.loginCourier(fromCourier(courier));
            id = loginResponse.as(CourierCredsByIdResponse.class).getId();
        }

        @AfterEach
        @DisplayName("Очистка: удаление тестового курьера")
        @Description("Удаление созданного курьера после каждого теста")
        public void tearDown() {
            apiCourier.delete(id);
        }
    }
}