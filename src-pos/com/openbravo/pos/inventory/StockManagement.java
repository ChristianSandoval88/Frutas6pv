package com.openbravo.pos.inventory;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import com.openbravo.basic.BasicException;
import com.openbravo.beans.*;
import com.openbravo.data.gui.*;
import com.openbravo.data.loader.*;
import com.openbravo.format.Formats;
import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.scripting.ScriptFactory;
import com.openbravo.pos.catalog.CatalogSelector;
import com.openbravo.pos.forms.*;
import com.openbravo.pos.catalog.JCatalog;
import com.openbravo.pos.panels.JProductFinder;
import com.openbravo.pos.printer.TicketParser;
import com.openbravo.pos.printer.TicketPrinterException;
import com.openbravo.pos.sales.JPanelTicket;
import com.openbravo.pos.sales.JProductAttEdit;
import com.openbravo.pos.scanpal2.DeviceScanner;
import com.openbravo.pos.scanpal2.DeviceScannerException;
import com.openbravo.pos.scanpal2.ProductDownloaded;
import com.openbravo.pos.ticket.ProductInfoExt;
import com.openbravo.pos.ticket.TicketInfo;
import com.openbravo.pos.ticket.TicketLineInfo;
import com.openbravo.pos.util.JRPrinterAWT300;
import com.openbravo.pos.util.ReportUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.PrintService;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

public class StockManagement extends JPanel implements JPanelView {
    
    private AppView m_App;
    private DataLogicSystem m_dlSystem;
    private DataLogicSales m_dlSales;
    private TicketParser m_TTP;

    private CatalogSelector m_cat;
    private ComboBoxValModel m_ReasonModel;
    
    private SentenceList m_sentlocations;
    private ComboBoxValModel m_LocationsModel;   
    private ComboBoxValModel m_LocationsModelDes;     
    
    private JInventoryLines m_invlines;
    
    private int NUMBER_STATE = 0;
    private int MULTIPLY = 0;
    private static int DEFAULT = 0;
    private static int ACTIVE = 1;
    private static int DECIMAL = 2;
    
    /** Creates new form StockManagement */
    public StockManagement(AppView app) {
        
        m_App = app;
        m_dlSystem = (DataLogicSystem) m_App.getBean("com.openbravo.pos.forms.DataLogicSystem");
        m_dlSales = (DataLogicSales) m_App.getBean("com.openbravo.pos.forms.DataLogicSales");
        m_TTP = new TicketParser(m_App.getDeviceTicket(), m_dlSystem);

        initComponents();
        
        btnDownloadProducts.setEnabled(m_App.getDeviceScanner() != null);

        
        // El modelo de locales
        m_sentlocations = m_dlSales.getLocationsList();
        m_LocationsModel =  new ComboBoxValModel();        
        m_LocationsModelDes = new ComboBoxValModel();
        
        m_ReasonModel = new ComboBoxValModel();
        m_ReasonModel.add(MovementReason.IN_PURCHASE);
        m_ReasonModel.add(MovementReason.IN_REFUND);
        m_ReasonModel.add(MovementReason.IN_MOVEMENT);
        m_ReasonModel.add(MovementReason.OUT_SALE);
        m_ReasonModel.add(MovementReason.OUT_REFUND);
        m_ReasonModel.add(MovementReason.OUT_BREAK);
        m_ReasonModel.add(MovementReason.OUT_MOVEMENT);        
        m_ReasonModel.add(MovementReason.OUT_CROSSING);        
        
        m_jreason.setModel(m_ReasonModel);
        
        m_cat = new JCatalog(m_dlSales);
        m_cat.getComponent().setPreferredSize(new Dimension(0, 245));
        m_cat.addActionListener(new CatalogListener());
        catcontainer.add(m_cat.getComponent(), BorderLayout.CENTER);
        
        // Las lineas de inventario
        m_invlines = new JInventoryLines();
        jPanel5.add(m_invlines, BorderLayout.CENTER);
    }
     
    public String getTitle() {
        return AppLocal.getIntString("Menu.StockMovement");
    }         
    
    public JComponent getComponent() {
        return this;
    }

