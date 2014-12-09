package cn.sunline.suncard.sde.bs.entity;

import java.sql.Timestamp;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BsDepartment entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BS_DEPARTMENT")
public class BsDepartment implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BsDepartmentId id;
	private String departmentName;
	private Timestamp modiDate;
	private String modiUser;
	private Integer version;

	// Constructors

	/** default constructor */
	public BsDepartment() {
	}

	/** minimal constructor */
	public BsDepartment(BsDepartmentId id) {
		this.id = id;
	}

	/** full constructor */
	public BsDepartment(BsDepartmentId id, String departmentName,
			Timestamp modiDate, String modiUser, Integer version) {
		this.id = id;
		this.departmentName = departmentName;
		this.modiDate = modiDate;
		this.modiUser = modiUser;
		this.version = version;
	}

	// Property accessors
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "bankorgId", column = @Column(name = "BANKORG_ID", nullable = false, precision = 10, scale = 0)),
			@AttributeOverride(name = "departmentId", column = @Column(name = "DEPARTMENT_ID", nullable = false, length = 10)) })
	public BsDepartmentId getId() {
		return this.id;
	}

	public void setId(BsDepartmentId id) {
		this.id = id;
	}

	@Column(name = "DEPARTMENT_NAME", length = 40)
	public String getDepartmentName() {
		return this.departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
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

}