package campus;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class US106 {
    RequestSpecification requestSpec;
    String studentID = "657711ca8af7ce488ac6a62d";
    List<String> payloadList = new ArrayList<>();

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
    public void addNewStudentIntoTheStudentGroup() {

        payloadList.add(studentID);

        given()

                .spec(requestSpec)
                .body(payloadList)

                .when()
                .post("school-service/api/student-group/658f53ffcacea97f2a0cb09d/add-students?page=0&size=10")

                .then()
                .contentType(ContentType.JSON)
                .log().body()

        .statusCode(200)

                        .body("content[0].id", instanceOf(String.class))
                        .body("content[0].firstName", instanceOf(String.class))
                        .body("content[0].lastName", instanceOf(String.class))

                        .body("content[0].id", notNullValue())
                        .body("content[0].firstName", notNullValue())
                        .body("content[0].lastName", notNullValue())
        ;
        System.out.println("studentID = " + studentID);
    }

    @Test (dependsOnMethods ="addNewStudentIntoTheStudentGroup")
    public void removeStudentFromTheStudentGroup() {

        payloadList.add(studentID);

        given()

                .spec(requestSpec)
                .body(payloadList)

                .when()
                .post("school-service/api/student-group/658f53ffcacea97f2a0cb09d/add-students?page=0&size=10")

                .then()
                .contentType(ContentType.JSON)
                .log().body()

                .statusCode(200)
        ;
        System.out.println("studentID = " + studentID);
    }
}
