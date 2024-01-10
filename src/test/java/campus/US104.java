package campus;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

public class US104 {
    Faker faker = new Faker();
    String schoolId = "646cbb07acf2ee0d37c6d984";
    String orderNo = "13";
    String columnSize = "2";
    RequestSpecification requestSpecification;
    String customId;

    @BeforeClass
    public void login() {
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
                        .statusCode(200)
                        .extract().response().getDetailedCookies();

        requestSpecification = new RequestSpecBuilder()
                .addCookies(cookies)
                .setContentType(ContentType.JSON)
                .build();
    }

    String customName = faker.name().firstName();

    @Test
    public void createCustomFieldGroup() {
        Map<String, String> custom = new HashMap<>();
        custom.put("name", customName);
        custom.put("orderNo", orderNo);
        custom.put("columnSize", columnSize);
        custom.put("schoolId", schoolId);
        customId =
                given()
                        .spec(requestSpecification)
                        .body(custom)
                        .when()
                        .post("school-service/api/custom-field-groups")
                        .then()
                        .statusCode(201)
                        .extract().path("id");
    }

    @Test
    public void createCustomFieldGroupNegative() {
        Map<String, String> custom = new HashMap<>();
        custom.put("name", customName);
        custom.put("orderNo", orderNo);
        custom.put("columnSize", columnSize);
        custom.put("schoolId", schoolId);
        given()
                .spec(requestSpecification)
                .body(custom)
                .when()
                .post("school-service/api/custom-field-groups")
                .then()
                .statusCode(400);
    }
    @Test(dependsOnMethods = "createCustomFieldGroup")
    public void editCustomFieldGroup(){
        String newName = faker.name().fullName();
        Map<String, String> custom = new HashMap<>();
        custom.put("id",customId);
        custom.put("name", newName);
        custom.put("orderNo", orderNo);
        custom.put("columnSize", columnSize);
        custom.put("schoolId", schoolId);
        given()
                .spec(requestSpecification)
                .body(custom)
                .when()
                .put("school-service/api/custom-field-groups")
                .then()
                .statusCode(200);
    }
    @Test(dependsOnMethods = "editCustomFieldGroup")
    public void deleteCustomFieldGroup(){
        given()
                .spec(requestSpecification)
                .when()
                .delete("school-service/api/custom-field-groups/"+customId)
                .then()
                .statusCode(200);
    }

    @Test(dependsOnMethods = "deleteCustomFieldGroup")
    public void deleteCustomFieldGroupNegative(){
        given()
                .spec(requestSpecification)
                .when()
                .delete("school-service/api/custom-field-groups/"+customId)
                .then()
                .statusCode(400);
    }

}
