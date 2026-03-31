package com.openbravo.pos.inventory;

import com.openbravo.format.Formats;
import java.util.Date;
import java.util.List;

public class InventoryRequestRecord {
  private List<InventoryRequestLine> m_invlines;
  
  public InventoryRequestRecord(List<InventoryRequestLine> invlines) {
    this.m_invlines = invlines;
  }
  
  public List<InventoryRequestLine> getLines() {
    return this.m_invlines;
  }
  
  public String printDate() {
    return Formats.TIMESTAMP.formatValue(new Date());
  }
}
