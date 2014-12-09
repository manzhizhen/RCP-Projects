package cn.sunline.suncard.sde.bs.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * BsPluginlogId entity. @author MyEclipse Persistence Tools
 */
@Embeddable
public class BsPluginlogId implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long bankorgId;
	private String pcId;
	private Long logSeq;

	// Constructors

	/** default constructor */
	public BsPluginlogId() {
	}

	/** full constructor */
	public BsPluginlogId(Long bankorgId, String pcId, Long logSeq) {
		this.bankorgId = bankorgId;
		this.pcId = pcId;
		this.logSeq = logSeq;
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

	@Column(name = "LOG_SEQ", nullable = false, precision = 10, scale = 0)
	public Long getLogSeq() {
		return this.logSeq;
	}

	public void setLogSeq(Long logSeq) {
		this.logSeq = logSeq;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof BsPluginlogId))
			return false;
		BsPluginlogId castOther = (BsPluginlogId) other;

		return ((this.getBankorgId() == castOther.getBankorgId()) || (this
				.getBankorgId() != null && castOther.getBankorgId() != null && this
				.getBankorgId().equals(castOther.getBankorgId())))
				&& ((this.getPcId() == castOther.getPcId()) || (this.getPcId() != null
						&& castOther.getPcId() != null && this.getPcId()
						.equals(castOther.getPcId())))
				&& ((this.getLogSeq() == castOther.getLogSeq()) || (this
						.getLogSeq() != null && castOther.getLogSeq() != null && this
						.getLogSeq().equals(castOther.getLogSeq())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getBankorgId() == null ? 0 : this.getBankorgId().hashCode());
		result = 37 * result
				+ (getPcId() == null ? 0 : this.getPcId().hashCode());
		result = 37 * result
				+ (getLogSeq() == null ? 0 : this.getLogSeq().hashCode());
		return result;
	}

}