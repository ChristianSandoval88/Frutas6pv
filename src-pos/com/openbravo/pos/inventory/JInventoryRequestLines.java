package com.openbravo.pos.inventory;

import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppLocal;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

public class JInventoryRequestLines extends JPanel {
  private InventoryTableModel m_inventorylines;
  
  private JScrollPane jScrollPane1;
  
  private JTable m_tableinventory;
  
  public JInventoryRequestLines() {
    initComponents();
    DefaultTableColumnModel columns = new DefaultTableColumnModel();
    TableColumn c = new TableColumn(0, 180, new DataCellRenderer(2), new DefaultCellEditor(new JTextField()));
    c.setHeaderValue(AppLocal.getIntString("label.item"));
    columns.addColumn(c);
    c = new TableColumn(1, 75, new DataCellRenderer(4), new DefaultCellEditor(new JTextField()));
    c.setHeaderValue(AppLocal.getIntString("label.units"));
    columns.addColumn(c);
    c = new TableColumn(2, 75, new DataCellRenderer(4), new DefaultCellEditor(new JTextField()));
    c.setHeaderValue("Inventario");
    columns.addColumn(c);
    c = new TableColumn(3, 75, new DataCellRenderer(4), new DefaultCellEditor(new JTextField()));
    c.setHeaderValue("Minimo");
    columns.addColumn(c);
    c = new TableColumn(4, 75, new DataCellRenderer(4), new DefaultCellEditor(new JTextField()));
    c.setHeaderValue("Maximo");
    columns.addColumn(c);
    c = new TableColumn(5, 75, new DataCellRenderer(4), new DefaultCellEditor(new JTextField()));
    c.setHeaderValue("Inv. Final");
    columns.addColumn(c);
    this.m_tableinventory.setColumnModel(columns);
    this.m_tableinventory.getTableHeader().setReorderingAllowed(false);
    this.m_tableinventory.setRowHeight(40);
    this.m_tableinventory.getSelectionModel().setSelectionMode(0);
    this.m_tableinventory.setIntercellSpacing(new Dimension(0, 1));
    this.m_inventorylines = new InventoryTableModel();
    this.m_tableinventory.setModel(this.m_inventorylines);
  }
  
  public void clear() {
    this.m_inventorylines.clear();
  }
  
  public void addLine(InventoryRequestLine i) {
    this.m_inventorylines.addRow(i);
    setSelectedIndex(this.m_inventorylines.getRowCount() - 1);
  }
  
  public void deleteLine(int index) {
    this.m_inventorylines.removeRow(index);
    if (index >= this.m_inventorylines.getRowCount())
      index = this.m_inventorylines.getRowCount() - 1; 
    if (index >= 0 && index < this.m_inventorylines.getRowCount())
      setSelectedIndex(index); 
  }
  
  public void setLine(int index, InventoryRequestLine i) {
    this.m_inventorylines.setRow(index, i);
    setSelectedIndex(index);
  }
  
  public InventoryRequestLine getLine(int index) {
    return this.m_inventorylines.getRow(index);
  }
  
  public List<InventoryRequestLine> getLines() {
    return this.m_inventorylines.getLines();
  }
  
  public int getCount() {
    return this.m_inventorylines.getRowCount();
  }
  
  public int getSelectedRow() {
    return this.m_tableinventory.getSelectedRow();
  }
  
  public void setSelectedIndex(int i) {
    this.m_tableinventory.getSelectionModel().setSelectionInterval(i, i);
    Rectangle oRect = this.m_tableinventory.getCellRect(i, 0, true);
    this.m_tableinventory.scrollRectToVisible(oRect);
  }
  
  public void goDown() {
    int i = this.m_tableinventory.getSelectionModel().getMaxSelectionIndex();
    if (i < 0) {
      i = 0;
    } else {
      i++;
      if (i >= this.m_inventorylines.getRowCount())
        i = this.m_inventorylines.getRowCount() - 1; 
    } 
    if (i >= 0 && i < this.m_inventorylines.getRowCount())
      setSelectedIndex(i); 
  }
  
