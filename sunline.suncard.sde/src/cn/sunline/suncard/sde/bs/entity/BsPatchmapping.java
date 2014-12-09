package cn.sunline.suncard.sde.bs.entity;

import java.sql.Timestamp;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BsPatchmapping entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BS_PATCHMAPPING")
public class BsPatchmapping implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields

	private BsPatchmappingId id;
	private Timestamp modiDate;
	private String modiUser;
	private Integer version;

	// Constructors

	/** default constructor */
	public BsPatchmapping() {
	}

	/** minimal constructor */
	public BsPatchmapping(BsPatchmappingId id) {
		this.id = id;
	}

	/** full constructor */
	public BsPatchmapping(BsPatchmappingId id, Timestamp modiDate,
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
			@AttributeOverride(name = "patchId", column = @Column(name = "PATCH_ID", nullable = false, length = 10)),
			@AttributeOverride(name = "pluginVer", column = @Column(name = "PLUGIN_VER", nullable = false, length = 10)) })
	public BsPatchmappingId getId() {
		return this.id;
	}

	public void setId(BsPatchmappingId id) {
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