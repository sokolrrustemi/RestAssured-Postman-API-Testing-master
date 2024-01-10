package campus;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.ObjectFactory;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;


public class US110 {

    Faker faker=new Faker();
    String schoolID= "6576fd8f8af7ce488ac69b89";
    RequestSpecification requestSpecification;
    String randomIncidentTypeName;
    String incidentTypeID;
    String editedIncidentTypeName;


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
    public void getIncidentTypeList() {
        Map<String ,String> getList=new HashMap<>();
        getList.put("schoolId",schoolID);

        Response response=
        given()

                .spec(requestSpecification)
                .contentType(ContentType.JSON)
                .body(getList)

                .when()
                .post("/school-service/api/incident-type/search");

                response.then()
                .log().body()
                .statusCode(200)
                ;

        List<Map<String,Object>> responseData = response.jsonPath().getList("$");

        for (Map<String,Object> item : responseData){

            Assert.assertTrue(item.containsKey("id"));
            Assert.assertTrue(item.get("name")instanceof String && !((String)item.get("name")).isEmpty());
            Assert.assertTrue(item.get("translateName")instanceof List);
            Assert.assertTrue(item.get("active")instanceof Boolean);
            Assert.assertTrue(item.get("schoolId")instanceof String && !((String)item.get("schoolId")).isEmpty());
            Assert.assertTrue(item.get("minPoint")instanceof Number && !Double.isNaN(((Number)item.get("minPoint")).doubleValue()));
            Assert.assertTrue(item.get("maxPoint")instanceof Number && !Double.isNaN(((Number)item.get("maxPoint")).doubleValue()));
            Assert.assertTrue(item.get("academicBased")instanceof Boolean);
        }

    }

    @Test(dependsOnMethods = "getIncidentTypeList")
    public void createIncidentType() {
        randomIncidentTypeName =faker.address().fullAddress();
        Integer numberMin=faker.number().numberBetween(10, 20);
        Integer numberMax=faker.number().numberBetween(21, 99);

        Map<String,Object>incidentType=new HashMap<>();
        incidentType.put("name",randomIncidentTypeName);
        incidentType.put("active", "true");
        incidentType.put("schoolId",schoolID);
        incidentType.put("translateName",new Object[1]);
        incidentType.put("minPoint", numberMin);
        incidentType.put("maxPoint", numberMax);
        incidentType.put("academicBased","true");





        incidentTypeID=
                given()

                        .spec(requestSpecification)
                        .body(incidentType)


                        .when()
                        .post("/school-service/api/incident-type/")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id")

        ;

    }

    @Test(dependsOnMethods = "createIncidentType")
    public void createIncidentTypeNegativeTest() {

        Integer numberMin=faker.number().numberBetween(10, 20);
        Integer numberMax=faker.number().numberBetween(21, 99);

        Map<String,Object>incidentType=new HashMap<>();
        incidentType.put("name",randomIncidentTypeName);
        incidentType.put("active", "true");
        incidentType.put("schoolId",schoolID);
        incidentType.put("translateName",new Object[1]);
        incidentType.put("minPoint", numberMin);
        incidentType.put("maxPoint", numberMax);
        incidentType.put("academicBased","true");






                given()

                        .spec(requestSpecification)
                        .body(incidentType)


                        .when()
                        .post("/school-service/api/incident-type/")

                        .then()
                        .log().body()
                        .statusCode(400)
                        .body("message",containsString("already exists."))

                ;

    }

    @Test(dependsOnMethods = "createIncidentType")
    public void editIncidentType() {

        editedIncidentTypeName =faker.address().secondaryAddress();
        Integer numberMin=faker.number().numberBetween(10, 20);
        Integer numberMax=faker.number().numberBetween(21, 99);

        Map<String,Object>editIncidentType=new HashMap<>();
        editIncidentType.put("id",incidentTypeID);
        editIncidentType.put("name",editedIncidentTypeName);
        editIncidentType.put("active", "true");
        editIncidentType.put("schoolId",schoolID);
        editIncidentType.put("translateName",new Object[1]);
        editIncidentType.put("minPoint", numberMin);
        editIncidentType.put("maxPoint", numberMax);
        editIncidentType.put("academicBased","true");






                given()

                        .spec(requestSpecification)
                        .body(editIncidentType)


                        .when()
                        .put("/school-service/api/incident-type/")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .body("id",equalTo(incidentTypeID))
                        .body("name",equalTo(editedIncidentTypeName))
                        .body("minPoint", equalTo(numberMin))
                        .body("maxPoint",equalTo(numberMax))
                ;

    }

    @Test(dependsOnMethods = "editIncidentType")
    public void deleteIncidentType() {



        given()

                .spec(requestSpecification)

                .when()
                .delete("/school-service/api/incident-type/"+incidentTypeID)

                .then()
                .log().body()
                .statusCode(200)
        ;
    }

    @Test(dependsOnMethods = "deleteIncidentType")
    public void deleteIncidentTypeNegative() {



        given()

                .spec(requestSpecification)

                .when()
                .delete("/school-service/api/incident-type/"+incidentTypeID)

                .then()
                .log().body()
                .statusCode(400)
                .body("message",containsString("Incident Type Not Found"))
        ;
    }
}


