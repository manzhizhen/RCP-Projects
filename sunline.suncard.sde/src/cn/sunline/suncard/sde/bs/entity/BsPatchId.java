package cn.sunline.suncard.sde.bs.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * BsPatchId entity. @author MyEclipse Persistence Tools
 */
@Embeddable
public class BsPatchId implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long bankorgId;
	private String pcId;
	private String patchId;

	// Constructors

	/** default constructor */
	public BsPatchId() {
	}

	/** full constructor */
	public BsPatchId(Long bankorgId, String pcId, String patchId) {
		this.bankorgId = bankorgId;
		this.pcId = pcId;
		this.patchId = patchId;
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

	@Column(name = "PATCH_ID", nullable = false, length = 10)
	public String getPatchId() {
		return this.patchId;
	}

	public void setPatchId(String patchId) {
		this.patchId = patchId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof BsPatchId))
			return false;
		BsPatchId castOther = (BsPatchId) other;

		return ((this.getBankorgId() == castOther.getBankorgId()) || (this
				.getBankorgId() != null && castOther.getBankorgId() != null && this
				.getBankorgId().equals(castOther.getBankorgId())))
				&& ((this.getPcId() == castOther.getPcId()) || (this.getPcId() != null
						&& castOther.getPcId() != null && this.getPcId()
						.equals(castOther.getPcId())))
				&& ((this.getPatchId() == castOther.getPatchId()) || (this
						.getPatchId() != null && castOther.getPatchId() != null && this
						.getPatchId().equals(castOther.getPatchId())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getBankorgId() == null ? 0 : this.getBankorgId().hashCode());
		result = 37 * result
				+ (getPcId() == null ? 0 : this.getPcId().hashCode());
		result = 37 * result
				+ (getPatchId() == null ? 0 : this.getPatchId().hashCode());
		return result;
	}

}