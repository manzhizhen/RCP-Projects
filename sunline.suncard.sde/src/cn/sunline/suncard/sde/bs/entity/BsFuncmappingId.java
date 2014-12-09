package cn.sunline.suncard.sde.bs.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * BsFuncmappingId entity. @author MyEclipse Persistence Tools
 */
@Embeddable
public class BsFuncmappingId implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Fields

	private Long bankorgId;
	private String pcId;
	private String functionId;
	private String widgetId;

	// Constructors

	/** default constructor */
	public BsFuncmappingId() {
	}

	/** full constructor */
	public BsFuncmappingId(Long bankorgId, String pcId, String functionId,
			String widgetId) {
		this.bankorgId = bankorgId;
		this.pcId = pcId;
		this.functionId = functionId;
		this.widgetId = widgetId;
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

	@Column(name = "FUNCTION_ID", nullable = false, length = 10)
	public String getFunctionId() {
		return this.functionId;
	}

	public void setFunctionId(String functionId) {
		this.functionId = functionId;
	}

	@Column(name = "WIDGET_ID", nullable = false, length = 10)
	public String getWidgetId() {
		return this.widgetId;
	}

	public void setWidgetId(String widgetId) {
		this.widgetId = widgetId;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof BsFuncmappingId))
			return false;
		BsFuncmappingId castOther = (BsFuncmappingId) other;

		return ((this.getBankorgId() == castOther.getBankorgId()) || (this
				.getBankorgId() != null && castOther.getBankorgId() != null && this
				.getBankorgId().equals(castOther.getBankorgId())))
				&& ((this.getPcId() == castOther.getPcId()) || (this.getPcId() != null
						&& castOther.getPcId() != null && this.getPcId()
						.equals(castOther.getPcId())))
				&& ((this.getFunctionId() == castOther.getFunctionId()) || (this
						.getFunctionId() != null
						&& castOther.getFunctionId() != null && this
						.getFunctionId().equals(castOther.getFunctionId())))
				&& ((this.getWidgetId() == castOther.getWidgetId()) || (this
						.getWidgetId() != null
						&& castOther.getWidgetId() != null && this
						.getWidgetId().equals(castOther.getWidgetId())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getBankorgId() == null ? 0 : this.getBankorgId().hashCode());
		result = 37 * result
				+ (getPcId() == null ? 0 : this.getPcId().hashCode());
		result = 37
				* result
				+ (getFunctionId() == null ? 0 : this.getFunctionId()
						.hashCode());
		result = 37 * result
				+ (getWidgetId() == null ? 0 : this.getWidgetId().hashCode());
		return result;
	}

}