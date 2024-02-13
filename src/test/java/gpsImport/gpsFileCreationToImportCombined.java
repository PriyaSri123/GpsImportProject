package gpsImport;

import static io.restassured.RestAssured.given;



import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import common.CmdPromptOutputCheck;
import common.payload;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;

public class gpsFileCreationToImportCombined {

	public static String BASE_URL = "";
	public static String BASE_URL_ENCODE_DECODE = "";
	public static String OBSERV_URL = "";
	public static String S3_ACCESS_BASE_URL = "";
	String access_token;
	String iamAccessToken;
	String PerftestOrgId;
	File userdetail;
	String response;
	String resp;
	static String encodedString;
	String accessKey;
	String sessionToken;
	String secretKey;
	String bucket;
	String awsPath;
	String gpsFolderName = "GPSFolder";
	String env;
	String username ="";
	String password ="";

	@Parameters({"environment"})
	@Test(priority = 1,groups = {"encode", "decode", "upload", "importwithpid", "importwithoutpid" })
	public void UrlDetails(String environment) {
        this.env = environment;
        System.out.println("Environment selected: "+env);
		//env = "qa";
		if (env.equalsIgnoreCase("qa") ){
			this.BASE_URL = "https://qa-auth-services.smartsuite-cataract.com";
			this.BASE_URL_ENCODE_DECODE = "https://qa-encoderdecoder.smartsuite-cataract.com";
			this.OBSERV_URL = "https://qa-observation.smartsuite-cataract.com";
			this.S3_ACCESS_BASE_URL = "https://hsdp-alcon-s3creds-qa.us-east.philips-healthsuite.com";
			this.userdetail = new File(".//src//test//resources//files//userdetailsQA.json");
		} else if (env.equalsIgnoreCase("vnv")) {
			this.BASE_URL = "https://vandv-auth-services.smartsuite-cataract.com";
			this.S3_ACCESS_BASE_URL = "https://s3creds-alconvandv.us-east.philips-healthsuite.com";
			this.BASE_URL_ENCODE_DECODE = "https://vandv-encoderdecoder.smartsuite-cataract.com";
			this.OBSERV_URL = "https://vandv-observation.smartsuite-cataract.com";
			this.userdetail = new File(".//src//test//resources//files//userdetails.json");
		} else if (env.equalsIgnoreCase("dev")) {
			this.BASE_URL = "https://dev-auth-services.smartsuite-cataract.com";
			this.BASE_URL_ENCODE_DECODE = "https://dev-encoderdecoder.smartsuite-cataract.com";
			this.OBSERV_URL = "https://dev-observation.smartsuite-cataract.com";
			this.S3_ACCESS_BASE_URL = "https://hsdp-alcon-s3creds-dev.us-east.philips-healthsuite.com";
			this.userdetail = new File(".//src//test//resources//files//userdetailsDev.json");

		}

	}
	
	@Parameters({"username","password"})
	@Test(priority = 2,groups = {"encode", "decode", "upload", "importwithpid", "importwithoutpid" },enabled =true)
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

	@Test(priority = 2, groups = { "encode", "decode", "upload", "importwithpid", "importwithoutpid" },enabled = false)
	public void getaccess() {
		// AccessToken req
		RestAssured.baseURI = BASE_URL;
		String res = given().log().all().headers("Accept", "*/*").headers("Accept-Encoding", "gzip, deflate, br")
				.headers("Connection", "keep-alive").headers("Content-Type", "application/json")
				.headers("api-version", "2").body(userdetail).when().post("authorize/oauth2/token").then().assertThat()
				.log().all().statusCode(200).extract().response().asString();

		System.out.println(res);
		JsonPath js = new JsonPath(res);
		access_token = js.get("access_token");
		System.out.println("accessToken:" + access_token);
		PerftestOrgId = js.get("user_details.organizations.managingOrganization");
		System.out.println("PerftestOrg:" + PerftestOrgId);

	}

	@Test(priority = 3, groups = { "encode", "decode", "upload", "importwithpid", "importwithoutpid" })
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
	
	@Parameters({"gpsName"})
	@Test(priority=4,groups = {"decode"})
	public static void  DecodeGPSFile(String gpsName) {

	 File file = new File("C:\\AWS\\"+gpsName+".gps");
			

		try {
			byte[] fileContent = FileUtils.readFileToByteArray(file);

			 encodedString = Base64.getEncoder().encodeToString(fileContent);
			System.out.println("Decoded string is " + encodedString);
            //Path Filename = Path.of("C:\\hexaDecimalDecoder\\decode_file.txt");
			//Files.writeString(Filename,encodedString);
		} catch (IOException ex) {
			System.out.println("Exception occurred");
		}
	}
	


