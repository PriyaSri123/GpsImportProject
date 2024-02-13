package gpsImport;

import static io.restassured.RestAssured.given;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.testng.Reporter;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import common.onePlannerPayload;
import common.payload;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

public class onePlanner {

	public static String ONEPLANNER_BASE_URL = "";
	public static String BASE_AUTH_URL = "";
	public static String OBSERV_URL = "";
	public static String PATIENT_URL = "";
	public static String BASE_URL_ENCODE_DECODE = "";
	static String encodedString;

	String access_token;
	String iamAccessToken;
	String PerftestOrgId;
	String surgeonId;
	File userdetail;
	String response;
	String env;
	String fileId;
	String patientguid;

	@Parameters({ "environment" })
	@Test(priority = 1, groups = { "onePlanner", "patientCreation", "getPatientDetailsById",
			"gpsDecodeAndEncryptUsingB64" })
	public void UrlDetails(String environment) {
		this.env = environment;
		System.out.println("Environment selected: " + env);
		if (env.equalsIgnoreCase("qa")) {
			this.ONEPLANNER_BASE_URL = "https://qa.api.avs-alcon.com";
			this.BASE_AUTH_URL = "https://qa-auth-services.smartsuite-cataract.com";
			this.OBSERV_URL = "https://qa-observation.smartsuite-cataract.com";
			this.PATIENT_URL = "https://qa-patient.smartsuite-cataract.com";
			this.BASE_URL_ENCODE_DECODE = "https://qa-encoderdecoder.smartsuite-cataract.com";
		} else if (env.equalsIgnoreCase("vnv")) {
			this.ONEPLANNER_BASE_URL = "https://vandv.api.avs-alcon.com";
			this.BASE_AUTH_URL = "https://vandv-auth-services.smartsuite-cataract.com";
			this.OBSERV_URL = "https://vandv-observation.smartsuite-cataract.com";
			this.PATIENT_URL = "https://vandv-patient.smartsuite-cataract.com";
			this.BASE_URL_ENCODE_DECODE = "https://vandv-encoderdecoder.smartsuite-cataract.com";
		} else if (env.equalsIgnoreCase("dev")) {
			this.ONEPLANNER_BASE_URL = "https://dev.api.avs-alcon.com";
			this.BASE_AUTH_URL = "https://dev-auth-services.smartsuite-cataract.com";
			this.OBSERV_URL = "https://dev-observation.smartsuite-cataract.com";
			this.PATIENT_URL = "https://dev-patient.smartsuite-cataract.com";
			this.BASE_URL_ENCODE_DECODE = "https://dev-encoderdecoder.smartsuite-cataract.com";
		} else if (env.equalsIgnoreCase("perf")) {
			this.ONEPLANNER_BASE_URL = "https://perf.api.avs-alcon.com";
			this.BASE_AUTH_URL = "https://perf-auth-services.smartsuite-cataract.com";
			this.OBSERV_URL = "https://perf-observation.smartsuite-cataract.com";
			this.PATIENT_URL = "https://perf-patient.smartsuite-cataract.com";
			this.BASE_URL_ENCODE_DECODE = "https://perf-encoderdecoder.smartsuite-cataract.com";
		}

	}

	@Parameters({ "username", "password" })
	@Test(priority = 2, groups = { "onePlanner", "patientCreation", "getPatientDetailsById",
			"gpsDecodeAndEncryptUsingB64" })
	public void accesstokenreq(String Username, String password) {
		String bodydetail = "{\r\n" + "\"grant_type\": \"password\",\r\n" + "\"username\" : \"" + Username + "\",\r\n"
				+ "\"password\":\"" + password + "\"\r\n" + "}\r\n" + "";
		RestAssured.baseURI = BASE_AUTH_URL;
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
		surgeonId = js.get("user_details.sub");
		System.out.println("surgeonId:" + surgeonId);
	}

	@Parameters({ "gpsNameToBeDecoded" })
	@Test(priority = 3, groups = { "gpsDecodeAndEncryptUsingB64" })
	public void gpsDecode(String gpsNameToBeDecoded) throws IOException {
		File file = new File("C:\\AWS\\" + gpsNameToBeDecoded + ".gps");

		FileWriter encodedDataFile;
		FileWriter decodedDataFile = null;

		// read the contents in the aws file and convert it to base 64 encrypted format
		byte[] fileContent = FileUtils.readFileToByteArray(file);
		encodedString = Base64.getEncoder().encodeToString(fileContent);
		System.out.println("Decoded string is " + encodedString);


		// write the encrypted content to a file
		encodedDataFile = new FileWriter(".//src//test//resources//out//oneplannergpsEncodedData.json");
		encodedDataFile.write(encodedString);
		encodedDataFile.close();

		//decoding file using base encode decode api
		RestAssured.baseURI = BASE_URL_ENCODE_DECODE;
		String res = given().log().all().headers("Content-Type", "application/json").headers("Accept", "*/*")
				.headers("Accept-Encoding", "gzip, deflate, br").headers("Connection", "keep-alive")
				.headers("Authorization", "" + access_token + "").headers("User-Agent", "PostmanRuntime/7.30.0")
				.body("{\r\n" + " \"gpsXml\":\"" + encodedString + "\",\r\n" + "    \"returnedImageFormat\": null\r\n"
						+ "\r\n" + "}")
				.when().post("api/encoderdecoder/decodegps").then().assertThat().log().all().statusCode(200).extract()
				.response().asString();

		decodedDataFile = new FileWriter(".//src//test//resources//out//decodeDataOnePlanner.json");
		decodedDataFile.write(res);
		decodedDataFile.close();

	}
	

