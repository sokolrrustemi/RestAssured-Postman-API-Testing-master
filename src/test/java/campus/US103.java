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

public class US103 {
    Faker randum = new Faker();
    String schoolId = "646cbb07acf2ee0d37c6d984";
    String academicPeriod = "6509eba5f640da7207ab120c";
    String gradeLevel = "654898fae70d9e34a8331e51";
    RequestSpecification requestSpecification;
    String examId;

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

    @Test
    public void createExam() {
        String ranName = randum.educator().course();
        Map<String, Object> exam = new HashMap<>();
        exam.put("name", ranName);
        exam.put("school", schoolId);
        exam.put("academicPeriod", academicPeriod);
        exam.put("gradeLevel", Map.of("id", gradeLevel));
        examId =
                given()
                        .spec(requestSpecification)
                        .body(exam)
                        .when()
                        .post("school-service/api/exams")
                        .then()
                        .statusCode(201)
                        .extract().path("id");
    }

    @Test(dependsOnMethods = "createExam")
    public void editExam() {
        Map<String, Object> exam = new HashMap<>();
        exam.put("id", examId);
        exam.put("name", "degisken120");
        exam.put("school", schoolId);
        exam.put("academicPeriod", academicPeriod);
        exam.put("gradeLevel", Map.of("id", gradeLevel));

        given()
                .spec(requestSpecification)
                .body(exam)
                .when()
                .put("school-service/api/exams")
                .then()
                .statusCode(200);
    }

    @Test(dependsOnMethods = "createExam")
    public void createExamNegative() {
        String ranName = randum.educator().course();
        System.out.println(ranName);
        Map<String, Object> exam = new HashMap<>();
        exam.put("name", "");
        exam.put("school", schoolId);
        exam.put("academicPeriod", academicPeriod);
        exam.put("gradeLevel", Map.of("id", gradeLevel));
        given()
                .spec(requestSpecification)
                .body(exam)
                .when()
                .post("school-service/api/exams")
                .then()
                .statusCode(400);
    }

    @Test(dependsOnMethods = "editExam")
    public void editExamNegative() {
        Map<String, Object> exam = new HashMap<>();
        exam.put("id", "fdsgf454515161vffs");
        exam.put("name", "degisken12");
        exam.put("school", schoolId);
        exam.put("academicPeriod", academicPeriod);
        exam.put("gradeLevel", Map.of("id", gradeLevel));

        given()
                .spec(requestSpecification)
                .body(exam)
                .when()
                .put("school-service/api/exams")
                .then()
                .statusCode(400);
    }
    @Test(dependsOnMethods = "editExam")
    public void deletExam(){
        given()
                .spec(requestSpecification)
                .when()
                .delete("school-service/api/exams/"+examId)
                .then()
                .statusCode(204);
    }
    @Test(dependsOnMethods = "deletExam")
    public void deletNegative(){
        given()
                .spec(requestSpecification)
                .when()
                .delete("school-service/api/exams/"+examId)
                .then()
                .statusCode(204);
    }
}