	@Test(priority = 5, groups = { "decode" })
	public void fileEditEncode() {

		FileWriter encodedDataFile;
		try {
			encodedDataFile = new FileWriter(".//src//test//resources//out//encodeDataOne.json");
			encodedDataFile.write(encodedString);
			encodedDataFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test(priority = 6, groups = { "decode" })
	public void gpsDecodeFn() {

		RestAssured.baseURI = BASE_URL_ENCODE_DECODE;
		String res = given().log().all().headers("Content-Type", "application/json").headers("Accept", "*/*")
				.headers("Accept-Encoding", "gzip, deflate, br").headers("Connection", "keep-alive")
				.headers("Authorization", "" + access_token + "").headers("User-Agent", "PostmanRuntime/7.30.0")
				.body("{\r\n" + " \"gpsXml\":\"" + encodedString + "\",\r\n" + "    \"returnedImageFormat\": null\r\n"
						+ "\r\n" + "}")
				.when().post("api/encoderdecoder/decodegps").then().assertThat().log().all().statusCode(200).extract()
				.response().asString();

		FileWriter decodedDataFile = null;
		try {
			decodedDataFile = new FileWriter(".//src//test//resources//out//decodeDataOne.json");
			decodedDataFile.write(res);
			decodedDataFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test(priority = 4, groups = { "encode" })
	public void gpsEncodeCheck() {

		String pathInput = System.getProperty("user.dir")+"\\src\\test\\resources\\files\\jsonBodyToBeEncoded.json";
		File encodefile = new File(pathInput);
		RestAssured.baseURI = BASE_URL_ENCODE_DECODE;
		response = given().log().all().headers("Content-Type", "application/json").headers("Accept", "*/*")
				.headers("Accept-Encoding", "gzip, deflate, br").headers("Connection", "keep-alive")
				.headers("Authorization", "" + access_token + "").headers("User-Agent", "PostmanRuntime/7.30.0")
				.body(encodefile).when().post("api/encoderdecoder/encodegps").then().assertThat().log().all()
				.statusCode(200).extract().response().asString();

		System.out.println(response);
		JsonPath js = new JsonPath(response);
		String encryptedData = js.get("gpsXml");
		FileWriter encodedDataFile;
		String pathOut = System.getProperty("user.dir")+"\\src\\test\\resources\\out\\encodegpsData.json";
		try {
			encodedDataFile = new FileWriter(pathOut);
			encodedDataFile.write(encryptedData);
			encodedDataFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Parameters({ "gpsName" })
	@Test(dependsOnMethods = { "gpsEncodeCheck" }, groups = { "encode" })
	public void createGpsFile(String gpsName) {

		String content;
		// File file = new
		// File("C:\\Users\\Priyadarshini.m\\eclipse-workspace-lat\\practiceApi\\src\\test\\resources\\out\\ARGOSLATEST.gps");

		String pathIn = System.getProperty("user.dir")+"\\src\\test\\resources\\out\\encodegpsData.json";
		try {
			content = new String(Files.readAllBytes(Paths.get(pathIn)));
			String encodedString = content;
			byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
			// FileUtils.writeByteArrayToFile(new
			// File("C:\\Users\\Priyadarshini.m\\eclipse-lat\\gpsImportProject\\src\\test\\resources\\out\\"+gpsName+".gps"),
			// decodedBytes);
			FileUtils.writeByteArrayToFile(new File("C:\\AWS\\" + gpsName + ".gps"), decodedBytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test(priority = 7, groups = { "upload" })
	public void getAccess() {
		// access Request for gps file

		RestAssured.baseURI = S3_ACCESS_BASE_URL;
		response = given().log().all().headers("Content-Type", "application/json").headers("Accept", "*/*")
				.headers("Accept-Encoding", "gzip, deflate, br").headers("Connection", "keep-alive")
				.headers("Authorization", "" + iamAccessToken + "").headers("api-version", "1").when()
				.get("core/credentials/Access").then().assertThat().log().all().statusCode(200).extract().response()
				.asString();

		System.out.println(response);

	}

	
	@Test(priority = 8, groups = { "upload" })
	public void getKeys() {
		JsonPath j = new JsonPath(response);
		
		if (env.equalsIgnoreCase("vnv")) {
			// get values of JSON array after getting array size
			int s = j.getInt("credentials.size()");
			System.out.println(s);
			for (int i = 0; i < s; i++) {
				if (i == 0) {
					accessKey = j.getString("credentials[" + i + "].accessKey");
					secretKey = j.getString("credentials[" + i + "].secretKey");
					sessionToken = j.getString("credentials[" + i + "].sessionToken");
					bucket = j.getString("credentials[" + i + "].bucket");
					System.out.println("accessKey: " + accessKey);
					System.out.println("secretKey:" + secretKey);
					System.out.println("sessionToken: " + sessionToken);
					System.out.println("bucketId: " + bucket);
				}
			}

			// get values of JSON array after getting array size
			int r = j.getInt("allowed.size()");
			System.out.println(s);
			for (int i = 0; i < r; i++) {
				if (i == 0) {
					awsPath = j.getString("allowed[" + i + "].resources[2]");
					System.out.println(awsPath);
				}
			}
		}

		else if (env.equalsIgnoreCase("qa")) {
			// get values of JSON array after getting array size
			int s = j.getInt("credentials.size()");
			System.out.println(s);
			for (int i = 0; i < s; i++) {
				if (i == 1) {
					accessKey = j.getString("credentials[" + i + "].accessKey");
					secretKey = j.getString("credentials[" + i + "].secretKey");
					sessionToken = j.getString("credentials[" + i + "].sessionToken");
					bucket = j.getString("credentials[" + i + "].bucket");
					System.out.println("accessKey: " + accessKey);
					System.out.println("secretKey:" + secretKey);
					System.out.println("sessionToken: " + sessionToken);
					System.out.println("bucketId: " + bucket);
				}
			}

			// get values of JSON array after getting array size
			int r = j.getInt("allowed.size()");
			System.out.println(s);
			for (int i = 0; i < r; i++) {
				if (i == 1) {
					awsPath = j.getString("allowed[" + i + "].resources[2]");
					System.out.println(awsPath);
				}
			}
		}

		else if (env.equalsIgnoreCase("dev")) {
			// get values of JSON array after getting array size
			int s = j.getInt("credentials.size()");
			System.out.println(s);
			for (int i = 0; i < s; i++) {
				if (i == 0) {
					accessKey = j.getString("credentials[" + i + "].accessKey");
					secretKey = j.getString("credentials[" + i + "].secretKey");
					sessionToken = j.getString("credentials[" + i + "].sessionToken");
					bucket = j.getString("credentials[" + i + "].bucket");
					System.out.println("accessKey: " + accessKey);
					System.out.println("secretKey:" + secretKey);
					System.out.println("sessionToken: " + sessionToken);
					System.out.println("bucketId: " + bucket);
				}
			}

			// get values of JSON array after getting array size
			int r = j.getInt("allowed.size()");
			System.out.println(s);
			for (int i = 0; i < r; i++) {
				if (i == 0) {
					awsPath = j.getString("allowed[" + i + "].resources[2]");
					System.out.println(awsPath);
				}
			}
		}

	}

	@Test(priority = 9, groups = { "upload" })
	public void fileEdit() {
		String stringTwo = "[default]\r\n" + "aws_access_key_id = " + accessKey + "\r\n" + "aws_secret_access_key = "
				+ secretKey + "\r\n" + "aws_session_token = " + sessionToken + "";

		String credPath = System.getProperty("user.home")+"\\.aws\\credentials";
		try {
			FileWriter file = new FileWriter(credPath);
			file.write(stringTwo);
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// cmd for running a single gps file
	@Parameters({ "gpsName" })
	@Test(priority = 10, groups = { "upload" })
	public void cmdPromptFn(String gpsName) {
		String command = "cmd.exe /c start " + "aws s3 cp C:\\AWS\\" + gpsName + ".gps s3://" + bucket + "/" + awsPath
				+ "/" + gpsName + ".gps";
		try {
			Process rt = Runtime.getRuntime().exec(command);
			System.out.println("GPS Upload function executed");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CmdPromptOutputCheck c1 = new CmdPromptOutputCheck();
		c1.cmdPromptOutResult(bucket, awsPath, gpsName);

	}

	// cmd prompt for running multiple gps files

	@Test(priority = 10, enabled = false, groups = { "upload" })
	public void cmdPromptFnTwo() {
		String command = "cmd.exe /c start " + "aws s3 cp --recursive C://AWS//" + gpsFolderName + "// s3://" + bucket
				+ "/" + awsPath + "//";
		try {
			Process rt = Runtime.getRuntime().exec(command);
			System.out.println("GPS Upload function executed");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Parameters({ "gpsName", "patientId" })
	@Test(priority = 11, groups = { "importwithpid" })
	public void gpsFileImportWithPatientId(String gpsName, String patientId) {

		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RestAssured.baseURI = OBSERV_URL;
		String response = given().log().all().headers("Content-Type", "application/json").headers("Accept", "*/*")
				.headers("Accept-Encoding", "gzip, deflate, br").headers("Connection", "keep-alive")
				.headers("Authorization", "" + access_token + "").headers("User-Agent", "PostmanRuntime/7.30.0")
				.body(payload.fhirGpsRequest(PerftestOrgId, gpsName, patientId)).when()
				.put("observation/fhir/" + PerftestOrgId + "/Observation").then().assertThat().log().all()
				.statusCode(200).extract().response().asString();

		System.out.println(response);

	}

	@Test(priority = 12, groups = { "importwithoutpid" })
	@Parameters({ "gpsName" })
	public void gpsFileImportWithoutPatientId(String gpsName) {
		// fhir Request
		try {
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		RestAssured.baseURI = OBSERV_URL;
		String response = given().log().all().headers("Content-Type", "application/json").headers("Accept", "*/*")
				.headers("Accept-Encoding", "gzip, deflate, br").headers("Connection", "keep-alive")
				.headers("Authorization", "" + access_token + "").headers("User-Agent", "PostmanRuntime/7.30.0")
				.body(payload.fhirGpsRequest(PerftestOrgId, gpsName)).when()
				.put("observation/fhir/" + PerftestOrgId + "/Observation").then().assertThat().log().all()
				.statusCode(200).extract().response().asString();

		System.out.println(response);

	}
}
