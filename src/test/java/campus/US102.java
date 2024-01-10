package campus;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.HashMap;
import java.util.Map;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import com.github.javafaker.Faker;

public class US102 {

    Faker faker = new Faker();
    String nationalityID;
    Map<String, String> nationalityMap;
    RequestSpecification requestSpecification;
    String randomNationality;


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
    public void getNationalitiesList() {
        long startTime = System.currentTimeMillis();
        Map<String, Object> nationalitySearchDTO = new HashMap<>();


        given()

                .spec(requestSpecification)
                .contentType(ContentType.JSON)
                .body(nationalitySearchDTO)

                .when()
                .post("school-service/api/nationality/search/")

                .then()
                .log().body()

                .statusCode(200)
        ;

        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        System.out.println("Response time : " + responseTime + "milliseconds");

        Assert.assertTrue(responseTime <= 1000, "Response time exceeds 1000 milliseconds");


    }

    @Test(dependsOnMethods = "getNationalitiesList")
    public void createNationalities() {
        long startTime = System.currentTimeMillis();

        randomNationality = faker.nation().nationality().concat(String.valueOf(faker.number()));

        nationalityMap = new HashMap<>();
        nationalityMap.put("name", randomNationality);


        nationalityID =
                given()

                        .spec(requestSpecification)
                        .body(nationalityMap)

                        .when()
                        .post("/school-service/api/nationality")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")

        ;
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        System.out.println("Response time : " + responseTime + "milliseconds");

        Assert.assertTrue(responseTime <= 1000, "Response time exceeds 1000 milliseconds");


    }

    @Test(dependsOnMethods = "createNationalities")
    public void createNationalitiesNegativeTest() {
        long startTime = System.currentTimeMillis();

        nationalityMap = new HashMap<>();
        nationalityMap.put("name", randomNationality);


        given()

                .spec(requestSpecification)
                .body(nationalityMap)

                .when()
                .post("/school-service/api/nationality")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already exists"))

        ;
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        System.out.println("Response time : " + responseTime + "milliseconds");

        Assert.assertTrue(responseTime <= 1000, "Response time exceeds 1000 milliseconds");


    }

    @Test(dependsOnMethods = "createNationalities")
    public void editNationalities() {


        String newCountryName = faker.country().name().concat(faker.country().countryCode2());

        Map<String,String> editNationality=new HashMap<>();
        editNationality.put("id",nationalityID);
        editNationality.put("name",  newCountryName);




                given()

                        .spec(requestSpecification)
                        .body(editNationality)

                        .when()
                        .put("/school-service/api/nationality")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .body("name",containsString(newCountryName))


                        ;


    }


    @Test(dependsOnMethods = "editNationalities")
    public void deleteNationalities() {


        given()

                .spec(requestSpecification)

                .when()
                .delete("/school-service/api/nationality/"+nationalityID)

                .then()
                .log().body()
                .statusCode(200)


        ;


    }

    @Test(dependsOnMethods = "deleteNationalities")
    public void deleteNationalitiesNegativeTest() {






        given()

                .spec(requestSpecification)

                .when()
                .delete("/school-service/api/nationality/"+nationalityID)

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("Nationality not  found"))
        ;
    }




}
