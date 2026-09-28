package api.clients;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import utils.ConfigReader;

import static io.restassured.RestAssured.given;

public abstract class BaseApiClient {

    private final RequestSpecification spec = new RequestSpecBuilder()
            .setBaseUri(ConfigReader.get("api.base.url"))
            .setAccept(ContentType.JSON)
            .addHeader("Origin", ConfigReader.get("base.url"))
            .log(LogDetail.URI)
            .build();

    protected RequestSpecification request() {
        return given().spec(spec);
    }
}
