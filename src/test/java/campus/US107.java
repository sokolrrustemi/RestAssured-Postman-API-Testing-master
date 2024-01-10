package campus;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;

public class US107 {
    Faker randomGenerator = new Faker();
    String studentGroupId;
    RequestSpecification requestSpec;
    String rndStudentGroupName;
    String rndDescription;
    Boolean rndPublicGroup;
    Boolean rndShowToStudent;
    String schoolId = "646cbb07acf2ee0d37c6d984";
    Map<String, String> StudentGroup;


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
    public void deleteStudent(){
        rndStudentGroupName = randomGenerator.job().field() + randomGenerator.number().digits(5);
        rndDescription = randomGenerator.job().position();
        rndPublicGroup = randomGenerator.bool().bool();
        rndShowToStudent = randomGenerator.bool().bool();

        StudentGroup = new HashMap<>();
        StudentGroup.put("name", rndStudentGroupName);
        StudentGroup.put("description", rndDescription);
        StudentGroup.put("publicGroup", String.valueOf(rndPublicGroup));
        StudentGroup.put("showToStudent", String.valueOf(rndShowToStudent));
        StudentGroup.put("schoolId", schoolId);

        studentGroupId =
                given()

                        .spec(requestSpec)
                        .body(StudentGroup)

                        .when()
                        .post("school-service/api/student-group/658f53ffcacea97f2a0cb09d/remove-students?page=0&size=10")

                        .then()
                        .log().body()

                        .statusCode(400)
                        .extract().path("id");
        Response body =
                given()
                        .when()
                        .get("school-service/api/student-group/658f53ffcacea97f2a0cb09d/remove-students?page=0&size=10")

                        .then()
                        .extract().response();



    }





}
