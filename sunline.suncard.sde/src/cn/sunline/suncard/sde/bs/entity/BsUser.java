package cn.sunline.suncard.sde.bs.entity;

import java.sql.Timestamp;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BsUser entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BS_USER")
public class BsUser implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields

	private BsUserId id;
	private String userName;
	private String departmentId;
	private String password;
	private String userStatus;
	private Timestamp lasetLogginDate;
	private Timestamp modiDate;
	private String modiUser;
	private Integer version;

	// Constructors

	/** default constructor */
	public BsUser() {
	}

	/** minimal constructor */
	public BsUser(BsUserId id) {
		this.id = id;
	}

	/** full constructor */
	public BsUser(BsUserId id, String userName, String departmentId,
			String password, String userStatus, Timestamp lasetLogginDate,
			Timestamp modiDate, String modiUser, Integer version) {
		this.id = id;
		this.userName = userName;
		this.departmentId = departmentId;
		this.password = password;
		this.userStatus = userStatus;
		this.lasetLogginDate = lasetLogginDate;
		this.modiDate = modiDate;
		this.modiUser = modiUser;
		this.version = version;
	}

	// Property accessors
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "bankorgId", column = @Column(name = "BANKORG_ID", nullable = false, precision = 10, scale = 0)),
			@AttributeOverride(name = "userId", column = @Column(name = "USER_ID", nullable = false, length = 10)) })
	public BsUserId getId() {
		return this.id;
	}

	public void setId(BsUserId id) {
		this.id = id;
	}

	@Column(name = "USER_NAME", length = 40)
	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "DEPARTMENT_ID", length = 10)
	public String getDepartmentId() {
		return this.departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	@Column(name = "PASSWORD", length = 40)
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "USER_STATUS", length = 1)
	public String getUserStatus() {
		return this.userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	@Column(name = "LASET_LOGGIN_DATE", length = 0)
	public Timestamp getLasetLogginDate() {
		return this.lasetLogginDate;
	}

	public void setLasetLogginDate(Timestamp lasetLogginDate) {
		this.lasetLogginDate = lasetLogginDate;
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