	@Parameters({ "fhirtype", "firstName", "lastName", "patientMrn", "gender", "yyyymmdd" })
	@Test(priority = 4, groups = { "patientCreation" })
	public void patientCreation(String fhirtype, String firstName, String lastName, String patientMrn, String gender,
			String yyyymmdd) {

		String bodypart = "";
		if (fhirtype.equalsIgnoreCase("dicom")) {
			bodypart = payload.dicom_fn(PerftestOrgId, gender, firstName, lastName, patientMrn, yyyymmdd);
		} else if (fhirtype.equalsIgnoreCase("crs")) {
			bodypart = payload.crs_fn(PerftestOrgId, gender, firstName, lastName, patientMrn, yyyymmdd);
		} else if (fhirtype.equalsIgnoreCase("emr")) {
			bodypart = payload.testPatient(PerftestOrgId, gender, firstName, lastName, patientMrn, yyyymmdd);
		}
		RestAssured.baseURI = OBSERV_URL;
		String resp = given().log().all().headers("Content-Type", "application/json").headers("Accept", "*/*")
				.headers("Accept-Encoding", "gzip, deflate, br").headers("Connection", "keep-alive")
				.headers("Authorization", "" + access_token + "").headers("api-version", "1").body(bodypart).when()
				.put("observation/fhir/" + PerftestOrgId + "/Observation").then().assertThat().log().all()
				.statusCode(200).extract().response().asString();

		System.out.println(resp);

		JsonPath js = new JsonPath(resp);
		patientguid = js.get("entry[0].resource.id");
		System.out.println("PatientId: " + patientguid);

		Reporter.log("Patient Creation response : \n" + resp);

	}

	@Parameters({ "gpsfileName", "dhsAscId" })
	@Test(priority = 5, groups = { "onePlanner" })
	public void createMutliPartUploadReq(String gpsfileName, String dhsAscId) {

		RestAssured.baseURI = ONEPLANNER_BASE_URL;
		String res = given().log().all().headers("Accept", "*/*").headers("Accept-Encoding", "gzip, deflate, br")
				.headers("Connection", "keep-alive").headers("Content-Type", "application/json")
				.headers("api-version", "2").headers("Authorization", "" + access_token + "")
				.queryParams("practiceId", "" + PerftestOrgId + "")
				.queryParam("deviceId", "f8928f8a-5197-4f00-45da-448ee21b8455")
				.queryParam("fileId", "07dcaa0e-d92c-4f7d-ab55-f60f22374e96e35")
				.body(onePlannerPayload.MutlipartRequestPayload(patientguid, surgeonId, gpsfileName, dhsAscId)).when()
				.post("filemanagement/createMultipartUploadRequest").then().assertThat().log().all().statusCode(200)
				.extract().response().asString();

		JsonPath js = new JsonPath(res);
		fileId = js.get("fileId");
		System.out.println(fileId);

		System.out.println(res);

		Reporter.log("Create Mutli Part response : \n" + res);

	}

	@Test(priority = 6, enabled = true, groups = { "onePlanner" })
	public void putUploadPart() throws IOException {

		RestAssured.baseURI = ONEPLANNER_BASE_URL;
		String res = given().log().all().headers("Accept", "*/*").headers("Accept-Encoding", "gzip, deflate, br")
				.headers("Connection", "keep-alive").headers("Content-Type", "application/json")
				.headers("api-version", "2").headers("Authorization", "" + access_token + "")
				.queryParams("practiceId", "" + PerftestOrgId + "")
				.queryParam("deviceId", "f8928f8a-5197-4f00-45da-448ee21b8455").queryParam("fileId", "" + fileId + "")
				.queryParam("partNumber", "1").body(onePlannerPayload.putUploadReqBody()).when()
				.put("filemanagement/uploadPart").then().assertThat().log().all().statusCode(200).extract().response()
				.asString();

		System.out.println(res);

		Reporter.log("Put Upload Part response : \n" + res);

	}

	@Test(priority = 7, enabled = true, groups = { "onePlanner" })
	public void completeMultipartUpload() {

		RestAssured.baseURI = ONEPLANNER_BASE_URL;
		String res = given().log().all().headers("Accept", "*/*").headers("Accept-Encoding", "gzip, deflate, br")
				.headers("Connection", "keep-alive").headers("Content-Type", "application/json")
				.headers("api-version", "2").headers("Authorization", "" + access_token + "")
				.queryParams("practiceId", "" + PerftestOrgId + "")
				.queryParam("deviceId", "f8928f8a-5197-4f00-45da-448ee21b8455").queryParam("fileId", "" + fileId + "")
				.body(onePlannerPayload.completeMutlipartRequest()).when()
				.post("filemanagement/completeMultipartUpload").then().assertThat().log().all().statusCode(200)
				.extract().response().asString();

		System.out.println(res);

		Reporter.log("Complete Multipart response : \n" + res);
	}

	@Test(priority = 8, groups = { "getPatientDetailsById" })
	public void getPatientId() {

		RestAssured.baseURI = PATIENT_URL;
		String res = given().log().all().headers("Accept", "*/*").headers("Accept-Encoding", "gzip, deflate, br")
				.headers("Connection", "keep-alive").headers("Content-Type", "application/json")
				.headers("api-version", "2").headers("Authorization", "" + access_token + "")
				.queryParams("practiceId", "" + PerftestOrgId + "").when().get("patient/" + patientguid + "").then()
				.assertThat().log().all().statusCode(200).extract().response().asString();

		System.out.println(res);

		Reporter.log("Get PatientId response : \n" + res);
	}

};