    public void activate() throws BasicException {
        m_cat.loadCatalog();
        
        java.util.List l = m_sentlocations.list();
        m_LocationsModel = new ComboBoxValModel(l);
        m_jLocation.setModel(m_LocationsModel); // para que lo refresque
        m_LocationsModelDes = new ComboBoxValModel(l);
        m_jLocationDes.setModel(m_LocationsModelDes); // para que lo refresque
        
        stateToInsert();
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                jTextField1.requestFocus();
            }
        });        
    }   
    
    
    public void stateToInsert() {
        // Inicializamos las cajas de texto
        m_jdate.setText(Formats.TIMESTAMP.formatValue(DateUtils.getTodayMinutes()));
        m_ReasonModel.setSelectedItem(MovementReason.IN_PURCHASE); 
        m_LocationsModel.setSelectedKey(m_App.getInventoryLocation());     
        m_LocationsModelDes.setSelectedKey(m_App.getInventoryLocation());         
        m_invlines.clear();
        m_jcodebar.setText(null);
    }
    
    public boolean deactivate() {

        if (m_invlines.getCount() > 0) {
            int res = JOptionPane.showConfirmDialog(this, LocalRes.getIntString("message.wannasave"), LocalRes.getIntString("title.editor"), JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (res == JOptionPane.YES_OPTION) {
                saveData();
                return true;
            } else if (res == JOptionPane.NO_OPTION) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }        
    }    

    private void addLine(ProductInfoExt oProduct, double dpor, double dprice, double inv) {
        InventoryLine i = new InventoryLine(oProduct, dpor, dprice);
        i.setInventario(inv);
        this.m_invlines.addLine(i);
      }
    
    private void deleteLine(int index) {
        if (index < 0){
            Toolkit.getDefaultToolkit().beep(); // No hay ninguna seleccionada
        } else {
            m_invlines.deleteLine(index);          
        }        
    }
    
    private void incProduct(ProductInfoExt product, double units) {
        try {
            double inventario = this.m_dlSales.findProductStock("0", product.getID(), null);
            MovementReason reason = (MovementReason)this.m_ReasonModel.getSelectedItem();
            addLine(product, units, reason.isInput() ? product
                .getPriceBuy() : product
                .getPriceSell(), inventario);
          } catch (BasicException ex) {
            Logger.getLogger(JPanelTicket.class.getName()).log(Level.SEVERE, (String)null, (Throwable)ex);
          } 
    }
    
    private void incProductByCode(String sCode) {
        incProductByCode(sCode, 1.0);
    }
    private void incProductByCode(String sCode, double dQuantity) {
    // precondicion: sCode != null
        
        try {
            ProductInfoExt oProduct = m_dlSales.getProductInfoByCode(sCode);
            if (oProduct == null) {                  
                Toolkit.getDefaultToolkit().beep();                   
            } else {
                // Se anade directamente una unidad con el precio y todo
                incProduct(oProduct, dQuantity);
            }
        } catch (BasicException eData) {       
            MessageInf msg = new MessageInf(eData);
            msg.show(this);            
        }
    }
    
    private void addUnits(double dUnits) {
        int i  = m_invlines.getSelectedRow();
        if (i >= 0 ) {
            InventoryLine inv = m_invlines.getLine(i);
            double dunits = inv.getMultiply() + dUnits;
            if (dunits <= 0.0) {
                deleteLine(i);
            } else {            
                inv.setMultiply(inv.getMultiply() + dUnits);
                m_invlines.setLine(i, inv);
            }
        }
    }
    
    private void setUnits(double dUnits) {
        int i  = m_invlines.getSelectedRow();
        if (i >= 0 ) {
            InventoryLine inv = m_invlines.getLine(i);         
            inv.setMultiply(dUnits);
            m_invlines.setLine(i, inv);
        }
    }
    
    private void stateTransition(char cTrans) {
        if (cTrans == '\n') {
            try {
              ProductInfoExt oProduct = this.m_dlSales.getProductInfoByCode(this.m_jcodebar.getText());
              if (oProduct != null) {
                String units = JOptionPane.showInputDialog("Cantidad:");
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
        try {
            Date d = (Date)Formats.TIMESTAMP.parseValue(this.m_jdate.getText());
            MovementReason reason = (MovementReason)this.m_ReasonModel.getSelectedItem();
            if (reason == MovementReason.OUT_CROSSING) {
              saveData(new InventoryRecord(d, MovementReason.OUT_MOVEMENT, (LocationInfo)this.m_LocationsModel

                    .getSelectedItem(), this.m_invlines
                    .getLines()));
              saveData(new InventoryRecord(d, MovementReason.IN_MOVEMENT, (LocationInfo)this.m_LocationsModelDes

                    .getSelectedItem(), this.m_invlines
                    .getLines()));
            } else {
              saveData(new InventoryRecord(d, reason, (LocationInfo)this.m_LocationsModel

                    .getSelectedItem(), this.m_invlines
                    .getLines()));
            } 
            stateToInsert();
          } catch (BasicException eData) {
            MessageInf msg = new MessageInf(-67108864, AppLocal.getIntString("message.cannotsaveinventorydata"), eData);
            msg.show(this);
          }          
    }
        
    private void saveData(InventoryRecord rec) throws BasicException {
        SentenceExec sent = this.m_dlSales.getStockDiaryInsert();
        for (int i = 0; i < this.m_invlines.getCount(); i++) {
          InventoryLine inv = rec.getLines().get(i);
          sent.exec(new Object[] { UUID.randomUUID().toString(), rec
                .getDate(), rec
                .getReason().getKey(), "0", inv
                .getProductID(), inv
                .getProductAttSetInstId(), rec
                .getReason().samesignum(Double.valueOf(inv.getMultiply())), 
                Double.valueOf(inv.getPrice()), this.m_App
                .getActiveCashIndex() });
        } 
        printTicket(rec); 
    }
    private void printReport(String resourcefile, InventoryRecord rec) {

        try {
            JasperReport jr;

            InputStream in = getClass().getResourceAsStream(resourcefile + ".ser");
            if (in == null) {
                // read and compile the report
                JasperDesign jd = JRXmlLoader.load(getClass().getResourceAsStream(resourcefile + ".jrxml"));
                jr = JasperCompileManager.compileReport(jd);
            } else {
                // read the compiled reporte
                ObjectInputStream oin = new ObjectInputStream(in);
                jr = (JasperReport) oin.readObject();
                oin.close();
            }

            // Construyo el mapa de los parametros.
            Map reportparams = new HashMap();
            // reportparams.put("ARG", params);
            try {
                reportparams.put("REPORT_RESOURCE_BUNDLE", ResourceBundle.getBundle(resourcefile + ".properties"));
            } catch (MissingResourceException e) {
            }

            Map reportfields = new HashMap();
            reportfields.put("REC", rec);

            JasperPrint jp = JasperFillManager.fillReport(jr, reportparams, new JRMapArrayDataSource(new Object[]{reportfields}));

            PrintService service = ReportUtils.getPrintService(m_App.getProperties().getProperty("machine.printername"));

            JRPrinterAWT300.printPages(jp, 0, jp.getPages().size() - 1, service);

        } catch (Exception e) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotloadreport"), e);
            msg.show(this);
        }
    }
    private void printTicket(InventoryRecord invrec) {

        String sresource = m_dlSystem.getResourceAsXML("Printer.Inventory");
        if (sresource == null) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"));
            msg.show(this);
        } else {
            try {
                ScriptEngine script = ScriptFactory.getScriptEngine(ScriptFactory.VELOCITY);
                script.put("inventoryrecord", invrec);
                m_TTP.printTicket(script.eval(sresource).toString());
            } catch (ScriptException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
                msg.show(this);
            } catch (TicketPrinterException e) {
                MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotprintticket"), e);
                msg.show(this);
            }
        }
    }
  
    
    private class CatalogListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String sQty = m_jcodebar.getText();
            if (sQty != null) {
                Double dQty = (Double.valueOf(sQty)==0) ? 1.0 : Double.valueOf(sQty);
                incProduct( (ProductInfoExt) e.getSource(), dQty);
                m_jcodebar.setText(null);
            } else {
                incProduct( (ProductInfoExt) e.getSource(),1.0);
            }
        }  
    }  
  
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jEditAttributes = new javax.swing.JButton();
        m_jDown = new javax.swing.JButton();
        m_jUp = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        btnDownloadProducts = new javax.swing.JButton();
        m_jDelete2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jNumberKeys = new com.openbravo.beans.JNumberKeys();
        jPanel4 = new javax.swing.JPanel();
        m_jEnter = new javax.swing.JButton();
        m_jcodebar = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        m_jdate = new javax.swing.JTextField();
        m_jbtndate = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        m_jreason = new javax.swing.JComboBox();
        jLabel8 = new javax.swing.JLabel();
        m_jLocation = new javax.swing.JComboBox();
        m_jDelete = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        m_jLocationDes = new javax.swing.JComboBox();
        m_jDelete1 = new javax.swing.JButton();
        m_jDelete3 = new javax.swing.JButton();
        catcontainer = new javax.swing.JPanel();

        jEditAttributes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/colorize.png"))); // NOI18N
        jEditAttributes.setFocusPainted(false);
        jEditAttributes.setFocusable(false);
        jEditAttributes.setMargin(new java.awt.Insets(8, 14, 8, 14));
        jEditAttributes.setRequestFocusEnabled(false);
        jEditAttributes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jEditAttributesActionPerformed(evt);
            }
        });

        m_jDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/1downarrow22.png"))); // NOI18N
        m_jDown.setFocusPainted(false);
        m_jDown.setFocusable(false);
        m_jDown.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jDown.setRequestFocusEnabled(false);
        m_jDown.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jDownActionPerformed(evt);
            }
        });

        m_jUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/1uparrow22.png"))); // NOI18N
        m_jUp.setFocusPainted(false);
        m_jUp.setFocusable(false);
        m_jUp.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jUp.setRequestFocusEnabled(false);
        m_jUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jUpActionPerformed(evt);
            }
        });

        btnDownloadProducts.setText("ScanPal");
        btnDownloadProducts.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDownloadProductsActionPerformed(evt);
            }
        });
        jPanel6.add(btnDownloadProducts);

        m_jDelete2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/contents.png"))); // NOI18N
        m_jDelete2.setFocusPainted(false);
        m_jDelete2.setFocusable(false);
        m_jDelete2.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jDelete2.setRequestFocusEnabled(false);
        m_jDelete2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jDelete2ActionPerformed(evt);
            }
        });

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jNumberKeys.addJNumberEventListener(new com.openbravo.beans.JNumberEventListener() {
            public void keyPerformed(com.openbravo.beans.JNumberEvent evt) {
                jNumberKeysKeyPerformed(evt);
            }
        });
        jPanel2.add(jNumberKeys);

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        m_jEnter.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/barcode.png"))); // NOI18N
        m_jEnter.setFocusPainted(false);
        m_jEnter.setFocusable(false);
        m_jEnter.setRequestFocusEnabled(false);
        m_jEnter.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jEnterActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel4.add(m_jEnter, gridBagConstraints);

        m_jcodebar.setBackground(java.awt.Color.white);
        m_jcodebar.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        m_jcodebar.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        m_jcodebar.setOpaque(true);
        m_jcodebar.setPreferredSize(new java.awt.Dimension(135, 30));
        m_jcodebar.setRequestFocusEnabled(false);
        m_jcodebar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                m_jcodebarMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel4.add(m_jcodebar, gridBagConstraints);

        jTextField1.setBackground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        jTextField1.setForeground(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        jTextField1.setCaretColor(javax.swing.UIManager.getDefaults().getColor("Panel.background"));
        jTextField1.setPreferredSize(new java.awt.Dimension(1, 1));
        jTextField1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jTextField1KeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        jPanel4.add(jTextField1, gridBagConstraints);

        jPanel2.add(jPanel4);

        jPanel1.add(jPanel2, java.awt.BorderLayout.NORTH);

        add(jPanel1, java.awt.BorderLayout.EAST);

        jPanel3.setLayout(null);

        jLabel1.setText(AppLocal.getIntString("label.stockdate")); // NOI18N
        jPanel3.add(jLabel1);
        jLabel1.setBounds(10, 10, 150, 14);
        jPanel3.add(m_jdate);
        m_jdate.setBounds(160, 10, 200, 20);

        m_jbtndate.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/date.png"))); // NOI18N
        m_jbtndate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jbtndateActionPerformed(evt);
            }
        });
        jPanel3.add(m_jbtndate);
        m_jbtndate.setBounds(370, 10, 40, 25);

        jLabel2.setText(AppLocal.getIntString("label.stockreason")); // NOI18N
        jPanel3.add(jLabel2);
        jLabel2.setBounds(10, 40, 150, 14);

        m_jreason.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jreasonActionPerformed(evt);
            }
        });
        jPanel3.add(m_jreason);
        m_jreason.setBounds(160, 40, 200, 20);

        jLabel8.setText(AppLocal.getIntString("label.warehouse")); // NOI18N
        jPanel3.add(jLabel8);
        jLabel8.setBounds(10, 70, 150, 14);
        jPanel3.add(m_jLocation);
        m_jLocation.setBounds(160, 70, 200, 20);

        m_jDelete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/locationbar_erase.png"))); // NOI18N
        m_jDelete.setFocusPainted(false);
        m_jDelete.setFocusable(false);
        m_jDelete.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jDelete.setRequestFocusEnabled(false);
        m_jDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jDeleteActionPerformed(evt);
            }
        });
        jPanel3.add(m_jDelete);
        m_jDelete.setBounds(520, 120, 54, 42);

        jPanel5.setLayout(new java.awt.BorderLayout());
        jPanel3.add(jPanel5);
        jPanel5.setBounds(10, 110, 500, 280);
        jPanel3.add(m_jLocationDes);
        m_jLocationDes.setBounds(370, 70, 200, 20);

        m_jDelete1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/button_ok.png"))); // NOI18N
        m_jDelete1.setFocusPainted(false);
        m_jDelete1.setFocusable(false);
        m_jDelete1.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jDelete1.setRequestFocusEnabled(false);
        m_jDelete1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jDelete1ActionPerformed(evt);
            }
        });
        jPanel3.add(m_jDelete1);
        m_jDelete1.setBounds(520, 220, 54, 42);

        m_jDelete3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/openbravo/images/search22.png"))); // NOI18N
        m_jDelete3.setFocusPainted(false);
        m_jDelete3.setFocusable(false);
        m_jDelete3.setMargin(new java.awt.Insets(8, 14, 8, 14));
        m_jDelete3.setRequestFocusEnabled(false);
        m_jDelete3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                m_jDelete3ActionPerformed(evt);
            }
        });
        jPanel3.add(m_jDelete3);
        m_jDelete3.setBounds(520, 170, 54, 42);

        add(jPanel3, java.awt.BorderLayout.CENTER);

        catcontainer.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        catcontainer.setLayout(new java.awt.BorderLayout());
        add(catcontainer, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

    private void btnDownloadProductsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDownloadProductsActionPerformed

        // Ejecutamos la descarga...
        DeviceScanner s = m_App.getDeviceScanner();
        try {
            s.connectDevice();
            s.startDownloadProduct();
            
            ProductDownloaded p = s.recieveProduct();
            while (p != null) {
                incProductByCode(p.getCode(), p.getQuantity());
                p = s.recieveProduct();
            }
            // MessageInf msg = new MessageInf(MessageInf.SGN_SUCCESS, "Se ha subido con exito la lista de productos al ScanPal.");
            // msg.show(this);            
        } catch (DeviceScannerException e) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.scannerfail2"), e);
            msg.show(this);            
        } finally {
            s.disconnectDevice();
        }        
        
    }//GEN-LAST:event_btnDownloadProductsActionPerformed

    private void m_jreasonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jreasonActionPerformed

        m_jLocationDes.setEnabled(m_ReasonModel.getSelectedItem() == MovementReason.OUT_CROSSING); 
        
    }//GEN-LAST:event_m_jreasonActionPerformed

    private void m_jDownActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jDownActionPerformed
        
        m_invlines.goDown();
        
    }//GEN-LAST:event_m_jDownActionPerformed

    private void m_jUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jUpActionPerformed

        m_invlines.goUp();
        
    }//GEN-LAST:event_m_jUpActionPerformed

    private void m_jDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jDeleteActionPerformed
        
        deleteLine(m_invlines.getSelectedRow());

    }//GEN-LAST:event_m_jDeleteActionPerformed

    private void m_jEnterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jEnterActionPerformed
        
        incProductByCode(m_jcodebar.getText());
        m_jcodebar.setText(null);
        
    }//GEN-LAST:event_m_jEnterActionPerformed

    private void m_jbtndateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jbtndateActionPerformed
        
        Date date;
        try {
            date = (Date) Formats.TIMESTAMP.parseValue(m_jdate.getText());
        } catch (BasicException e) {
            date = null;
        }
        date = JCalendarDialog.showCalendarTime(this, date);
        if (date != null) {
            m_jdate.setText(Formats.TIMESTAMP.formatValue(date));
        }
    }//GEN-LAST:event_m_jbtndateActionPerformed

    private void jNumberKeysKeyPerformed(com.openbravo.beans.JNumberEvent evt) {//GEN-FIRST:event_jNumberKeysKeyPerformed
        
        stateTransition(evt.getKey());
        
    }//GEN-LAST:event_jNumberKeysKeyPerformed

