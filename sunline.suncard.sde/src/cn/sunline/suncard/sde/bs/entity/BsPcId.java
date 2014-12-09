package cn.sunline.suncard.sde.bs.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * BsPcId entity. @author MyEclipse Persistence Tools
 */
@Embeddable
public class BsPcId implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long bankorgId;
	private String pcId;

	// Constructors

	/** default constructor */
	public BsPcId() {
	}

	/** full constructor */
	public BsPcId(Long bankorgId, String pcId) {
		this.bankorgId = bankorgId;
		this.pcId = pcId;
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

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof BsPcId))
			return false;
		BsPcId castOther = (BsPcId) other;

		return ((this.getBankorgId() == castOther.getBankorgId()) || (this
				.getBankorgId() != null && castOther.getBankorgId() != null && this
				.getBankorgId().equals(castOther.getBankorgId())))
				&& ((this.getPcId() == castOther.getPcId()) || (this.getPcId() != null
						&& castOther.getPcId() != null && this.getPcId()
						.equals(castOther.getPcId())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getBankorgId() == null ? 0 : this.getBankorgId().hashCode());
		result = 37 * result
				+ (getPcId() == null ? 0 : this.getPcId().hashCode());
		return result;
	}

}