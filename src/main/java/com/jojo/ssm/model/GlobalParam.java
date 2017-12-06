package com.jojo.ssm.model;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "global_param")
public class GlobalParam implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(name = "param_key")
    private String paramKey;

    @Column(name = "param_desc")
    private String paramDesc;

    @Column(name = "param_value")
    private String paramValue;

    @Column(name = "param_type")
    private String paramType;

    @Column(name = "is_build_in")
    private Integer isBuildIn;

    private static final long serialVersionUID = 1L;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return param_key
     */
    public String getParamKey() {
        return paramKey;
    }

    /**
     * @param paramKey
     */
    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    /**
     * @return param_desc
     */
    public String getParamDesc() {
        return paramDesc;
    }

    /**
     * @param paramDesc
     */
    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }

    /**
     * @return param_value
     */
    public String getParamValue() {
        return paramValue;
    }

    /**
     * @param paramValue
     */
    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    /**
     * @return param_type
     */
    public String getParamType() {
        return paramType;
    }

    /**
     * @param paramType
     */
    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    /**
     * @return is_build_in
     */
    public Integer getIsBuildIn() {
        return isBuildIn;
    }

    /**
     * @param isBuildIn
     */
    public void setIsBuildIn(Integer isBuildIn) {
        this.isBuildIn = isBuildIn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", paramKey=").append(paramKey);
        sb.append(", paramDesc=").append(paramDesc);
        sb.append(", paramValue=").append(paramValue);
        sb.append(", paramType=").append(paramType);
        sb.append(", isBuildIn=").append(isBuildIn);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        GlobalParam other = (GlobalParam) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getParamKey() == null ? other.getParamKey() == null : this.getParamKey().equals(other.getParamKey()))
            && (this.getParamDesc() == null ? other.getParamDesc() == null : this.getParamDesc().equals(other.getParamDesc()))
            && (this.getParamValue() == null ? other.getParamValue() == null : this.getParamValue().equals(other.getParamValue()))
            && (this.getParamType() == null ? other.getParamType() == null : this.getParamType().equals(other.getParamType()))
            && (this.getIsBuildIn() == null ? other.getIsBuildIn() == null : this.getIsBuildIn().equals(other.getIsBuildIn()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getParamKey() == null) ? 0 : getParamKey().hashCode());
        result = prime * result + ((getParamDesc() == null) ? 0 : getParamDesc().hashCode());
        result = prime * result + ((getParamValue() == null) ? 0 : getParamValue().hashCode());
        result = prime * result + ((getParamType() == null) ? 0 : getParamType().hashCode());
        result = prime * result + ((getIsBuildIn() == null) ? 0 : getIsBuildIn().hashCode());
        return result;
    }
}