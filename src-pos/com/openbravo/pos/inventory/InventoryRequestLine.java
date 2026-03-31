package com.openbravo.pos.inventory;

import com.openbravo.format.Formats;
import com.openbravo.pos.ticket.ProductInfoExt;
import com.openbravo.pos.util.StringUtils;

public class InventoryRequestLine {
  private String reference;
  
  private String m_sProdID;
  
  private String m_sProdName;
  
  private double inv;
  
  private double min;
  
  private double max;
  
  private double unidadesSolicitadas;
  
  public InventoryRequestLine(ProductInfoExt oProduct, double unidadesSolicitadas) {
    this.m_sProdID = oProduct.getID();
    this.m_sProdName = oProduct.getName();
    this.reference = oProduct.getReference();
    this.unidadesSolicitadas = unidadesSolicitadas;
    this.inv = 0.0D;
    this.min = 0.0D;
    this.max = 0.0D;
  }
  
  public String getProductID() {
    return this.m_sProdID;
  }
  
  public String getProductName() {
    return this.m_sProdName;
  }
  
  public void setProductName(String sValue) {
    if (this.m_sProdID == null)
      this.m_sProdName = sValue; 
  }
  
  public double getUnidadesSolicitadas() {
    return this.unidadesSolicitadas;
  }
  
  public void setUnidadesSolicitadas(double dValue) {
    this.unidadesSolicitadas = dValue;
  }
  
  public double getMax() {
    return this.max;
  }
  
  public void setMax(double dValue) {
    this.max = dValue;
  }
  
  public double getInventario() {
    return this.inv;
  }
  
  public void setInventario(double dValue) {
    this.inv = dValue;
  }
  
  public double getMin() {
    return this.min;
  }
  
  public void setMin(double dValue) {
    this.min = dValue;
  }
  
  public double getFaltante() {
    return this.min - this.inv;
  }
  
  public String printName() {
    return StringUtils.encodeXML(this.m_sProdName);
  }
  
  public String printUnidades() {
    return Formats.DOUBLE.formatValue(new Double(getUnidadesSolicitadas()));
  }
  
  public String printFaltante() {
    return Formats.CURRENCY.formatValue(new Double(getFaltante()));
  }
  
  public String getProductReference() {
    return this.reference;
  }
}
