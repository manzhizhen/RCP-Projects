package cn.sunline.suncard.sde.bs.entity;

import java.sql.Timestamp;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * BsPlugin entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "BS_PLUGIN")
public class BsPlugin implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields

	private BsPluginId id;
	private String pluginName;
	private String pluginVer;
	private Timestamp modiDate;
	private String modiUser;
	private Integer version;

	// Constructors

	/** default constructor */
	public BsPlugin() {
	}

	/** minimal constructor */
	public BsPlugin(BsPluginId id) {
		this.id = id;
	}

	/** full constructor */
	public BsPlugin(BsPluginId id, String pluginName, String pluginVer,
			Timestamp modiDate, String modiUser, Integer version) {
		this.id = id;
		this.pluginName = pluginName;
		this.pluginVer = pluginVer;
		this.modiDate = modiDate;
		this.modiUser = modiUser;
		this.version = version;
	}

	// Property accessors
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "bankorgId", column = @Column(name = "BANKORG_ID", nullable = false, precision = 10, scale = 0)),
			@AttributeOverride(name = "pcId", column = @Column(name = "PC_ID", nullable = false, length = 20)),
			@AttributeOverride(name = "pluginId", column = @Column(name = "PLUGIN_ID", nullable = false, length = 10)) })
	public BsPluginId getId() {
		return this.id;
	}

	public void setId(BsPluginId id) {
		this.id = id;
	}

	@Column(name = "PLUGIN_NAME", length = 40)
	public String getPluginName() {
		return this.pluginName;
	}

	public void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	@Column(name = "PLUGIN_VER", length = 10)
	public String getPluginVer() {
		return this.pluginVer;
	}

	public void setPluginVer(String pluginVer) {
		this.pluginVer = pluginVer;
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