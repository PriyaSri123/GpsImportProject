package lenstar;

import java.util.ArrayList;


//import com.fasterxml.jackson.databind.ObjectMapper; // version 2.11.1
//import com.fasterxml.jackson.annotation.JsonProperty; // version 2.11.1
/* ObjectMapper om = new ObjectMapper();
Root root = om.readValue(myJsonString, Root.class); */
public class Code{
 public ArrayList<Coding> coding;

public ArrayList<Coding> getCoding() {
	return coding;
}

public void setCoding(ArrayList<Coding> coding) {
	this.coding = coding;
}
}






















