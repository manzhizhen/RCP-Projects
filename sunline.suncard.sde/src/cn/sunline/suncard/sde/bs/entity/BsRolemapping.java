package cn.sunline.suncard.sde.bs.entity;

import java.sql.Timestamp;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BsRolemapping entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BS_ROLEMAPPING")
public class BsRolemapping implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields

	private BsRolemappingId id;
	private Timestamp modiDate;
	private String modiUser;
	private Integer version;

	// Constructors

	/** default constructor */
	public BsRolemapping() {
	}

	/** minimal constructor */
	public BsRolemapping(BsRolemappingId id) {
		this.id = id;
	}

	/** full constructor */
	public BsRolemapping(BsRolemappingId id, Timestamp modiDate,
			String modiUser, Integer version) {
		this.id = id;
		this.modiDate = modiDate;
		this.modiUser = modiUser;
		this.version = version;
	}

	// Property accessors
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "bankorgId", column = @Column(name = "BANKORG_ID", nullable = false, precision = 10, scale = 0)),
			@AttributeOverride(name = "pcId", column = @Column(name = "PC_ID", nullable = false, length = 20)),
			@AttributeOverride(name = "roleId", column = @Column(name = "ROLE_ID", nullable = false, length = 10)),
			@AttributeOverride(name = "functionId", column = @Column(name = "FUNCTION_ID", nullable = false, length = 10)) })
	public BsRolemappingId getId() {
		return this.id;
	}

	public void setId(BsRolemappingId id) {
		this.id = id;
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