package cn.sunline.suncard.sde.bs.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * BsDepartmentId entity. @author MyEclipse Persistence Tools
 */
@Embeddable
public class BsDepartmentId implements java.io.Serializable {

	// Fields

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long bankorgId;
	private String departmentId;

	// Constructors

	/** default constructor */
	public BsDepartmentId() {
	}

	/** full constructor */
	public BsDepartmentId(Long bankorgId, String departmentId) {
		this.bankorgId = bankorgId;
		this.departmentId = departmentId;
	}

	// Property accessors

	@Column(name = "BANKORG_ID", nullable = false, precision = 10, scale = 0)
	public Long getBankorgId() {
		return this.bankorgId;
	}

	public void setBankorgId(Long bankorgId) {
		this.bankorgId = bankorgId;
	}

	@Column(name = "DEPARTMENT_ID", nullable = false, length = 10)
	public String getDepartmentId() {
		return this.departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof BsDepartmentId))
			return false;
		BsDepartmentId castOther = (BsDepartmentId) other;

		return ((this.getBankorgId() == castOther.getBankorgId()) || (this
				.getBankorgId() != null && castOther.getBankorgId() != null && this
				.getBankorgId().equals(castOther.getBankorgId())))
				&& ((this.getDepartmentId() == castOther.getDepartmentId()) || (this
						.getDepartmentId() != null
						&& castOther.getDepartmentId() != null && this
						.getDepartmentId().equals(castOther.getDepartmentId())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getBankorgId() == null ? 0 : this.getBankorgId().hashCode());
		result = 37
				* result
				+ (getDepartmentId() == null ? 0 : this.getDepartmentId()
						.hashCode());
		return result;
	}

}