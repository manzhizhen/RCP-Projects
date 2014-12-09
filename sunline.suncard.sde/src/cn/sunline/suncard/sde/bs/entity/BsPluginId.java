package cn.sunline.suncard.sde.bs.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * BsPluginId entity. @author MyEclipse Persistence Tools
 */
@Embeddable
public class BsPluginId implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long bankorgId;
	private String pcId;
	private String pluginId;

	// Constructors

	/** default constructor */
	public BsPluginId() {
	}

	/** full constructor */
	public BsPluginId(Long bankorgId, String pcId, String pluginId) {
		this.bankorgId = bankorgId;
		this.pcId = pcId;
		this.pluginId = pluginId;
	}

	// Property accessors

	@Column(name = "BANKORG_ID", nullable = false, precision = 10, scale = 0)
	public Long getBankorgId() {
		return this.bankorgId;
	}

	public void setBankorgId(Long bankorgId) {
		this.bankorgId = bankorgId;
	}

	@Column(name = "PC_ID", nullable = false, length = 20)
	public String getPcId() {
		return this.pcId;
	}

	public void setPcId(String pcId) {
		this.pcId = pcId;
	}

	@Column(name = "PLUGIN_ID", nullable = false, length = 10)
	public String getPluginId() {
		return this.pluginId;
	}

	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof BsPluginId))
			return false;
		BsPluginId castOther = (BsPluginId) other;

		return ((this.getBankorgId() == castOther.getBankorgId()) || (this
				.getBankorgId() != null && castOther.getBankorgId() != null && this
				.getBankorgId().equals(castOther.getBankorgId())))
				&& ((this.getPcId() == castOther.getPcId()) || (this.getPcId() != null
						&& castOther.getPcId() != null && this.getPcId()
						.equals(castOther.getPcId())))
				&& ((this.getPluginId() == castOther.getPluginId()) || (this
						.getPluginId() != null
						&& castOther.getPluginId() != null && this
						.getPluginId().equals(castOther.getPluginId())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getBankorgId() == null ? 0 : this.getBankorgId().hashCode());
		result = 37 * result
				+ (getPcId() == null ? 0 : this.getPcId().hashCode());
		result = 37 * result
				+ (getPluginId() == null ? 0 : this.getPluginId().hashCode());
		return result;
	}

}