private void jTextField1KeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField1KeyTyped
    jTextField1.setText(null);
    stateTransition(evt.getKeyChar());
}//GEN-LAST:event_jTextField1KeyTyped

private void m_jcodebarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_m_jcodebarMouseClicked
    java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                jTextField1.requestFocus();
            }
    });
}//GEN-LAST:event_m_jcodebarMouseClicked

private void jEditAttributesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEditAttributesActionPerformed

    int i = m_invlines.getSelectedRow();
    if (i < 0) {
        Toolkit.getDefaultToolkit().beep(); // no line selected
    } else {
        try {
            InventoryLine line = m_invlines.getLine(i);
            JProductAttEdit attedit = JProductAttEdit.getAttributesEditor(this, m_App.getSession());
            attedit.editAttributes(line.getProductAttSetId(), line.getProductAttSetInstId());
            attedit.setVisible(true);
            if (attedit.isOK()) {
                // The user pressed OK
                line.setProductAttSetInstId(attedit.getAttributeSetInst());
                line.setProductAttSetInstDesc(attedit.getAttributeSetInstDescription());
                m_invlines.setLine(i, line);
            }
        } catch (BasicException ex) {
            MessageInf msg = new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotfindattributes"), ex);
            msg.show(this);
        }
    }
}//GEN-LAST:event_jEditAttributesActionPerformed

    private void m_jDelete1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jDelete1ActionPerformed
        if (m_invlines.getCount() == 0) {
                // No podemos grabar, no hay ningun registro.
                Toolkit.getDefaultToolkit().beep();
            } else {
               int res = JOptionPane.showConfirmDialog(this, "Esta seguro de realizar esta captura?", "Mensaje", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
               if (res == JOptionPane.YES_OPTION) {
                 saveData();
               }
            }
    }//GEN-LAST:event_m_jDelete1ActionPerformed

    private void m_jDelete2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jDelete2ActionPerformed
        /*JFileChooser file=new JFileChooser();
        file.showOpenDialog(this);
        FileInputStream fis = null;
        try {
            File excelFile = file.getSelectedFile();
            if(excelFile!=null)
            { 
                Workbook workbook = WorkbookFactory.create(excelFile);
                Sheet sheet = workbook.getSheetAt(0);

                Iterator<Row> rowIterator = sheet.rowIterator();
                while (rowIterator.hasNext()) {
                    Row row = rowIterator.next();

                    // Now let's iterate over the columns of the current row
                    Iterator<Cell> cellIterator = row.cellIterator();
                    DataFormatter dataFormatter = new DataFormatter();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        String cellValue = dataFormatter.formatCellValue(cell);
                        System.out.print(cellValue + "\t");
                    }
                    System.out.println();
                }
                workbook.close();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StockManagement.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StockManagement.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(StockManagement.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/
    }//GEN-LAST:event_m_jDelete2ActionPerformed

    private void m_jDelete3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_m_jDelete3ActionPerformed
        ProductInfoExt prod = JProductFinder.showMessage(StockManagement.this, m_dlSales);
        if (prod != null) {
            String units = JOptionPane.showInputDialog("Cantidad:");
            try{
                double d = Double.parseDouble(units);
                incProduct(prod, d);
            } catch(Exception e){
            }
        }
    }//GEN-LAST:event_m_jDelete3ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDownloadProducts;
    private javax.swing.JPanel catcontainer;
    private javax.swing.JButton jEditAttributes;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel8;
    private com.openbravo.beans.JNumberKeys jNumberKeys;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JButton m_jDelete;
    private javax.swing.JButton m_jDelete1;
    private javax.swing.JButton m_jDelete2;
    private javax.swing.JButton m_jDelete3;
    private javax.swing.JButton m_jDown;
    private javax.swing.JButton m_jEnter;
    private javax.swing.JComboBox m_jLocation;
    private javax.swing.JComboBox m_jLocationDes;
    private javax.swing.JButton m_jUp;
    private javax.swing.JButton m_jbtndate;
    private javax.swing.JLabel m_jcodebar;
    private javax.swing.JTextField m_jdate;
    private javax.swing.JComboBox m_jreason;
    // End of variables declaration//GEN-END:variables
    
}
