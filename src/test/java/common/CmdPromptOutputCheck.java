package common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;


public class CmdPromptOutputCheck{
	String awsPath;
	String bucket;
	String gpsName;

public StreamWrapper getStreamWrapper(InputStream is, String type){
            return new StreamWrapper(is, type);
}
private class StreamWrapper extends Thread {
    InputStream is = null;
    String type = null;          
    String message = null;

    public String getMessage() {
            return message;
    }

    StreamWrapper(InputStream is, String type) {
        this.is = is;
        this.type = type;
    }

    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuffer buffer = new StringBuffer();
            String line = null;
            while ( (line = br.readLine()) != null) {
                buffer.append(line);//.append("\n");
            }
            message = buffer.toString();
        } catch (IOException ioe) {
            ioe.printStackTrace();  
        }
    }
}
 
// this is where the action is

@Test(priority=1)
public void cmdPromptOutResult(String bucket,String awsPath , String gpsName)
{
	
//	       String bucket = "cf-s3-a602043a-2019-41dd-8d2a-a6e4c1e27c41";
//	       String awsPath ="98a3551f-8a71-4389-8a71-5cdce3954db0/test-images";
//	       String gpsName ="pseudophakic_silicone_IOLMASTER500";
           
	       String command = "aws s3 cp C:\\AWS\\"+gpsName+".gps s3://"+bucket+"/"+awsPath+"/"+gpsName+".gps";
           Runtime rt = Runtime.getRuntime();
           CmdPromptOutputCheck res = new CmdPromptOutputCheck();
            StreamWrapper error, output;

            try {
            	       
                        Process proc = rt.exec(command);
                        error = res.getStreamWrapper(proc.getErrorStream(), "ERROR");
                        output = res.getStreamWrapper(proc.getInputStream(), "OUTPUT");
                        int exitVal = 0;

                        error.start();
                        output.start();
                        error.join(3000);
                        output.join(3000);
                        exitVal = proc.waitFor();
                        System.out.println("Output: "+output.message+"");
            } catch (IOException e) {
                        e.printStackTrace();
            } catch (InterruptedException e) {
                        e.printStackTrace();
            }
            }
}
