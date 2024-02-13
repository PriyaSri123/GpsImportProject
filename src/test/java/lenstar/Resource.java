package lenstar;

import java.util.ArrayList;
import java.util.Date;

public class Resource{
	 public String resourceType;
	 public String getResourceType() {
		return resourceType;
	}
	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Code getCode() {
		return code;
	}
	public void setCode(Code code) {
		this.code = code;
	}
	public Date getEffectiveDateTime() {
		return effectiveDateTime;
	}
	public void setEffectiveDateTime(Date effectiveDateTime) {
		this.effectiveDateTime = effectiveDateTime;
	}
	public ArrayList<Component> getComponent() {
		return component;
	}
	public void setComponent(ArrayList<Component> component) {
		this.component = component;
	}
	public ArrayList<Related> getRelated() {
		return related;
	}
	public void setRelated(ArrayList<Related> related) {
		this.related = related;
	}
	public Meta getMeta() {
		return meta;
	}
	public void setMeta(Meta meta) {
		this.meta = meta;
	}
	public ArrayList<Identifier> getIdentifier() {
		return identifier;
	}
	public void setIdentifier(ArrayList<Identifier> identifier) {
		this.identifier = identifier;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public ArrayList<Name> getName() {
		return name;
	}
	public void setName(ArrayList<Name> name) {
		this.name = name;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getBirthDate() {
		return birthDate;
	}
	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}
	public ManagingOrganization getManagingOrganization() {
		return managingOrganization;
	}
	public void setManagingOrganization(ManagingOrganization managingOrganization) {
		this.managingOrganization = managingOrganization;
	}
	public String id;
	 public String status;
	 public Code code;
	 public Date effectiveDateTime;
	 public ArrayList<Component> component;
	 public ArrayList<Related> related;
	 public Meta meta;
	 public ArrayList<Identifier> identifier;
	 public String active;
	 public ArrayList<Name> name;
	 public String gender;
	 public String birthDate;
	 public ManagingOrganization managingOrganization;
	}
