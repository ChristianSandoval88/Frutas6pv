package com.openbravo.pos.inventory;

import com.openbravo.basic.BasicException;
import com.openbravo.beans.JCalendarDialog;
import com.openbravo.beans.JNumberEvent;
import com.openbravo.beans.JNumberEventListener;
import com.openbravo.beans.JNumberKeys;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.LocalRes;
import com.openbravo.data.loader.SentenceExec;
import com.openbravo.format.Formats;
import com.openbravo.pos.catalog.CatalogSelector;
import com.openbravo.pos.catalog.JCatalog;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.forms.JPanelView;
import com.openbravo.pos.panels.JProductFinder;
import com.openbravo.pos.printer.TicketParser;
import com.openbravo.pos.printer.TicketPrinterException;
import com.openbravo.pos.sales.JPanelTicket;
import com.openbravo.pos.scanpal2.DeviceScanner;
import com.openbravo.pos.scanpal2.DeviceScannerException;
import com.openbravo.pos.scanpal2.ProductDownloaded;
import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.scripting.ScriptFactory;
import com.openbravo.pos.ticket.ProductInfoExt;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class StockRequest extends JPanel implements JPanelView {
  private AppView m_App;
  
  private DataLogicSystem m_dlSystem;
  
  private DataLogicSales m_dlSales;
  
  private TicketParser m_TTP;
  
  private CatalogSelector m_cat;
  
  private JInventoryRequestLines m_invlines;
  
  private int NUMBER_STATE = 0;
  
  private int MULTIPLY = 0;
  
  private static int DEFAULT = 0;
  
  private static int ACTIVE = 1;
  
  private static int DECIMAL = 2;
  
  private JButton btnDownloadProducts;
  
  private JPanel catcontainer;
  
  private JButton jEditAttributes;
  
  private JLabel jLabel1;
  
  private JLabel jLabel2;
  
  private JLabel jLabel8;
  
  private JNumberKeys jNumberKeys;
  
  private JPanel jPanel1;
  
  private JPanel jPanel2;
  
  private JPanel jPanel3;
  
  private JPanel jPanel4;
  
  private JPanel jPanel5;
  
  private JPanel jPanel6;
  
  private JTextField jTextField1;
  
  private JButton m_jDelete;
  
  private JButton m_jDelete1;
  
  private JButton m_jDelete2;
  
  private JButton m_jDelete3;
  
  private JButton m_jDown;
  
  private JButton m_jEnter;
  
  private JComboBox m_jLocation;
  
  private JComboBox m_jLocationDes;
  
  private JButton m_jUp;
  
  private JButton m_jbtndate;
  
  private JLabel m_jcodebar;
  
  private JTextField m_jdate;
  
  private JComboBox m_jreason;
  
  public StockRequest(AppView app) {
    this.m_App = app;
    this.m_dlSystem = (DataLogicSystem)this.m_App.getBean("com.openbravo.pos.forms.DataLogicSystem");
    this.m_dlSales = (DataLogicSales)this.m_App.getBean("com.openbravo.pos.forms.DataLogicSales");
    this.m_TTP = new TicketParser(this.m_App.getDeviceTicket(), this.m_dlSystem);
    initComponents();
    this.m_cat = (CatalogSelector)new JCatalog(this.m_dlSales);
    this.m_cat.getComponent().setPreferredSize(new Dimension(0, 245));
    this.m_cat.addActionListener(new CatalogListener());
    this.catcontainer.add(this.m_cat.getComponent(), "Center");
    this.m_invlines = new JInventoryRequestLines();
    this.jPanel5.add(this.m_invlines, "Center");
  }
  
  public String getTitle() {
    return AppLocal.getIntString("Menu.StockRequest");
  }
  
  public JComponent getComponent() {
    return this;
  }
  
  public void activate() throws BasicException {
    this.m_cat.loadCatalog();
    stateToInsert();
    EventQueue.invokeLater(new Runnable() {
          public void run() {
            StockRequest.this.jTextField1.requestFocus();
          }
        });
  }
  
  public void stateToInsert() {
    this.m_invlines.clear();
    this.m_jcodebar.setText((String)null);
  }
  
  public boolean deactivate() {
    if (this.m_invlines.getCount() > 0) {
      int res = JOptionPane.showConfirmDialog(this, LocalRes.getIntString("message.wannasave"), LocalRes.getIntString("title.editor"), 1, 3);
      if (res == 0) {
        saveData();
        return true;
      } 
      if (res == 1)
        return true; 
      return false;
    } 
    return true;
  }
  
  private void addLine(ProductInfoExt oProduct, double unidadesSolicitadas, double inv, double min, double max) {
    InventoryRequestLine i = new InventoryRequestLine(oProduct, unidadesSolicitadas);
    i.setInventario(inv);
    i.setMin(min);
    i.setMax(max);
    this.m_invlines.addLine(i);
  }
  
  private void deleteLine(int index) {
    if (index < 0) {
      Toolkit.getDefaultToolkit().beep();
    } else {
      this.m_invlines.deleteLine(index);
    } 
  }
  
  private void incProduct(ProductInfoExt product, double units) {
    try {
      double inventario = this.m_dlSales.findProductStock("0", product.getID(), null);
      double min = this.m_dlSales.findMinProductStock("0", product.getID());
      double max = this.m_dlSales.findMaxProductStock("0", product.getID());
      addLine(product, units, inventario, min, max);
    } catch (BasicException ex) {
      Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
    } 
  }
  
  private void incProductByCode(String sCode) {
    incProductByCode(sCode, 1.0D);
  }
  
  private void incProductByCode(String sCode, double dQuantity) {
    try {
      ProductInfoExt oProduct = this.m_dlSales.getProductInfoByCode(sCode);
      if (oProduct == null) {
        Toolkit.getDefaultToolkit().beep();
      } else {
        incProduct(oProduct, dQuantity);
      } 
    } catch (BasicException eData) {
      MessageInf msg = new MessageInf((Throwable)eData);
      msg.show(this);
    } 
  }
  
  private void addUnits(double dUnits) {
    int i = this.m_invlines.getSelectedRow();
    if (i >= 0) {
      InventoryRequestLine inv = this.m_invlines.getLine(i);
      double dunits = inv.getUnidadesSolicitadas() + dUnits;
      if (dunits <= 0.0D) {
        deleteLine(i);
      } else {
        inv.setUnidadesSolicitadas(inv.getUnidadesSolicitadas() + dUnits);
        this.m_invlines.setLine(i, inv);
      } 
    } 
  }
  
  private void setUnits(double dUnits) {
    int i = this.m_invlines.getSelectedRow();
    if (i >= 0) {
      InventoryRequestLine inv = this.m_invlines.getLine(i);
      inv.setUnidadesSolicitadas(dUnits);
      this.m_invlines.setLine(i, inv);
    } 
  }
  
  private void stateTransition(char cTrans) {
    if (cTrans == '\n') {
      try {
        ProductInfoExt oProduct = this.m_dlSales.getProductInfoByCode(this.m_jcodebar.getText());
        if (oProduct != null) {
          double inventario = this.m_dlSales.findProductStock("0", oProduct.getID(), null);
          double min = this.m_dlSales.findMinProductStock("0", oProduct.getID());
          double max = this.m_dlSales.findMaxProductStock("0", oProduct.getID());
          double faltante = min - inventario;
          String units = JOptionPane.showInputDialog(null, "<html>Producto: " + oProduct
              .getName() + "<br>Inv. Actual: " + Formats.DOUBLE
              .formatValue(Double.valueOf(inventario)) + "<br>Minimo: " + Formats.DOUBLE
              .formatValue(Double.valueOf(min)) + "<br>Maximo: " + Formats.DOUBLE
              .formatValue(Double.valueOf(max)) + "<br>Faltante: " + Formats.DOUBLE
              .formatValue(Double.valueOf(faltante)) + "<br>Ingrese la cantiadad:", Formats.DOUBLE
              .formatValue(Double.valueOf(faltante)));
          try {
            double d = Double.parseDouble(units);
            incProductByCode(this.m_jcodebar.getText(), d);
          } catch (Exception exception) {}
        } 
      } catch (BasicException ex) {
        Logger.getLogger(StockManagement.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
      } 
      this.m_jcodebar.setText((String)null);
    } else if (cTrans == '') {
      this.m_jcodebar.setText((String)null);
      this.NUMBER_STATE = DEFAULT;
    } else if (cTrans == '*') {
      this.MULTIPLY = ACTIVE;
    } else if (cTrans == '+') {
      if (this.MULTIPLY != DEFAULT && this.NUMBER_STATE != DEFAULT) {
        setUnits(Double.parseDouble(this.m_jcodebar.getText()));
        this.m_jcodebar.setText((String)null);
      } else if (this.m_jcodebar.getText() == null || this.m_jcodebar.getText().equals("")) {
        addUnits(1.0D);
      } else {
        addUnits(Double.parseDouble(this.m_jcodebar.getText()));
        this.m_jcodebar.setText((String)null);
      } 
      this.NUMBER_STATE = DEFAULT;
      this.MULTIPLY = DEFAULT;
    } else if (cTrans == '-') {
      if (this.m_jcodebar.getText() == null || this.m_jcodebar.getText().equals("")) {
        addUnits(-1.0D);
      } else {
        addUnits(-Double.parseDouble(this.m_jcodebar.getText()));
        this.m_jcodebar.setText((String)null);
      } 
      this.NUMBER_STATE = DEFAULT;
      this.MULTIPLY = DEFAULT;
    } else if (cTrans == '=') {
      if (this.m_invlines.getCount() == 0) {
        Toolkit.getDefaultToolkit().beep();
      } else {
        int res = JOptionPane.showConfirmDialog(this, "Esta seguro de realizar esta captura?", "Mensaje", 1, 3);
        if (res == 0)
          saveData(); 
      } 
    } else if (this.m_jcodebar.getText() == null) {
      this.m_jcodebar.setText("" + cTrans);
    } else {
      this.m_jcodebar.setText(this.m_jcodebar.getText() + cTrans);
    } 
  }
  
  private void saveData() {
    int i;
    for (i = 0; i < this.m_invlines.getCount(); i++) {
      for (int j = i + 1; j < this.m_invlines.getCount(); j++) {
        if (((InventoryRequestLine)this.m_invlines.getLines().get(i)).printName().equals(((InventoryRequestLine)this.m_invlines.getLines().get(j)).printName())) {
          JOptionPane.showMessageDialog(this, "El producto " + ((InventoryRequestLine)this.m_invlines
              .getLines().get(i)).printName() + " se encuentra duplicado en el pedido.");
          return;
        } 
      } 
    } 
    for (i = 0; i < this.m_invlines.getCount(); i++) {
      InventoryRequestLine inv = this.m_invlines.getLines().get(i);
      if (inv.getUnidadesSolicitadas() <= 0.0D) {
        JOptionPane.showMessageDialog(this, "Valide la cantidad solicitada del producto " + inv
            .printName() + ".");
        return;
      } 
      if (inv.getMax() > 0.0D && inv.getInventario() + inv.getUnidadesSolicitadas() > inv.getMax()) {
        JOptionPane.showMessageDialog(this, "El producto " + inv
            .printName() + " rebasarel m(" + Formats.DOUBLE.formatValue(Double.valueOf(inv.getMax())) + ")");
        return;
      } 
    } 
    try {
      saveData(new InventoryRequestRecord(this.m_invlines
            .getLines()));
      stateToInsert();
    } catch (BasicException eData) {
      MessageInf msg = new MessageInf(-67108864, AppLocal.getIntString("message.cannotsaveinventorydata"), eData);
      msg.show(this);
    } 
  }
  
  private void saveData(InventoryRequestRecord rec) throws BasicException {
    SentenceExec sent = this.m_dlSales.getStockRequestInsert();
    for (int i = 0; i < this.m_invlines.getCount(); i++) {
      InventoryRequestLine inv = rec.getLines().get(i);
      sent.exec(new Object[] { UUID.randomUUID().toString(), new Date(), inv
            
            .getProductID(), 
            Double.valueOf(inv.getUnidadesSolicitadas()), 
            Double.valueOf(inv.getInventario()), 
            Double.valueOf(inv.getMin()), 
            Double.valueOf(inv.getMax()), this.m_App
            .getAppUserView().getUser().getUserInfo().getId() });
    } 
    printTicket(rec, this.m_App.getAppUserView().getUser().getUserInfo().getName());
  }
  
  private void printTicket(InventoryRequestRecord invrec, String user) {
    String sresource = this.m_dlSystem.getResourceAsXML("Printer.InventoryRequest");
    if (sresource == null) {
      MessageInf msg = new MessageInf(-33554432, AppLocal.getIntString("message.cannotprintticket"));
      msg.show(this);
    } else {
      try {
        ScriptEngine script = ScriptFactory.getScriptEngine("velocity");
        script.put("request", invrec);
        script.put("user", user);
        script.put("fecha", Formats.TIMESTAMP.formatValue(new Date()));
        this.m_TTP.printTicket(script.eval(sresource).toString());
      } catch (ScriptException e) {
        MessageInf msg = new MessageInf(-33554432, AppLocal.getIntString("message.cannotprintticket"), e);
        msg.show(this);
      } catch (TicketPrinterException e) {
        MessageInf msg = new MessageInf(-33554432, AppLocal.getIntString("message.cannotprintticket"), e);
        msg.show(this);
      } 
    } 
  }
  
  private class CatalogListener implements ActionListener {
    private CatalogListener() {}
    
    public void actionPerformed(ActionEvent e) {
      try {
        ProductInfoExt oProduct = (ProductInfoExt)e.getSource();
        if (oProduct != null) {
          double inventario = StockRequest.this.m_dlSales.findProductStock("0", oProduct.getID(), null);
          double min = StockRequest.this.m_dlSales.findMinProductStock("0", oProduct.getID());
          double max = StockRequest.this.m_dlSales.findMaxProductStock("0", oProduct.getID());
          double faltante = min - inventario;
          String units = JOptionPane.showInputDialog(null, "<html>Producto: " + oProduct
              .getName() + "<br>Inv. Actual: " + Formats.DOUBLE
              .formatValue(Double.valueOf(inventario)) + "<br>Minimo: " + Formats.DOUBLE
              .formatValue(Double.valueOf(min)) + "<br>Maximo: " + Formats.DOUBLE
              .formatValue(Double.valueOf(max)) + "<br>Faltante: " + Formats.DOUBLE
              .formatValue(Double.valueOf(faltante)) + "<br>Ingrese la cantiadad:", Formats.DOUBLE
              .formatValue(Double.valueOf(faltante)));
          try {
            double d = Double.parseDouble(units);
            StockRequest.this.incProductByCode(oProduct.getCode(), d);
          } catch (Exception exception) {}
        } 
      } catch (BasicException ex) {
        Logger.getLogger(StockManagement.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
      } 
      StockRequest.this.m_jcodebar.setText((String)null);
    }
  }
  
  private void initComponents() {
    this.jEditAttributes = new JButton();
    this.m_jDown = new JButton();
    this.m_jUp = new JButton();
    this.jPanel6 = new JPanel();
    this.btnDownloadProducts = new JButton();
    this.m_jDelete2 = new JButton();
    this.jLabel8 = new JLabel();
    this.m_jLocation = new JComboBox();
    this.m_jLocationDes = new JComboBox();
    this.jLabel1 = new JLabel();
    this.m_jdate = new JTextField();
    this.m_jbtndate = new JButton();
    this.jLabel2 = new JLabel();
    this.m_jreason = new JComboBox();
    this.jPanel1 = new JPanel();
    this.jPanel2 = new JPanel();
    this.jNumberKeys = new JNumberKeys();
    this.jPanel4 = new JPanel();
    this.m_jEnter = new JButton();
    this.m_jcodebar = new JLabel();
    this.jTextField1 = new JTextField();
    this.jPanel3 = new JPanel();
    this.m_jDelete = new JButton();
    this.jPanel5 = new JPanel();
    this.m_jDelete1 = new JButton();
    this.m_jDelete3 = new JButton();
    this.catcontainer = new JPanel();
    this.jEditAttributes.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/colorize.png")));
    this.jEditAttributes.setFocusPainted(false);
    this.jEditAttributes.setFocusable(false);
    this.jEditAttributes.setMargin(new Insets(8, 14, 8, 14));
    this.jEditAttributes.setRequestFocusEnabled(false);
    this.jEditAttributes.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            StockRequest.this.jEditAttributesActionPerformed(evt);
          }
        });
    this.m_jDown.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/1downarrow22.png")));
    this.m_jDown.setFocusPainted(false);
    this.m_jDown.setFocusable(false);
    this.m_jDown.setMargin(new Insets(8, 14, 8, 14));
    this.m_jDown.setRequestFocusEnabled(false);
    this.m_jDown.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            StockRequest.this.m_jDownActionPerformed(evt);
          }
        });
    this.m_jUp.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/1uparrow22.png")));
    this.m_jUp.setFocusPainted(false);
    this.m_jUp.setFocusable(false);
    this.m_jUp.setMargin(new Insets(8, 14, 8, 14));
    this.m_jUp.setRequestFocusEnabled(false);
    this.m_jUp.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            StockRequest.this.m_jUpActionPerformed(evt);
          }
        });
    this.btnDownloadProducts.setText("ScanPal");
    this.btnDownloadProducts.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            StockRequest.this.btnDownloadProductsActionPerformed(evt);
          }
        });
    this.jPanel6.add(this.btnDownloadProducts);
    this.m_jDelete2.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/contents.png")));
    this.m_jDelete2.setFocusPainted(false);
    this.m_jDelete2.setFocusable(false);
    this.m_jDelete2.setMargin(new Insets(8, 14, 8, 14));
    this.m_jDelete2.setRequestFocusEnabled(false);
    this.m_jDelete2.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            StockRequest.this.m_jDelete2ActionPerformed(evt);
          }
        });
    this.jLabel8.setText(AppLocal.getIntString("label.warehouse"));
    this.jLabel1.setText(AppLocal.getIntString("label.stockdate"));
    this.m_jbtndate.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/date.png")));
    this.m_jbtndate.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            StockRequest.this.m_jbtndateActionPerformed(evt);
          }
        });
    this.jLabel2.setText(AppLocal.getIntString("label.stockreason"));
    this.m_jreason.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            StockRequest.this.m_jreasonActionPerformed(evt);
          }
        });
    setLayout(new BorderLayout());
    this.jPanel1.setLayout(new BorderLayout());
    this.jPanel2.setLayout(new BoxLayout(this.jPanel2, 1));
    this.jNumberKeys.addJNumberEventListener(new JNumberEventListener() {
          public void keyPerformed(JNumberEvent evt) {
            StockRequest.this.jNumberKeysKeyPerformed(evt);
          }
        });
    this.jPanel2.add((Component)this.jNumberKeys);
    this.jPanel4.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    this.jPanel4.setLayout(new GridBagLayout());
    this.m_jEnter.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/barcode.png")));
    this.m_jEnter.setFocusPainted(false);
    this.m_jEnter.setFocusable(false);
    this.m_jEnter.setRequestFocusEnabled(false);
    this.m_jEnter.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            StockRequest.this.m_jEnterActionPerformed(evt);
          }
        });
    GridBagConstraints gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = 1;
    gridBagConstraints.weightx = 1.0D;
    gridBagConstraints.weighty = 1.0D;
    gridBagConstraints.insets = new Insets(0, 5, 0, 0);
    this.jPanel4.add(this.m_jEnter, gridBagConstraints);
    this.m_jcodebar.setBackground(Color.white);
    this.m_jcodebar.setHorizontalAlignment(4);
    this.m_jcodebar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
    this.m_jcodebar.setOpaque(true);
    this.m_jcodebar.setPreferredSize(new Dimension(135, 30));
    this.m_jcodebar.setRequestFocusEnabled(false);
    this.m_jcodebar.addMouseListener(new MouseAdapter() {
          public void mouseClicked(MouseEvent evt) {
            StockRequest.this.m_jcodebarMouseClicked(evt);
          }
        });
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    this.jPanel4.add(this.m_jcodebar, gridBagConstraints);
    this.jTextField1.setBackground(UIManager.getDefaults().getColor("Panel.background"));
    this.jTextField1.setForeground(UIManager.getDefaults().getColor("Panel.background"));
    this.jTextField1.setCaretColor(UIManager.getDefaults().getColor("Panel.background"));
    this.jTextField1.setPreferredSize(new Dimension(1, 1));
    this.jTextField1.addKeyListener(new KeyAdapter() {
          public void keyTyped(KeyEvent evt) {
            StockRequest.this.jTextField1KeyTyped(evt);
          }
        });
    gridBagConstraints = new GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = 1;
    this.jPanel4.add(this.jTextField1, gridBagConstraints);
    this.jPanel2.add(this.jPanel4);
    this.jPanel1.add(this.jPanel2, "North");
    add(this.jPanel1, "East");
    this.jPanel3.setLayout((LayoutManager)null);
    this.m_jDelete.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/locationbar_erase.png")));
    this.m_jDelete.setFocusPainted(false);
    this.m_jDelete.setFocusable(false);
    this.m_jDelete.setMargin(new Insets(8, 14, 8, 14));
    this.m_jDelete.setRequestFocusEnabled(false);
    this.m_jDelete.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            StockRequest.this.m_jDeleteActionPerformed(evt);
          }
        });
    this.jPanel3.add(this.m_jDelete);
    this.m_jDelete.setBounds(580, 10, 54, 42);
    this.jPanel5.setLayout(new BorderLayout());
    this.jPanel3.add(this.jPanel5);
    this.jPanel5.setBounds(10, 10, 560, 420);
    this.m_jDelete1.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/button_ok.png")));
    this.m_jDelete1.setFocusPainted(false);
    this.m_jDelete1.setFocusable(false);
    this.m_jDelete1.setMargin(new Insets(8, 14, 8, 14));
    this.m_jDelete1.setRequestFocusEnabled(false);
    this.m_jDelete1.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            StockRequest.this.m_jDelete1ActionPerformed(evt);
          }
        });
    this.jPanel3.add(this.m_jDelete1);
    this.m_jDelete1.setBounds(580, 110, 54, 42);
    this.m_jDelete3.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/search22.png")));
    this.m_jDelete3.setFocusPainted(false);
    this.m_jDelete3.setFocusable(false);
    this.m_jDelete3.setMargin(new Insets(8, 14, 8, 14));
    this.m_jDelete3.setRequestFocusEnabled(false);
    this.m_jDelete3.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            StockRequest.this.m_jDelete3ActionPerformed(evt);
          }
        });
    this.jPanel3.add(this.m_jDelete3);
    this.m_jDelete3.setBounds(580, 60, 54, 42);
    add(this.jPanel3, "Center");
    this.catcontainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    this.catcontainer.setLayout(new BorderLayout());
    add(this.catcontainer, "South");
  }
  
  private void btnDownloadProductsActionPerformed(ActionEvent evt) {
    DeviceScanner s = this.m_App.getDeviceScanner();
    try {
      s.connectDevice();
      s.startDownloadProduct();
      ProductDownloaded p = s.recieveProduct();
      while (p != null) {
        incProductByCode(p.getCode(), p.getQuantity());
        p = s.recieveProduct();
      } 
    } catch (DeviceScannerException e) {
      MessageInf msg = new MessageInf(-33554432, AppLocal.getIntString("message.scannerfail2"), e);
      msg.show(this);
    } finally {
      s.disconnectDevice();
    } 
  }
  
  private void m_jreasonActionPerformed(ActionEvent evt) {}
  
  private void m_jDownActionPerformed(ActionEvent evt) {
    this.m_invlines.goDown();
  }
  
  private void m_jUpActionPerformed(ActionEvent evt) {
    this.m_invlines.goUp();
  }
  
  private void m_jDeleteActionPerformed(ActionEvent evt) {
    deleteLine(this.m_invlines.getSelectedRow());
  }
  
  private void m_jEnterActionPerformed(ActionEvent evt) {
    incProductByCode(this.m_jcodebar.getText());
    this.m_jcodebar.setText((String)null);
  }
  
  private void m_jbtndateActionPerformed(ActionEvent evt) {
    Date date = new Date();
    try {
      date = (Date)Formats.TIMESTAMP.parseValue(this.m_jdate.getText());
    } catch (BasicException e) {
      date = null;
    } 
    date = JCalendarDialog.showCalendarTime(this, date);
    if (date != null)
      this.m_jdate.setText(Formats.TIMESTAMP.formatValue(date)); 
  }
  
  private void jNumberKeysKeyPerformed(JNumberEvent evt) {
    stateTransition(evt.getKey());
  }
  
  private void jTextField1KeyTyped(KeyEvent evt) {
    this.jTextField1.setText((String)null);
    stateTransition(evt.getKeyChar());
  }
  
  private void m_jcodebarMouseClicked(MouseEvent evt) {
    EventQueue.invokeLater(new Runnable() {
          public void run() {
            StockRequest.this.jTextField1.requestFocus();
          }
        });
  }
  
  private void jEditAttributesActionPerformed(ActionEvent evt) {}
  
  private void m_jDelete1ActionPerformed(ActionEvent evt) {
    if (this.m_invlines.getCount() == 0) {
      Toolkit.getDefaultToolkit().beep();
    } else {
      int res = JOptionPane.showConfirmDialog(this, "Esta seguro de realizar esta captura?", "Mensaje", 1, 3);
      if (res == 0)
        saveData(); 
    } 
  }
  
  private void m_jDelete2ActionPerformed(ActionEvent evt) {}
  
  private void m_jDelete3ActionPerformed(ActionEvent evt) {
    try {
      ProductInfoExt oProduct = JProductFinder.showMessage(this, this.m_dlSales);
      if (oProduct != null) {
        double inventario = this.m_dlSales.findProductStock("0", oProduct.getID(), null);
        double min = this.m_dlSales.findMinProductStock("0", oProduct.getID());
        double max = this.m_dlSales.findMaxProductStock("0", oProduct.getID());
        double faltante = min - inventario;
        String units = JOptionPane.showInputDialog(null, "<html>Producto: " + oProduct
            .getName() + "<br>Inv. Actual: " + Formats.DOUBLE
            .formatValue(Double.valueOf(inventario)) + "<br>Minimo: " + Formats.DOUBLE
            .formatValue(Double.valueOf(min)) + "<br>Maximo: " + Formats.DOUBLE
            .formatValue(Double.valueOf(max)) + "<br>Faltante: " + Formats.DOUBLE
            .formatValue(Double.valueOf(faltante)) + "<br>Ingrese la cantiadad:", Formats.DOUBLE
            .formatValue(Double.valueOf(faltante)));
        try {
          double d = Double.parseDouble(units);
          incProductByCode(oProduct.getCode(), d);
        } catch (Exception exception) {}
      } 
    } catch (BasicException ex) {
      Logger.getLogger(StockManagement.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
    } 
    this.m_jcodebar.setText((String)null);
  }
}
