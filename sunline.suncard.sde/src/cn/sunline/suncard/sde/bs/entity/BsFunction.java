package cn.sunline.suncard.sde.bs.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * BsFunction entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BS_FUNCTION")
public class BsFunction implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields

	private BsFunctionId id;
	private String functionName;
	private Timestamp modiDate;
	private String modiUser;
	private Integer version;

	// Constructors
	
	private List<BsFuncmapping> bsFuncmappings = new ArrayList<BsFuncmapping>();

	/** default constructor */
	public BsFunction() {
	}

	/** minimal constructor */
	public BsFunction(BsFunctionId id) {
		this.id = id;
	}

	/** full constructor */
	public BsFunction(BsFunctionId id, String functionName, Timestamp modiDate,
			String modiUser, Integer version) {
		this.id = id;
		this.functionName = functionName;
		this.modiDate = modiDate;
		this.modiUser = modiUser;
		this.version = version;
	}

	// Property accessors
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "bankorgId", column = @Column(name = "BANKORG_ID", nullable = false, precision = 10, scale = 0)),
			@AttributeOverride(name = "pcId", column = @Column(name = "PC_ID", nullable = false, length = 20)),
			@AttributeOverride(name = "functionId", column = @Column(name = "FUNCTION_ID", nullable = false, length = 10)) })
	public BsFunctionId getId() {
		return this.id;
	}

	public void setId(BsFunctionId id) {
		this.id = id;
	}

	@Column(name = "FUNCTION_NAME", length = 40)
	public String getFunctionName() {
		return this.functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	@Column(name = "MODI_DATE", length = 0)
	public Timestamp getModiDate() {
		return this.modiDate;
	}

	public void setModiDate(Timestamp modiDate) {
		this.modiDate = modiDate;
	}

	@Column(name = "MODI_USER", length = 64)
	public String getModiUser() {
		return this.modiUser;
	}

	public void setModiUser(String modiUser) {
		this.modiUser = modiUser;
	}

	@Column(name = "VERSION", precision = 8, scale = 0)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Transient
	public List<BsFuncmapping> getBsFuncmappings() {
		return bsFuncmappings;
	}

	public void setBsFuncmappings(List<BsFuncmapping> bsFuncmappings) {
		this.bsFuncmappings = bsFuncmappings;
	}
}