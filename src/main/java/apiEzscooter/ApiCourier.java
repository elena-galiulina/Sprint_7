package apiEzscooter;
import model.Courier;
import model.CourierCred;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
public class ApiCourier {
    private static final String API_V1_COURIER_CREATE = "/api/v1/courier";
    private static final String API_V1_COURIER_LOGIN = "/api/v1/courier/login";

    public ApiCourier() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
    }

    public Response createCourier(Courier courier) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(courier)
                .when()
                .post(API_V1_COURIER_CREATE);
    }

    public Response loginCourier(CourierCred creds) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(creds)
                .when()
                .post(API_V1_COURIER_LOGIN);
    }

    public Response delete(String id) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .delete(API_V1_COURIER_CREATE + "/" + id);
    }

}
