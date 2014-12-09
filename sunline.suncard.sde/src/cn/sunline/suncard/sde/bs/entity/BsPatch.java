package cn.sunline.suncard.sde.bs.entity;

import java.sql.Timestamp;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BsPatch entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BS_PATCH")
public class BsPatch implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields

	private BsPatchId id;
	private String pluginId;
	private String patchVer;
	private Timestamp modiDate;
	private String modiUser;
	private Integer version;
	private String patchName;

	// Constructors

	/** default constructor */
	public BsPatch() {
	}

	/** minimal constructor */
	public BsPatch(BsPatchId id) {
		this.id = id;
	}

	/** full constructor */
	public BsPatch(BsPatchId id, String pluginId, String patchVer,
			Timestamp modiDate, String modiUser, Integer version, String patchName) {
		this.id = id;
		this.pluginId = pluginId;
		this.patchVer = patchVer;
		this.modiDate = modiDate;
		this.modiUser = modiUser;
		this.version = version;
		this.patchName = patchName;
	}

	// Property accessors
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "bankorgId", column = @Column(name = "BANKORG_ID", nullable = false, precision = 10, scale = 0)),
			@AttributeOverride(name = "pcId", column = @Column(name = "PC_ID", nullable = false, length = 20)),
			@AttributeOverride(name = "patchId", column = @Column(name = "PATCH_ID", nullable = false, length = 10)) })
	public BsPatchId getId() {
		return this.id;
	}

	public void setId(BsPatchId id) {
		this.id = id;
	}

	@Column(name = "PLUGIN_ID", length = 10)
	public String getPluginId() {
		return this.pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	@Column(name = "PATCH_VER", length = 10)
	public String getPatchVer() {
		return this.patchVer;
	}

	public void setPatchVer(String patchVer) {
		this.patchVer = patchVer;
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

	@Column(name = "PATCH_NAME", length = 40)
	public String getPatchName() {
		return this.patchName;
	}

	public void setPatchName(String patchName) {
		this.patchName = patchName;
	}

}