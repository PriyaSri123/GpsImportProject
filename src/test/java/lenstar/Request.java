package lenstar;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Request{
	 public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getIfNoneExist() {
		return ifNoneExist;
	}
	public void setIfNoneExist(String ifNoneExist) {
		this.ifNoneExist = ifNoneExist;
	}
	public String method;
	 public String url;
	 @JsonProperty("IfNoneExist") 
	 public String ifNoneExist;
	}
