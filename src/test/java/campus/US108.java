package campus;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

public class US108 {

    Faker faker = new Faker();
    RequestSpecification requestSpecification;
    Map<String, String> educationStandard;
    String randomEducationStandardName;
    String randomEducationStandardDescription;
    String educationID;
    String schoolID = "646cbb07acf2ee0d37c6d984";
    Map<String, String> editedEducationStandard;
    String editetdRandomEducationStandardName;
    String editedRandomEducationStandardDescription;

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
    public void getEducationStandardList() {
        ;

        Response response =

                given()

                        .spec(requestSpecification)
                        .contentType(ContentType.JSON)

                        .when()
                        .get("/school-service/api/education-standard/school/6576fd8f8af7ce488ac69b89")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract().response();

        Assert.assertTrue(response.getContentType().contains("application/json"), "Wrong Response Format");

        JsonPath jsonPath = response.jsonPath();

        Assert.assertTrue(jsonPath.getString("id") != null && !jsonPath.getString("id").isEmpty(), "id field is present and not empty");
        Assert.assertTrue(jsonPath.getString("name") != null && !jsonPath.getString("name").isEmpty(), "name field is present and not empty");
        Assert.assertTrue(jsonPath.getString("description") != null, "description field is present");
        Assert.assertTrue(jsonPath.getString("schoolId") != null && !jsonPath.getString("schoolId").isEmpty(), "schoolId field is present and not empty");

    }

    @Test(dependsOnMethods = "getEducationStandardList")
    public void createEducationStandard() {

        randomEducationStandardName = faker.university().name();
        randomEducationStandardDescription = faker.book().title();

        educationStandard = new HashMap<>();

        educationStandard.put("name", randomEducationStandardName);
        educationStandard.put("description", randomEducationStandardDescription);
        educationStandard.put("schoolId", schoolID);


        educationID =
                given()
                        .spec(requestSpecification)
                        .body(educationStandard)

                        .when()
                        .post("/school-service/api/education-standard")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");

        Assert.assertTrue(randomEducationStandardName.length() < 101, "Name should be less than or equal to 100 characters");
        Assert.assertTrue(randomEducationStandardDescription.length() < 5001, "Description should be less than or equal to 5000 characters");


    }

    @Test(dependsOnMethods = "createEducationStandard")
    public void editEducationStandard() {


        editetdRandomEducationStandardName = faker.educator().campus();
        editedRandomEducationStandardDescription = faker.book().publisher();

        editedEducationStandard = new HashMap<>();

        editedEducationStandard.put("id", educationID);
        editedEducationStandard.put("name", editetdRandomEducationStandardName);
        editedEducationStandard.put("description", editedRandomEducationStandardDescription);
        editedEducationStandard.put("schoolId", schoolID);


        given()
                .spec(requestSpecification)
                .body(editedEducationStandard)

                .when()
                .put("/school-service/api/education-standard")

                .then()
                .log().body()
                .statusCode(200);


        Assert.assertTrue(randomEducationStandardName.length() < 101, "Name should be less than or equal to 100 characters");
        Assert.assertTrue(randomEducationStandardDescription.length() < 5001, "Description should be less than or equal to 5000 characters");


    }

    @Test(dependsOnMethods = "editEducationStandard")
    public void deleteEducationStandard() {

        Response response =
                given()
                        .spec(requestSpecification)

                        .when()
                        .delete("/school-service/api/education-standard/" + educationID)

                        .then()
                        .log().body()
                        .statusCode(204)
                        .extract().response();

        Assert.assertEquals(response.getStatusCode(), 204, "Education Standard successfully deleted");


    }

    @Test(dependsOnMethods = "deleteEducationStandard")
    public void deleteEducationStandardNegative() {

        given()
                .spec(requestSpecification)

                .when()
                .delete("/school-service/api/education-standard/" + educationID)

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("Education Standard not found"));


    }
}
