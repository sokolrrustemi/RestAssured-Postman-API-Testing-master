package campus;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.*;

import io.restassured.specification.RequestSpecification;
import org.testng.annotations.*;

import java.util.*;

import static io.restassured.RestAssured.*;

import static org.hamcrest.Matchers.*;

public class US101 {
    Faker randomGenerator = new Faker();
    RequestSpecification requestSpec;
    String countryId = "5baac28d91cefe05fc6e3fe6";
    String stateId;

    Map<String, Object> countryObject;
    Map<String, Object> newState;
    Map<String, Object> updatedState;

    @BeforeClass
    public void Login() {
        baseURI = "https://test.mersys.io/";

        Map<String, String> userCredential = new HashMap<>();
        userCredential.put("username", "turkeyts");
        userCredential.put("password", "TechnoStudy123");
        userCredential.put("rememberMe", "true");

        Cookies cookies =
                given()
                        .body(userCredential)
                        .contentType(ContentType.JSON)

                        .when()
                        .post("/auth/login")

                        .then()
                        .contentType(ContentType.JSON)
                        .log().body()

                        .statusCode(200)

                        .body("access_token", instanceOf(String.class))
                        .body("token_type", instanceOf(String.class))
                        .body("refresh_token", instanceOf(String.class))
                        .body("expires_in", instanceOf(Integer.class))
                        .body("scope", instanceOf(String.class))
                        .body("passwordChange", instanceOf(Boolean.class))
                        .body("username", instanceOf(String.class))
                        .body("iat", instanceOf(Integer.class))
                        .body("jti", instanceOf(String.class))
                        .body("is_2fa_enabled", instanceOf(Boolean.class))

                        .body("access_token", notNullValue())
                        .body("token_type", notNullValue())
                        .body("refresh_token", notNullValue())
                        .body("expires_in", greaterThan(0))
                        .body("scope", notNullValue())
                        .body("passwordChange", notNullValue())
                        .body("username", notNullValue())
                        .body("iat", notNullValue())
                        .body("jti", notNullValue())
                        .body("is_2fa_enabled", notNullValue())

                        .extract().response().detailedCookies();

        requestSpec = new RequestSpecBuilder()
                .addCookies(cookies)
                .setContentType(ContentType.JSON)
                .build()
        ;
    }

    @Test
    public void stateList() {
        Map<String, String> countryList = new HashMap<>();
        countryList.put("name", "");
        countryList.put("countryId", countryId);

        given()

                .spec(requestSpec)
                .body(countryList)

                .when()
                .post("school-service/api/states/search/")

                .then()
                .contentType(ContentType.JSON)
                .log().body()

                .statusCode(200)
                .time(lessThan(1000L))

                .body("[0].id", instanceOf(String.class))
                .body("[0].country.id", instanceOf(String.class))

                .body("[0].id", notNullValue())
                .body("[0].country.id", notNullValue())

        ;
    }
    @Test
    public void createNewState() {
        String rndName = randomGenerator.country().name() + randomGenerator.number().digits(5);
        String rndShortName = randomGenerator.country().countryCode3();

        countryObject = new HashMap<>();
        countryObject.put("id", "63a41a0dcb75ee5c2199a8bc");

        //Object[] translateName = new Object[1]; Bu da translateName için kullanılabilir.

        newState = new HashMap<>();
        newState.put("name", rndName);
        newState.put("shortName", rndShortName);
        newState.put("country", countryObject);
        newState.put("translateName", new ArrayList<>());

        stateId =
                given()

                        .spec(requestSpec)
                        .body(newState)

                        .when()
                        .post("school-service/api/states/")

                        .then()
                        .contentType(ContentType.JSON)
                        .log().body()

                        .statusCode(201)

                 .body("id", instanceOf(String.class))
                 .body("country.id", instanceOf(String.class))
                 .body("name", instanceOf(String.class))
                 .body("shortName", instanceOf(String.class))

                 .body("id", notNullValue())
                 .body("country.id", notNullValue())
                 .body("name", notNullValue())
                 .body("shortName", notNullValue())

                        .extract().path("id")
        ;
        System.out.println("createdStateId = " + stateId);
}

@Test(dependsOnMethods = "createNewState")
    public void createNewStateNegative(){
        given()
                .spec(requestSpec)
                .body(newState)
                .when()
                .post("school-service/api/states/")

                .then()
                .contentType(ContentType.JSON)
                .statusCode(400)
                .body("message", containsString(" already exists."))
                .log().body()

                .body("type", instanceOf(String.class))
                .body("status", instanceOf(Integer.class))
                .body("path", instanceOf(String.class))
                .body("message", instanceOf(String.class))

                .body("type", notNullValue())
                .body("status", notNullValue())
                .body("path", notNullValue())
                .body("message", notNullValue())
        ;
    System.out.println("coudntCreateStateId = " + stateId);
    }

    @Test(dependsOnMethods = "createNewStateNegative")
    public void updateState(){
        String updatedName = randomGenerator.country().name() + randomGenerator.number().digits(5);
        String updatedShortName = randomGenerator.country().countryCode3();

        countryObject = new HashMap<>();
        countryObject.put("id", "63a41a0dcb75ee5c2199a8bc");

        //Object[] translateName = new Object[1]; Bu da translateName için kullanılabilir.

        updatedState = new HashMap<>();
        updatedState.put("id", stateId);
        updatedState.put("name", updatedName);
        updatedState.put("shortName", updatedShortName);
        updatedState.put("country", countryObject);
        updatedState.put("translateName", new ArrayList<>());

        given()
                .spec(requestSpec)
                .body(updatedState)

                .when()
                .put("school-service/api/states/")

                .then()
                .contentType(ContentType.JSON)
                .statusCode(200)
                .log().body()

                .body("id", instanceOf(String.class))
                .body("country.id", instanceOf(String.class))
                .body("name", instanceOf(String.class))
                .body("shortName", instanceOf(String.class))

                .body("id", notNullValue())
                .body("country.id", notNullValue())
                .body("name", notNullValue())
                .body("shortName", notNullValue())
        ;
        System.out.println("updatedStateId = " + stateId);
                ;
    }

    @Test(dependsOnMethods = "updateState")
    public void deleteState(){
        given()
                .spec(requestSpec)

                .when()
                .delete("school-service/api/states/" + stateId)

                .then()
                .log().body()
                .statusCode(200)

        ;
        System.out.println("deletedStateId = " + stateId);
    }

    @Test(dependsOnMethods = "deleteState")
    public void deleteStateNegative(){
        given()
                .spec(requestSpec)

                .when()
                .delete("school-service/api/states/" + stateId)

                .then()
                .log().body()
                .statusCode(200)
        ;
        System.out.println("secondTimeDeletedStateId = " + stateId);
    }
}
