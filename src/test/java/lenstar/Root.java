package lenstar;

import java.util.ArrayList;

public class Root{
	 public String resourceType;
	 public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ArrayList<Entry> getEntry() {
		return entry;
	}
	public void setEntry(ArrayList<Entry> entry) {
		this.entry = entry;
	}
	public String type;
	 public ArrayList<Entry> entry;
	}