  public void goUp() {
    int i = this.m_tableinventory.getSelectionModel().getMinSelectionIndex();
    if (i < 0) {
      i = this.m_inventorylines.getRowCount() - 1;
    } else {
      i--;
      if (i < 0)
        i = 0; 
    } 
    if (i >= 0 && i < this.m_inventorylines.getRowCount())
      setSelectedIndex(i); 
  }
  
  private static class InventoryTableModel extends AbstractTableModel {
    private ArrayList<InventoryRequestLine> m_rows = new ArrayList<>();
    
    public int getRowCount() {
      return this.m_rows.size();
    }
    
    public int getColumnCount() {
      return 6;
    }
    
    public String getColumnName(int column) {
      return "a";
    }
    
    public Object getValueAt(int row, int column) {
      InventoryRequestLine i = this.m_rows.get(row);
      switch (column) {
        case 0:
          return i.getProductName();
        case 1:
          return Formats.DOUBLE.formatValue(Double.valueOf(i.getUnidadesSolicitadas()));
        case 2:
          return Formats.DOUBLE.formatValue(Double.valueOf(i.getInventario()));
        case 3:
          return Formats.DOUBLE.formatValue(Double.valueOf(i.getMin()));
        case 4:
          return Formats.DOUBLE.formatValue(Double.valueOf(i.getMax()));
        case 5:
          return Formats.DOUBLE.formatValue(Double.valueOf(i.getUnidadesSolicitadas() + i.getInventario()));
      } 
      return null;
    }
    
    public boolean isCellEditable(int row, int column) {
      return false;
    }
    
    public void clear() {
      int old = getRowCount();
      if (old > 0) {
        this.m_rows.clear();
        fireTableRowsDeleted(0, old - 1);
      } 
    }
    
    public List<InventoryRequestLine> getLines() {
      return this.m_rows;
    }
    
    public InventoryRequestLine getRow(int index) {
      return this.m_rows.get(index);
    }
    
    public void setRow(int index, InventoryRequestLine oLine) {
      this.m_rows.set(index, oLine);
      fireTableRowsUpdated(index, index);
    }
    
    public void addRow(InventoryRequestLine oLine) {
      insertRow(this.m_rows.size(), oLine);
    }
    
    public void insertRow(int index, InventoryRequestLine oLine) {
      this.m_rows.add(index, oLine);
      fireTableRowsInserted(index, index);
    }
    
    public void removeRow(int row) {
      this.m_rows.remove(row);
      fireTableRowsDeleted(row, row);
    }
    
    private InventoryTableModel() {}
  }
  
  private static class DataCellRenderer extends DefaultTableCellRenderer {
    private int m_iAlignment;
    
    public DataCellRenderer(int align) {
      this.m_iAlignment = align;
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      JLabel aux = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
      aux.setVerticalAlignment(1);
      aux.setHorizontalAlignment(this.m_iAlignment);
      if (!isSelected)
        aux.setBackground(UIManager.getDefaults().getColor("TextField.disabledBackground")); 
      return aux;
    }
  }
  
  private void initComponents() {
    this.jScrollPane1 = new JScrollPane();
    this.m_tableinventory = new JTable();
    setLayout(new BorderLayout());
    this.jScrollPane1.setPreferredSize(new Dimension(552, 402));
    this.m_tableinventory.setAutoCreateColumnsFromModel(false);
    this.m_tableinventory.setAutoResizeMode(0);
    this.m_tableinventory.setFocusable(false);
    this.m_tableinventory.setRequestFocusEnabled(false);
    this.m_tableinventory.setShowVerticalLines(false);
    this.jScrollPane1.setViewportView(this.m_tableinventory);
    add(this.jScrollPane1, "Center");
  }
}
