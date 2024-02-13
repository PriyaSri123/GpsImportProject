package lenstar;

import static io.restassured.RestAssured.given;

import java.io.File;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import common.payload;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

public class FhirMeasurementImportWithPidCreation {

	public static String BASE_URL = "";
	public static String BASE_URL_ENCODE_DECODE = "";
	public static String OBSERV_URL = "";
	public static String S3_ACCESS_BASE_URL = "";
	private String env;
	String access_token;
	String iamAccessToken;
	String PerftestOrgId;

	@Parameters({ "environment" })
	@Test(priority = 1, groups = { "lenstar" })
	public void UrlDetails(String environment) {
		this.env = environment;
		System.out.println("Environment selected: " + env);
		// env = "qa";
		if (env.equalsIgnoreCase("qa")) {

			this.BASE_URL = "https://qa-auth-services.smartsuite-cataract.com";
			this.OBSERV_URL = "https://qa-observation.smartsuite-cataract.com";

		} else if (env.equalsIgnoreCase("vnv")) {

			this.BASE_URL = "https://vandv-auth-services.smartsuite-cataract.com";
			this.OBSERV_URL = "https://vandv-observation.smartsuite-cataract.com";

		} else if (env.equalsIgnoreCase("dev")) {

			this.BASE_URL = "https://dev-auth-services.smartsuite-cataract.com";
			this.OBSERV_URL = "https://dev-observation.smartsuite-cataract.com";

		}

	}

	@Parameters({ "username", "password" })
	@Test(priority = 2, groups = { "lenstar" }, enabled = true)
	public void getaccesstest(String Username, String password) {
		// AccessToken req
		String bodydetail = "{\r\n" + "\"grant_type\": \"password\",\r\n" + "\"username\" : \"" + Username + "\",\r\n"
				+ "\"password\":\"" + password + "\"\r\n" + "}\r\n" + "";
		RestAssured.baseURI = BASE_URL;
		String res = given().log().all().headers("Accept", "*/*").headers("Accept-Encoding", "gzip, deflate, br")
				.headers("Connection", "keep-alive").headers("Content-Type", "application/json")
				.headers("api-version", "2").body(bodydetail).when().post("authorize/oauth2/token").then().assertThat()
				.log().all().statusCode(200).extract().response().asString();

		System.out.println(res);
		JsonPath js = new JsonPath(res);
		access_token = js.get("access_token");
		System.out.println("accessToken:" + access_token);
		PerftestOrgId = js.get("user_details.organizations.managingOrganization");
		System.out.println("PerftestOrg:" + PerftestOrgId);

	}
	@Test(priority = 3, groups = { "lenstar" })
	public void alconIntrospect() {
		// AlconIntrospect
		RestAssured.baseURI = BASE_URL;
		String resp = given().log().all().headers("Content-Type", "application/json").headers("Accept", "*/*")
				.headers("Accept-Encoding", "gzip, deflate, br").headers("Connection", "keep-alive")
				.headers("api-version", "1").body(payload.accessToken(access_token)).when()
				.post("authorize/oauth2/introspect").then().assertThat().log().all().statusCode(200).extract()
				.response().asString();

		System.out.println(resp);
		JsonPath js1 = new JsonPath(resp);
		iamAccessToken = js1.get("accessToken");
		System.out.println("iamAccessToken:" + iamAccessToken);

	}
	
	@Test(priority = 4, groups = { "lenstar" })
	public void fhirLenstar() {
		// AlconIntrospect
		String body = payload.lenstarFhirReq();
		RestAssured.baseURI = OBSERV_URL;
		String resp = given().log().all().headers("Content-Type", "application/json").headers("Accept", "*/*")
				.headers("Accept-Encoding", "gzip, deflate, br").headers("Connection", "keep-alive")
				.headers("Authorization", "" + access_token + "").headers("User-Agent", "PostmanRuntime/7.30.0")
				.headers("api-version", "1").body(payload.lenstarFhirReq()).when()
				.put("observation/fhir/" + PerftestOrgId + "/Observation").then().assertThat().log().all().statusCode(200).extract()
				.response().asString();
		
		Name n1 = new Name();
		n1.setFamily("TEST");
		
		JsonPath js1 = new JsonPath(body);

		String ln = js1.get("entry[3].resource.name[0].family");
		String fn = js1.get("entry[3].resource.name[0].given[0]");
		String pid = js1.get("entry[3].resource.identifier[0].value");
		
		System.out.println(ln);
		System.out.println(fn);
		System.out.println(pid);
		
		
		  
		
		

	}

}
