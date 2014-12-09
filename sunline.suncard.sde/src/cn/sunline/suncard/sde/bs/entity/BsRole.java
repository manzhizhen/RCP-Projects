package cn.sunline.suncard.sde.bs.entity;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * BsRole entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BS_ROLE")
public class BsRole implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields

	private BsRoleId id;
	private String roleName;
	private Timestamp modiDate;
	private String modiUser;
	private Integer version;

	// Constructors
	
	private Set<BsRolemapping> bsRolemappings = new HashSet<BsRolemapping>();

	/** default constructor */
	public BsRole() {
	}

	/** minimal constructor */
	public BsRole(BsRoleId id) {
		this.id = id;
	}

	/** full constructor */
	public BsRole(BsRoleId id, String roleName, Timestamp modiDate,
			String modiUser, Integer version) {
		this.id = id;
		this.roleName = roleName;
		this.modiDate = modiDate;
		this.modiUser = modiUser;
		this.version = version;
	}

	// Property accessors
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "bankorgId", column = @Column(name = "BANKORG_ID", nullable = false, precision = 10, scale = 0)),
			@AttributeOverride(name = "pcId", column = @Column(name = "PC_ID", nullable = false, length = 20)),
			@AttributeOverride(name = "roleId", column = @Column(name = "ROLE_ID", nullable = false, length = 10)) })
	public BsRoleId getId() {
		return this.id;
	}

	public void setId(BsRoleId id) {
		this.id = id;
	}

	@Column(name = "ROLE_NAME", length = 40)
	public String getRoleName() {
		return this.roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
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
	public Set<BsRolemapping> getBsRolemappings() {
		return bsRolemappings;
	}

	public void setBsRolemappings(Set<BsRolemapping> bsRolemappings) {
		this.bsRolemappings = bsRolemappings;
	}
}