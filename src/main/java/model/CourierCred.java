package model;

import io.qameta.allure.internal.shadowed.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourierCred {

    private String login;
    private String password;

    public CourierCred(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public static CourierCred fromCourier(Courier courier) {
        return new CourierCred(courier.getLogin(), courier.getPassword());
    }
}
