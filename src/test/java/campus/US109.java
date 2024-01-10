package campus;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class US109 {

    Faker faker = new Faker();
    RequestSpecification requestSpecification;
    String schoolID = "6576fd8f8af7ce488ac69b89";
    String schemeID;
    String randomGradingSchemeName;


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
                        .extract().response().getDetailedCookies();

        requestSpecification = new RequestSpecBuilder()
                .addCookies(cookies)
                .setContentType(ContentType.JSON)
                .build();


    }

    @Test
    public void getGradingSchemeList() {


        given()

                .spec(requestSpecification)
                .contentType(ContentType.JSON)

                .when()
                .get("/school-service/api/grading-schemes/school/6576fd8f8af7ce488ac69b89/search")

                .then()
                .log().body()
                .statusCode(200)
                .assertThat()
                .body("id", everyItem(notNullValue()))
                .body("name", everyItem(isA(String.class)))
                .body("name", everyItem(not(emptyOrNullString())))
                .body("active", everyItem(isA(Boolean.class)))
                .body("schoolId", everyItem(isA(String.class)))
                .body("schoolId", everyItem(not(emptyOrNullString())))
                .body("gradeRanges", everyItem(isA(List.class)))
                .body("type", everyItem(isA(String.class)))
                .body("type", not(emptyOrNullString()))
                .body("enablePoint", everyItem(isA(Boolean.class)))
        ;

    }

    @Test(dependsOnMethods = "getGradingSchemeList")
    public void createGradingScheme() {

        randomGradingSchemeName = faker.book().publisher();

        Map<String, Object> gradingScheme = new HashMap<>();
        gradingScheme.put("name", randomGradingSchemeName);
        gradingScheme.put("type", "POINT");
        gradingScheme.put("active", "true");
        gradingScheme.put("enablePoint", "true");
        gradingScheme.put("schoolId", schoolID);
        gradingScheme.put("gradeRanges", new Object[1]);

        schemeID =
                given()

                        .spec(requestSpecification)
                        .body(gradingScheme)

                        .when()
                        .post("/school-service/api/grading-schemes/")

                        .then()
                        .log().body()
                        .statusCode(201)

                        .extract().path("id")
        ;
        given()
                .spec(requestSpecification)
                .contentType(ContentType.JSON)

                .when()
                .get("/school-service/api/grading-schemes/" + schemeID)

                .then()
                .statusCode(200)
                .body("id", equalTo(schemeID))
                .body("name", equalTo(randomGradingSchemeName))
                .body("type", equalTo("POINT"))
                .body("schoolId", equalTo(schoolID))
        ;


    }

    @Test(dependsOnMethods = "createGradingScheme")
    public void createGradingSchemeNegativeTest() {

        Map<String, Object> gradingScheme = new HashMap<>();
        gradingScheme.put("name", randomGradingSchemeName);
        gradingScheme.put("type", "POINT");
        gradingScheme.put("active", "true");
        gradingScheme.put("enablePoint", "true");
        gradingScheme.put("schoolId", schoolID);
        gradingScheme.put("gradeRanges", new Object[1]);

        given()

                .spec(requestSpecification)
                .body(gradingScheme)

                .when()
                .post("/school-service/api/grading-schemes/")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("There is already Grade Scheme with"))

        ;
        given()
                .spec(requestSpecification)
                .contentType(ContentType.JSON)

                .when()
                .get("/school-service/api/grading-schemes/" + schemeID)

                .then()
                .statusCode(200)
                .body("id", equalTo(schemeID))
                .body("name", equalTo(randomGradingSchemeName))
                .body("type", equalTo("POINT"))
                .body("schoolId", equalTo(schoolID))
        ;


    }

    @Test(dependsOnMethods = "createGradingScheme")
    public void editGradingScheme() {
        String editedGradingScheme = faker.educator().course();
        Map<String, Object> editGradingScheme = new HashMap<>();
        editGradingScheme.put("id", schemeID);
        editGradingScheme.put("name", editedGradingScheme);
        editGradingScheme.put("type", "POINT");
        editGradingScheme.put("active", "true");
        editGradingScheme.put("enablePoint", "true");
        editGradingScheme.put("schoolId", schoolID);
        editGradingScheme.put("gradeRanges", new Object[1]);

        given()

                .spec(requestSpecification)
                .body(editGradingScheme)

                .when()
                .put("/school-service/api/grading-schemes/")

                .then()
                .log().body()
                .statusCode(200)
                .body("id", equalTo(schemeID))
                .body("schoolId", equalTo(schoolID))
        ;

    }

    @Test(dependsOnMethods = "editGradingScheme")
    public void deleteGradingScheme() {

        Map<String, String> deleteGradingScheme = new HashMap<>();
        deleteGradingScheme.put("X-School", schoolID);


        given()

                .spec(requestSpecification)
                .headers(deleteGradingScheme)


                .when()
                .delete("/school-service/api/grading-schemes/" + schemeID)

                .then()
                .log().body()
                .statusCode(200)
        ;

    }

    @Test(dependsOnMethods = "editGradingScheme")
    public void deleteGradingSchemeNegativeTest() {

        Map<String, String> deleteGradingSchemeNegative = new HashMap<>();
        deleteGradingSchemeNegative.put("X-School", schoolID);


        given()

                .spec(requestSpecification)
                .headers(deleteGradingSchemeNegative)


                .when()
                .delete("/school-service/api/grading-schemes/" + schemeID)

                .then()
                .log().body()
                .statusCode(400)
                .body("message",containsString("To delete grading scheme not found"))
        ;


    }


}
