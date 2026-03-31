package com.openbravo.pos.panels;

import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.gui.TableRendererBasic;
import com.openbravo.data.loader.Datas;
import com.openbravo.data.loader.SerializerWrite;
import com.openbravo.data.loader.SerializerWriteBasic;
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.format.Formats;
import com.openbravo.pos.forms.AppConfig;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.BeanFactoryApp;
import com.openbravo.pos.forms.BeanFactoryException;
import com.openbravo.pos.forms.DataLogicSystem;
import com.openbravo.pos.forms.JPanelView;
import com.openbravo.pos.printer.TicketParser;
import com.openbravo.pos.printer.TicketPrinterException;
import com.openbravo.pos.scripting.ScriptEngine;
import com.openbravo.pos.scripting.ScriptException;
import com.openbravo.pos.scripting.ScriptFactory;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class JPanelCloseMoney extends JPanel implements JPanelView, BeanFactoryApp {
  private AppView m_App;
  
  private DataLogicSystem m_dlSystem;
  
  private PaymentsModel m_PaymentsToClose = null;
  
  private TicketParser m_TTP;
  
  private JTextField j1;
  
  private JTextField j10;
  
  private JTextField j100;
  
  private JTextField j2;
  
  private JTextField j20;
  
  private JTextField j200;
  
  private JTextField j5;
  
  private JTextField j50;
  
  private JTextField j500;
  
  private JTextField j50c;
  
  private JButton jButton1;
  
  private JLabel jGranTotal;
  
  private JLabel jLabel1;
  
  private JLabel jLabel10;
  
  private JLabel jLabel11;
  
  private JLabel jLabel12;
  
  private JLabel jLabel13;
  
  private JLabel jLabel14;
  
  private JLabel jLabel15;
  
  private JLabel jLabel16;
  
  private JLabel jLabel18;
  
  private JLabel jLabel19;
  
  private JLabel jLabel2;
  
  private JLabel jLabel20;
  
  private JLabel jLabel21;
  
  private JLabel jLabel3;
  
  private JLabel jLabel4;
  
  private JLabel jLabel5;
  
  private JLabel jLabel6;
  
  private JLabel jLabel7;
  
  private JLabel jLabel8;
  
  private JLabel jLabel9;
  
  private JPanel jPanel1;
  
  private JPanel jPanel4;
  
  private JPanel jPanel5;
  
  private JPanel jPanel6;
  
  private JPanel jPanel7;
  
  private JLabel jResultado;
  
  private JTextField jTextField1;
  
  private JTextField m_jCash;
  
  private JButton m_jCloseCash;
  
  private JTextField m_jCount;
  
  private JTextField m_jMaxDate;
  
  private JTextField m_jMinDate;
  
  private JButton m_jPrintCash;
  
  private JTextField m_jSales;
  
  private JTextField m_jSalesSubtotal;
  
  private JTextField m_jSalesTaxes;
  
  private JTextField m_jSalesTotal;
  
  private JScrollPane m_jScrollSales;
  
  private JScrollPane m_jScrollTableTicket;
  
  private JScrollPane m_jScrollTableTicket1;
  
  private JTextField m_jSequence;
  
  private JTable m_jTicketTable;
  
  private JTable m_jTicketTable1;
  
  private JTable m_jsalestable;
  
  public JPanelCloseMoney() {
    initComponents();
  }
  
  public void init(AppView app) throws BeanFactoryException {
    this.m_App = app;
    this.m_dlSystem = (DataLogicSystem)this.m_App.getBean("com.openbravo.pos.forms.DataLogicSystem");
    this.m_TTP = new TicketParser(this.m_App.getDeviceTicket(), this.m_dlSystem);
    this.m_jTicketTable.setDefaultRenderer(Object.class, (TableCellRenderer)new TableRendererBasic(new Formats[] { new FormatsPayment(), Formats.CURRENCY }));
    this.m_jTicketTable1.setDefaultRenderer(Object.class, (TableCellRenderer)new TableRendererBasic(new Formats[] { Formats.STRING, Formats.DOUBLE, Formats.TIMESTAMP, Formats.STRING }));
    this.m_jTicketTable.setAutoResizeMode(0);
    this.m_jScrollTableTicket.getVerticalScrollBar().setPreferredSize(new Dimension(25, 25));
    this.m_jTicketTable.getTableHeader().setReorderingAllowed(false);
    this.m_jTicketTable.setRowHeight(25);
    this.m_jTicketTable.getSelectionModel().setSelectionMode(0);
    this.m_jTicketTable1.setAutoResizeMode(0);
    this.m_jScrollTableTicket1.getVerticalScrollBar().setPreferredSize(new Dimension(25, 25));
    this.m_jTicketTable1.getTableHeader().setReorderingAllowed(false);
    this.m_jTicketTable1.setRowHeight(25);
    this.m_jTicketTable1.getSelectionModel().setSelectionMode(0);
    this.m_jsalestable.setDefaultRenderer(Object.class, (TableCellRenderer)new TableRendererBasic(new Formats[] { Formats.STRING, Formats.CURRENCY, Formats.CURRENCY, Formats.CURRENCY }));
    this.m_jsalestable.setAutoResizeMode(0);
    this.m_jScrollSales.getVerticalScrollBar().setPreferredSize(new Dimension(25, 25));
    this.m_jsalestable.getTableHeader().setReorderingAllowed(false);
    this.m_jsalestable.setRowHeight(25);
    this.m_jsalestable.getSelectionModel().setSelectionMode(0);
  }
  
  public Object getBean() {
    return this;
  }
  
  public JComponent getComponent() {
    return this;
  }
  
  public String getTitle() {
    return AppLocal.getIntString("Menu.CloseTPV");
  }
  
  public void activate() throws BasicException {
    loadData();
  }
  
  public boolean deactivate() {
    return true;
  }
  
  private void loadData() throws BasicException {
    this.m_jSequence.setText((String)null);
    this.m_jMinDate.setText((String)null);
    this.m_jMaxDate.setText((String)null);
    this.m_jPrintCash.setEnabled(false);
    this.m_jCloseCash.setEnabled(false);
    this.m_jCount.setText((String)null);
    this.m_jCash.setText((String)null);
    this.m_jSales.setText((String)null);
    this.m_jSalesSubtotal.setText((String)null);
    this.m_jSalesTaxes.setText((String)null);
    this.m_jSalesTotal.setText((String)null);
    this.m_jTicketTable.setModel(new DefaultTableModel());
    this.m_jTicketTable1.setModel(new DefaultTableModel());
    this.m_jsalestable.setModel(new DefaultTableModel());
    this.m_PaymentsToClose = PaymentsModel.loadInstance(this.m_App);
    this.m_jSequence.setText(this.m_PaymentsToClose.printSequence());
    this.m_jMinDate.setText(this.m_PaymentsToClose.printDateStart());
    this.m_jMaxDate.setText(this.m_PaymentsToClose.printDateEnd());
    if (this.m_PaymentsToClose.getPayments() != 0 || this.m_PaymentsToClose.getSales() != 0) {
      this.m_jPrintCash.setEnabled(true);
      this.m_jCloseCash.setEnabled(true);
      this.m_jCount.setText(this.m_PaymentsToClose.printPayments());
      this.m_jCash.setText(this.m_PaymentsToClose.printPaymentsTotal());
      this.m_jSales.setText(this.m_PaymentsToClose.printSales());
      this.m_jSalesSubtotal.setText(this.m_PaymentsToClose.printSalesBase());
      this.m_jSalesTaxes.setText(this.m_PaymentsToClose.printSalesTaxes());
      this.m_jSalesTotal.setText(this.m_PaymentsToClose.printSalesTotal());
    } 
    this.m_jTicketTable.setModel(this.m_PaymentsToClose.getPaymentsModel());
    this.m_jTicketTable1.setModel(this.m_PaymentsToClose.getPaymentsModel1());
    TableColumnModel jColumns = this.m_jTicketTable.getColumnModel();
    jColumns.getColumn(0).setPreferredWidth(200);
    jColumns.getColumn(0).setResizable(false);
    jColumns.getColumn(1).setPreferredWidth(100);
    jColumns.getColumn(1).setResizable(false);
    jColumns = this.m_jTicketTable1.getColumnModel();
    jColumns.getColumn(0).setPreferredWidth(200);
    jColumns.getColumn(0).setResizable(false);
    jColumns.getColumn(1).setPreferredWidth(50);
    jColumns.getColumn(1).setResizable(false);
    jColumns.getColumn(2).setPreferredWidth(180);
    jColumns.getColumn(2).setResizable(false);
    jColumns.getColumn(3).setPreferredWidth(180);
    jColumns.getColumn(3).setResizable(false);
    this.m_jsalestable.setModel(this.m_PaymentsToClose.getSalesModel());
    jColumns = this.m_jsalestable.getColumnModel();
    jColumns.getColumn(0).setPreferredWidth(200);
    jColumns.getColumn(0).setResizable(false);
    jColumns.getColumn(1).setPreferredWidth(100);
    jColumns.getColumn(1).setResizable(false);
    this.jGranTotal.setText((String)null);
    this.jResultado.setText((String)null);
  }
  
  private void printPayments(String report) {
    String sresource = this.m_dlSystem.getResourceAsXML(report);
    if (sresource == null) {
      MessageInf msg = new MessageInf(-33554432, AppLocal.getIntString("message.cannotprintticket"));
      msg.show(this);
    } else {
      try {
        ScriptEngine script = ScriptFactory.getScriptEngine("velocity");
        script.put("payments", this.m_PaymentsToClose);
        script.put("B500", this.j500.getText());
        script.put("B200", this.j200.getText());
        script.put("B100", this.j100.getText());
        script.put("B50", this.j50.getText());
        script.put("B20", this.j20.getText());
        script.put("M10", this.j10.getText());
        script.put("M5", this.j5.getText());
        script.put("M2", this.j2.getText());
        script.put("M1", this.j1.getText());
        script.put("M50", this.j50c.getText());
        script.put("TB500", Formats.CURRENCY.formatValue(Double.valueOf(Double.parseDouble(this.j500.getText()) * 500.0D)));
        script.put("TB200", Formats.CURRENCY.formatValue(Double.valueOf(Double.parseDouble(this.j200.getText()) * 200.0D)));
        script.put("TB100", Formats.CURRENCY.formatValue(Double.valueOf(Double.parseDouble(this.j100.getText()) * 100.0D)));
        script.put("TB50", Formats.CURRENCY.formatValue(Double.valueOf(Double.parseDouble(this.j50.getText()) * 50.0D)));
        script.put("TB20", Formats.CURRENCY.formatValue(Double.valueOf(Double.parseDouble(this.j20.getText()) * 20.0D)));
        script.put("TM10", Formats.CURRENCY.formatValue(Double.valueOf(Double.parseDouble(this.j10.getText()) * 10.0D)));
        script.put("TM5", Formats.CURRENCY.formatValue(Double.valueOf(Double.parseDouble(this.j5.getText()) * 5.0D)));
        script.put("TM2", Formats.CURRENCY.formatValue(Double.valueOf(Double.parseDouble(this.j2.getText()) * 2.0D)));
        script.put("TM1", Formats.CURRENCY.formatValue(Double.valueOf(Double.parseDouble(this.j1.getText()) * 1.0D)));
        script.put("TM50", Formats.CURRENCY.formatValue(Double.valueOf(Double.parseDouble(this.j50c.getText()) * 0.5D)));
        script.put("GRANTOTAL", Formats.CURRENCY.formatValue(Double.valueOf(Double.parseDouble(this.jGranTotal.getText()))));
        script.put("RESULTADO", Formats.CURRENCY.formatValue(Double.valueOf(Double.parseDouble(this.jResultado.getText()))));
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
  
  private class FormatsPayment extends Formats {
    private FormatsPayment() {}
    
    protected String formatValueInt(Object value) {
      return AppLocal.getIntString("transpayment." + (String)value);
    }
    
    protected Object parseValueInt(String value) throws ParseException {
      return value;
    }
    
    public int getAlignment() {
      return 2;
    }
  }
  
  private void initComponents() {
    this.jPanel6 = new JPanel();
    this.m_jSalesTotal = new JTextField();
    this.m_jScrollSales = new JScrollPane();
    this.m_jsalestable = new JTable();
    this.m_jSalesTaxes = new JTextField();
    this.m_jSalesSubtotal = new JTextField();
    this.m_jSales = new JTextField();
    this.jLabel5 = new JLabel();
    this.jLabel6 = new JLabel();
    this.jLabel12 = new JLabel();
    this.jLabel7 = new JLabel();
    this.jButton1 = new JButton();
    this.jTextField1 = new JTextField();
    this.jLabel8 = new JLabel();
    this.jPanel7 = new JPanel();
    this.m_jScrollTableTicket1 = new JScrollPane();
    this.m_jTicketTable1 = new JTable();
    this.jLabel3 = new JLabel();
    this.m_jMaxDate = new JTextField();
    this.jResultado = new JLabel();
    this.jLabel4 = new JLabel();
    this.m_jCash = new JTextField();
    this.jPanel5 = new JPanel();
    this.m_jScrollTableTicket = new JScrollPane();
    this.m_jTicketTable = new JTable();
    this.m_jCount = new JTextField();
    this.jGranTotal = new JLabel();
    this.jPanel1 = new JPanel();
    this.jPanel4 = new JPanel();
    this.jLabel11 = new JLabel();
    this.m_jSequence = new JTextField();
    this.jLabel2 = new JLabel();
    this.m_jMinDate = new JTextField();
    this.jLabel1 = new JLabel();
    this.m_jCloseCash = new JButton();
    this.m_jPrintCash = new JButton();
    this.jLabel9 = new JLabel();
    this.j200 = new JTextField();
    this.jLabel10 = new JLabel();
    this.j500 = new JTextField();
    this.jLabel13 = new JLabel();
    this.j100 = new JTextField();
    this.jLabel14 = new JLabel();
    this.j50 = new JTextField();
    this.jLabel15 = new JLabel();
    this.j20 = new JTextField();
    this.jLabel16 = new JLabel();
    this.jLabel18 = new JLabel();
    this.j10 = new JTextField();
    this.j5 = new JTextField();
    this.jLabel19 = new JLabel();
    this.j2 = new JTextField();
    this.jLabel20 = new JLabel();
    this.j1 = new JTextField();
    this.jLabel21 = new JLabel();
    this.j50c = new JTextField();
    this.jPanel6.setBorder(BorderFactory.createTitledBorder(AppLocal.getIntString("label.salestitle")));
    this.m_jSalesTotal.setEditable(false);
    this.m_jSalesTotal.setHorizontalAlignment(4);
    this.m_jsalestable.setFocusable(false);
    this.m_jsalestable.setIntercellSpacing(new Dimension(0, 1));
    this.m_jsalestable.setRequestFocusEnabled(false);
    this.m_jsalestable.setShowVerticalLines(false);
    this.m_jScrollSales.setViewportView(this.m_jsalestable);
    this.m_jSalesTaxes.setEditable(false);
    this.m_jSalesTaxes.setHorizontalAlignment(4);
    this.m_jSalesSubtotal.setEditable(false);
    this.m_jSalesSubtotal.setHorizontalAlignment(4);
    this.m_jSales.setEditable(false);
    this.m_jSales.setHorizontalAlignment(4);
    this.jLabel5.setText(AppLocal.getIntString("label.sales"));
    this.jLabel6.setText(AppLocal.getIntString("label.subtotalcash"));
    this.jLabel12.setText(AppLocal.getIntString("label.taxcash"));
    this.jLabel7.setText(AppLocal.getIntString("label.totalcash"));
    GroupLayout jPanel6Layout = new GroupLayout(this.jPanel6);
    this.jPanel6.setLayout(jPanel6Layout);
    jPanel6Layout.setHorizontalGroup(jPanel6Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(jPanel6Layout.createSequentialGroup()
          .addContainerGap()
          .addComponent(this.m_jScrollSales, -2, 350, -2)
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
              .addComponent(this.jLabel5, -2, 90, -2)
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(this.m_jSales, -2, 100, -2))
            .addGroup(jPanel6Layout.createSequentialGroup()
              .addComponent(this.jLabel6, -2, 90, -2)
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(this.m_jSalesSubtotal, -2, 100, -2))
            .addGroup(jPanel6Layout.createSequentialGroup()
              .addComponent(this.jLabel12, -2, 90, -2)
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(this.m_jSalesTaxes, -2, 100, -2))
            .addGroup(jPanel6Layout.createSequentialGroup()
              .addComponent(this.jLabel7, -2, 90, -2)
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(this.m_jSalesTotal, -2, 100, -2)))
          .addContainerGap(67, 32767)));
    jPanel6Layout.setVerticalGroup(jPanel6Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(jPanel6Layout.createSequentialGroup()
          .addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(this.m_jScrollSales, -2, 140, -2)
            .addGroup(jPanel6Layout.createSequentialGroup()
              .addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.jLabel5)
                .addComponent(this.m_jSales, -2, -1, -2))
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.jLabel6)
                .addComponent(this.m_jSalesSubtotal, -2, -1, -2))
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.jLabel12)
                .addComponent(this.m_jSalesTaxes, -2, -1, -2))
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addGroup(jPanel6Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(this.jLabel7)
                .addComponent(this.m_jSalesTotal, -2, -1, -2))))
          .addContainerGap(16, 32767)));
    this.jButton1.setText("Enviar");
    this.jButton1.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            JPanelCloseMoney.this.jButton1ActionPerformed(evt);
          }
        });
    this.jLabel8.setText("No. Corte");
    this.jPanel7.setBorder(BorderFactory.createTitledBorder(AppLocal.getIntString("label.paymentstitle")));
    this.m_jScrollTableTicket1.setMinimumSize(new Dimension(550, 140));
    this.m_jScrollTableTicket1.setPreferredSize(new Dimension(550, 140));
    this.m_jTicketTable1.setFocusable(false);
    this.m_jTicketTable1.setIntercellSpacing(new Dimension(0, 1));
    this.m_jTicketTable1.setRequestFocusEnabled(false);
    this.m_jTicketTable1.setShowVerticalLines(false);
    this.m_jScrollTableTicket1.setViewportView(this.m_jTicketTable1);
    GroupLayout jPanel7Layout = new GroupLayout(this.jPanel7);
    this.jPanel7.setLayout(jPanel7Layout);
    jPanel7Layout.setHorizontalGroup(jPanel7Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
          .addContainerGap()
          .addComponent(this.m_jScrollTableTicket1, -1, 660, 32767)
          .addContainerGap()));
    jPanel7Layout.setVerticalGroup(jPanel7Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(jPanel7Layout.createSequentialGroup()
          .addComponent(this.m_jScrollTableTicket1, -1, 320, 32767)
          .addContainerGap()));
    this.jLabel3.setText(AppLocal.getIntString("Label.EndDate"));
    this.m_jMaxDate.setEditable(false);
    this.m_jMaxDate.setHorizontalAlignment(4);
    this.jResultado.setText(AppLocal.getIntString("Label.Tickets"));
    this.jLabel4.setText(AppLocal.getIntString("Label.Cash"));
    this.m_jCash.setEditable(false);
    this.m_jCash.setHorizontalAlignment(4);
    this.jPanel5.setBorder(BorderFactory.createTitledBorder(AppLocal.getIntString("label.paymentstitle")));
    this.m_jScrollTableTicket.setMinimumSize(new Dimension(350, 140));
    this.m_jScrollTableTicket.setPreferredSize(new Dimension(350, 140));
    this.m_jTicketTable.setFocusable(false);
    this.m_jTicketTable.setIntercellSpacing(new Dimension(0, 1));
    this.m_jTicketTable.setRequestFocusEnabled(false);
    this.m_jTicketTable.setShowVerticalLines(false);
    this.m_jScrollTableTicket.setViewportView(this.m_jTicketTable);
    GroupLayout jPanel5Layout = new GroupLayout(this.jPanel5);
    this.jPanel5.setLayout(jPanel5Layout);
    jPanel5Layout.setHorizontalGroup(jPanel5Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(jPanel5Layout.createSequentialGroup()
          .addContainerGap(-1, 32767)
          .addComponent(this.m_jScrollTableTicket, -2, 350, -2)
          .addGap(198, 198, 198)));
    jPanel5Layout.setVerticalGroup(jPanel5Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(jPanel5Layout.createSequentialGroup()
          .addComponent(this.m_jScrollTableTicket, -2, 140, -2)
          .addContainerGap(16, 32767)));
    this.m_jCount.setEditable(false);
    this.m_jCount.setHorizontalAlignment(4);
    this.jGranTotal.setText(AppLocal.getIntString("Label.Tickets"));
    setLayout(new BorderLayout());
    this.jPanel1.setPreferredSize(new Dimension(1024, 768));
    this.jPanel4.setBorder(BorderFactory.createTitledBorder(AppLocal.getIntString("label.datestitle")));
    this.jLabel11.setText(AppLocal.getIntString("label.sequence"));
    this.m_jSequence.setEditable(false);
    this.m_jSequence.setHorizontalAlignment(4);
    this.jLabel2.setText(AppLocal.getIntString("Label.StartDate"));
    this.m_jMinDate.setEditable(false);
    this.m_jMinDate.setHorizontalAlignment(4);
    this.jLabel1.setText(AppLocal.getIntString("Label.Tickets"));
    GroupLayout jPanel4Layout = new GroupLayout(this.jPanel4);
    this.jPanel4.setLayout(jPanel4Layout);
    jPanel4Layout.setHorizontalGroup(jPanel4Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
          .addContainerGap()
          .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
              .addComponent(this.jLabel11, -2, 140, -2)
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(this.m_jSequence, -2, 160, -2))
            .addGroup(jPanel4Layout.createSequentialGroup()
              .addComponent(this.jLabel2, -2, 140, -2)
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(this.m_jMinDate, -2, 160, -2)))
          .addGap(35, 35, 35)
          .addComponent(this.jLabel1, -2, 90, -2)
          .addContainerGap(-1, 32767)));
    jPanel4Layout.setVerticalGroup(jPanel4Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(jPanel4Layout.createSequentialGroup()
          .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(this.jLabel1)
            .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
              .addComponent(this.jLabel11)
              .addComponent(this.m_jSequence, -2, -1, -2)))
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addGroup(jPanel4Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(this.jLabel2)
            .addComponent(this.m_jMinDate, -2, -1, -2))
          .addContainerGap()));
    this.m_jCloseCash.setText(AppLocal.getIntString("Button.CloseCash"));
    this.m_jCloseCash.setPreferredSize(new Dimension(83, 43));
    this.m_jCloseCash.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            JPanelCloseMoney.this.m_jCloseCashActionPerformed(evt);
          }
        });
    this.m_jPrintCash.setText(AppLocal.getIntString("Button.PrintCash"));
    this.m_jPrintCash.setPreferredSize(new Dimension(55, 43));
    this.m_jPrintCash.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            JPanelCloseMoney.this.m_jPrintCashActionPerformed(evt);
          }
        });
    this.jLabel9.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/500.png")));
    this.j200.setFont(new Font("Tahoma", 0, 36));
    this.j200.setText("0");
    this.jLabel10.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/200.png")));
    this.jLabel10.setPreferredSize(new Dimension(282, 132));
    this.j500.setFont(new Font("Tahoma", 0, 36));
    this.j500.setText("0");
    this.j500.setPreferredSize(new Dimension(7, 50));
    this.jLabel13.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/100.png")));
    this.jLabel13.setPreferredSize(new Dimension(282, 132));
    this.j100.setFont(new Font("Tahoma", 0, 36));
    this.j100.setText("0");
    this.jLabel14.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/50.png")));
    this.jLabel14.setPreferredSize(new Dimension(282, 132));
    this.j50.setFont(new Font("Tahoma", 0, 36));
    this.j50.setText("0");
    this.jLabel15.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/20.png")));
    this.jLabel15.setPreferredSize(new Dimension(282, 132));
    this.j20.setFont(new Font("Tahoma", 0, 36));
    this.j20.setText("0");
    this.jLabel16.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/10.png")));
    this.jLabel18.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/5.png")));
    this.j10.setFont(new Font("Tahoma", 0, 36));
    this.j10.setText("0");
    this.j5.setFont(new Font("Tahoma", 0, 36));
    this.j5.setText("0");
    this.jLabel19.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/2.png")));
    this.j2.setFont(new Font("Tahoma", 0, 36));
    this.j2.setText("0");
    this.jLabel20.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/1.png")));
    this.j1.setFont(new Font("Tahoma", 0, 36));
    this.j1.setText("0");
    this.j1.setToolTipText("");
    this.jLabel21.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/50C.png")));
    this.j50c.setFont(new Font("Tahoma", 0, 36));
    this.j50c.setText("0");
    GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
    this.jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(jPanel1Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
          .addContainerGap()
          .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(this.jPanel4, -1, -1, 32767)
            .addGroup(jPanel1Layout.createSequentialGroup()
              .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                  .addComponent(this.m_jPrintCash, -2, 83, -2)
                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                  .addComponent(this.m_jCloseCash, -2, 132, -2))
                .addGroup(jPanel1Layout.createSequentialGroup()
                  .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(this.jLabel9)
                    .addComponent(this.j500, -2, 100, -2))
                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                  .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(this.jLabel10, -2, -1, -2)
                    .addComponent(this.j200, -2, 100, -2))
                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                  .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(this.jLabel13, -2, -1, -2)
                    .addComponent(this.j100, -2, 100, -2)))
                .addGroup(jPanel1Layout.createSequentialGroup()
                  .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                      .addComponent(this.jLabel14, -2, -1, -2)
                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                      .addComponent(this.j50)
                      .addGap(188, 188, 188)))
                  .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(this.jLabel15, -2, -1, -2)
                    .addComponent(this.j20, -2, 101, -2)))
                .addGroup(jPanel1Layout.createSequentialGroup()
                  .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(this.jLabel16)
                    .addComponent(this.j10, -2, 100, -2))
                  .addGap(18, 18, 18)
                  .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addComponent(this.j5, GroupLayout.Alignment.LEADING)
                    .addComponent(this.jLabel18))
                  .addGap(18, 18, 18)
                  .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addComponent(this.j2, GroupLayout.Alignment.LEADING)
                    .addComponent(this.jLabel19))
                  .addGap(18, 18, 18)
                  .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addComponent(this.j1, GroupLayout.Alignment.LEADING)
                    .addComponent(this.jLabel20))
                  .addGap(18, 18, 18)
                  .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addComponent(this.j50c, GroupLayout.Alignment.LEADING)
                    .addComponent(this.jLabel21))))
              .addGap(0, 146, 32767)))
          .addContainerGap()));
    jPanel1Layout.setVerticalGroup(jPanel1Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
          .addContainerGap()
          .addComponent(this.jPanel4, -2, -1, -2)
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
              .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(this.jLabel9)
                .addComponent(this.jLabel10, -2, -1, -2))
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(this.j200, -2, -1, -2)
                .addComponent(this.j500, -2, -1, -2)))
            .addGroup(jPanel1Layout.createSequentialGroup()
              .addComponent(this.jLabel13, -2, -1, -2)
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(this.j100, -2, -1, -2)))
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
              .addComponent(this.jLabel14, -2, -1, -2)
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(this.j50, -2, -1, -2))
            .addGroup(jPanel1Layout.createSequentialGroup()
              .addComponent(this.jLabel15, -2, -1, -2)
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(this.j20, -2, -1, -2)))
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
              .addComponent(this.jLabel16)
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(this.j10, -2, -1, -2))
            .addGroup(jPanel1Layout.createSequentialGroup()
              .addComponent(this.jLabel18)
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(this.j5, -2, -1, -2))
            .addGroup(jPanel1Layout.createSequentialGroup()
              .addComponent(this.jLabel19)
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(this.j2, -2, -1, -2))
            .addGroup(jPanel1Layout.createSequentialGroup()
              .addComponent(this.jLabel21)
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(this.j50c, -2, -1, -2))
            .addGroup(jPanel1Layout.createSequentialGroup()
              .addComponent(this.jLabel20)
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
              .addComponent(this.j1, -2, -1, -2)))
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 40, 32767)
          .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(this.m_jCloseCash, -2, -1, -2)
            .addComponent(this.m_jPrintCash, -2, -1, -2))
          .addContainerGap()));
    add(this.jPanel1, "Center");
  }
  
  private void jButton1ActionPerformed(ActionEvent evt) {
    if (this.jTextField1.getText().trim().equals("")) {
      JOptionPane.showMessageDialog(this, "Debe escribir un numero de corte.");
    } else {
      try {
        int corte = Integer.parseInt(this.jTextField1.getText().trim());
        if (JOptionPane.showConfirmDialog(this, "Esta seguro de volver a enviar el corte numero " + this.jTextField1.getText().trim()) == 0) {
          AppConfig config = new AppConfig(new File(System.getProperty("user.home") + File.separator + "openbravopos.properties"));
          config.load();
          Properties props = new Properties();
          props.put("mail.smtp.auth", "true");
          props.put("mail.smtp.starttls.enable", "true");
          props.put("mail.smtp.host", "smtp.office365.com");
          props.put("mail.smtp.user", config.getProperty("correoEmisor"));
          props.put("mail.smtp.password", config.getProperty("claveCorreoEmisor"));
          props.put("mail.smtp.port", "587");
          Session session = Session.getInstance(props, new GMailAuthenticator(config.getProperty("correoEmisor"), config.getProperty("claveCorreoEmisor")));
          try {
            PaymentsModel p = PaymentsModel.emptyInstance();
            try {
              String corteMoney = p.findMoney(this.m_App, this.jTextField1.getText().trim());
              String inicio = p.findDateStart(this.m_App, this.jTextField1.getText().trim());
              String fin = p.findDateEnd(this.m_App, this.jTextField1.getText().trim());
              System.out.println("correoEmisor: " + config.getProperty("correoEmisor"));
              System.out.println("claveCorreoEmisor: " + config.getProperty("claveCorreoEmisor"));
              System.out.println("correoReceptor1: " + config.getProperty("correoReceptor1"));
              MimeMessage mimeMessage = new MimeMessage(session);
              mimeMessage.setFrom((Address)new InternetAddress(config.getProperty("correoEmisor")));
              mimeMessage.setRecipients(Message.RecipientType.TO, 
                  (Address[])InternetAddress.parse(config.getProperty("correoReceptor1")));
              mimeMessage.setSubject("Cierre de caja " + this.jTextField1.getText().trim());
              try {
                PaymentsModel m_PaymentsToClose2 = PaymentsModel.loadInstance(this.m_App, corteMoney);
                String body = "<html><table><tr><td colspan=\"4\">*REPORTE DE CIERRE DE CAJA " + config.getProperty("machine.hostname") + ".</td></tr><tr><td colspan=\"4\"><hr/></td></tr><tr><td colspan=\"2\">Secuencia:</td><td colspan=\"2\">" + this.jTextField1.getText().trim() + "</td></tr><tr><td colspan=\"2\">Fecha de inicio:</td><td colspan=\"2\">" + inicio + "</td></tr><tr><td colspan=\"2\">Fecha de fin:</td><td colspan=\"2\">" + fin + "</td></tr><tr><td colspan=\"4\"><hr/></td></tr><tr></tr><tr><td colspan=\"4\">*REPORTE DE PAGOS.</td></tr><tr><td colspan=\"4\"><hr/></td></tr>";
                List<PaymentsModel.PaymentsLine> payments = m_PaymentsToClose2.getPaymentLines();
                for (int i = 0; i < payments.size(); i++)
                  body = body + "<tr><td colspan=\"2\">" + ((PaymentsModel.PaymentsLine)payments.get(i)).printType() + "</td><td colspan=\"2\">" + ((PaymentsModel.PaymentsLine)payments.get(i)).printValue() + "</td></tr>"; 
                body = body + "<tr><td colspan=\"2\">TOTAL:</td><td colspan=\"2\">" + this.m_jCash.getText() + "</td></tr>";
                body = body + "<tr><td colspan=\"4\"><hr/></td></tr><tr></tr><tr><td colspan=\"4\">*REPORTE DE PRODUCTOS VENDIDOS.</td></tr><tr><td colspan=\"4\"><hr/></td></tr>";
                body = body + "<tr><td>PRODUCTO</td><td>CANT</td><td>TOTAL</td><td>GANANCIAS</td></tr>";
                List<PaymentsModel.ProductSalesLine> productSales = m_PaymentsToClose2.getProductSalesLines();
                Double units = Double.valueOf(0.0D);
                Double total = Double.valueOf(0.0D);
                Double ganancias = Double.valueOf(0.0D);
                for (int j = 0; j < productSales.size(); j++) {
                  units = Double.valueOf(units.doubleValue() + ((PaymentsModel.ProductSalesLine)productSales.get(j)).getProductUnits().doubleValue());
                  total = Double.valueOf(total.doubleValue() + ((PaymentsModel.ProductSalesLine)productSales.get(j)).getProductTotal().doubleValue());
                  ganancias = Double.valueOf(ganancias.doubleValue() + ((PaymentsModel.ProductSalesLine)productSales.get(j)).getProductGanancias().doubleValue());
                  body = body + "<tr><td>" + ((PaymentsModel.ProductSalesLine)productSales.get(j)).printProductName() + "</td><td>" + Formats.DOUBLE.formatValue(((PaymentsModel.ProductSalesLine)productSales.get(j)).getProductUnits()) + "</td><td>" + ((PaymentsModel.ProductSalesLine)productSales.get(j)).printTotal() + "</td><td>" + ((PaymentsModel.ProductSalesLine)productSales.get(j)).printGanancias() + "</td></tr>";
                } 
                body = body + "<tr><td>TOTAL:</td><td>" + Formats.DOUBLE.formatValue(units).trim() + "</td><td>" + Formats.CURRENCY.formatValue(total).trim() + "</td><td>&nbsp;&nbsp;&nbsp;" + Formats.CURRENCY.formatValue(ganancias).trim() + "</td></tr>";
                body = body + "<tr><td colspan=\"4\"><hr/></td></tr><tr></tr><tr><td colspan=\"4\">REPORTE DE INVENTARIO ACTUAL.</td></tr><tr><td colspan=\"4\"><hr/></td></tr>";
                List<PaymentsModel.Stock> stock = m_PaymentsToClose2.getStockLines();
                for (int k = 0; k < stock.size(); k++)
                  body = body + "<tr><td colspan=\"2\">" + ((PaymentsModel.Stock)stock.get(k)).printProductName() + "</td><td colspan=\"2\">" + ((PaymentsModel.Stock)stock.get(k)).printUnits().toString() + "</td></tr>"; 
                mimeMessage.setContent(body, "text/html");
                Transport transport = session.getTransport("smtp");
                transport.connect("smtp.office365.com", 587, config.getProperty("correoEmisor"), config.getProperty("claveCorreoEmisor"));
                mimeMessage.saveChanges();
                transport.sendMessage((Message)mimeMessage, mimeMessage.getAllRecipients());
                transport.close();
                System.out.println("ENVIO EXITOSO");
                JOptionPane.showMessageDialog(this, "ENVIO EXITOSO AL CORREO: " + config.getProperty("correoReceptor1"));
              } catch (BasicException ex) {
                JOptionPane.showMessageDialog(this, "ERROR: " + ex.toString());
              } 
            } catch (BasicException ex) {
              JOptionPane.showMessageDialog(this, "ERROR: " + ex.toString());
            } 
          } catch (MessagingException ex) {
            System.out.println("ERROR:" + ex.toString());
            JOptionPane.showMessageDialog(this, "ERROR: " + ex.toString());
          } 
        } 
      } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this, "Debe escribir un numero de corte valido.");
      } 
    } 
  }
  
  private void m_jPrintCashActionPerformed(ActionEvent evt) {
    /*if (!isNumeric(this.j500.getText()) || !isNumeric(this.j200.getText()) || !isNumeric(this.j100.getText()) || !isNumeric(this.j50.getText()) || 
      !isNumeric(this.j20.getText()) || !isNumeric(this.j10.getText()) || !isNumeric(this.j5.getText()) || !isNumeric(this.j2.getText()) || 
      !isNumeric(this.j1.getText()) || !isNumeric(this.j50c.getText())) {
      JOptionPane.showMessageDialog(this, "NO SE PUEDE REALIZAR EL CALCULO, REVISE LAS CANTIDADES");
      return;
    } 
    Double granTotal = Double.valueOf(0.0D);
    granTotal = Double.valueOf(granTotal.doubleValue() + Double.parseDouble(this.j500.getText()) * 500.0D);
    granTotal = Double.valueOf(granTotal.doubleValue() + Double.parseDouble(this.j200.getText()) * 200.0D);
    granTotal = Double.valueOf(granTotal.doubleValue() + Double.parseDouble(this.j100.getText()) * 100.0D);
    granTotal = Double.valueOf(granTotal.doubleValue() + Double.parseDouble(this.j50.getText()) * 50.0D);
    granTotal = Double.valueOf(granTotal.doubleValue() + Double.parseDouble(this.j20.getText()) * 20.0D);
    granTotal = Double.valueOf(granTotal.doubleValue() + Double.parseDouble(this.j10.getText()) * 10.0D);
    granTotal = Double.valueOf(granTotal.doubleValue() + Double.parseDouble(this.j5.getText()) * 5.0D);
    granTotal = Double.valueOf(granTotal.doubleValue() + Double.parseDouble(this.j2.getText()) * 2.0D);
    granTotal = Double.valueOf(granTotal.doubleValue() + Double.parseDouble(this.j1.getText()) * 1.0D);
    granTotal = Double.valueOf(granTotal.doubleValue() + Double.parseDouble(this.j50c.getText()) * 0.5D);
    this.jGranTotal.setText(granTotal.toString());
    Double resultado = Double.valueOf(granTotal.doubleValue() - this.m_PaymentsToClose.getTotal());
    this.jResultado.setText(resultado.toString());
      printPayments("Printer.PartialCash");*/
  }
  
  private static boolean isNumeric(String cadena) {
    try {
      Integer.parseInt(cadena);
      return true;
    } catch (NumberFormatException nfe) {
      return false;
    } 
  }
  
  private void m_jCloseCashActionPerformed(ActionEvent evt) {
    if (!isNumeric(this.j500.getText()) || !isNumeric(this.j200.getText()) || !isNumeric(this.j100.getText()) || !isNumeric(this.j50.getText()) || 
      !isNumeric(this.j20.getText()) || !isNumeric(this.j10.getText()) || !isNumeric(this.j5.getText()) || !isNumeric(this.j2.getText()) || 
      !isNumeric(this.j1.getText()) || !isNumeric(this.j50c.getText())) {
      JOptionPane.showMessageDialog(this, "NO SE PUEDE REALIZAR EL CALCULO, REVISE LAS CANTIDADES");
      return;
    } 
    Double granTotal = Double.valueOf(0.0D);
    granTotal = Double.valueOf(granTotal.doubleValue() + Double.parseDouble(this.j500.getText()) * 500.0D);
    granTotal = Double.valueOf(granTotal.doubleValue() + Double.parseDouble(this.j200.getText()) * 200.0D);
    granTotal = Double.valueOf(granTotal.doubleValue() + Double.parseDouble(this.j100.getText()) * 100.0D);
    granTotal = Double.valueOf(granTotal.doubleValue() + Double.parseDouble(this.j50.getText()) * 50.0D);
    granTotal = Double.valueOf(granTotal.doubleValue() + Double.parseDouble(this.j20.getText()) * 20.0D);
    granTotal = Double.valueOf(granTotal.doubleValue() + Double.parseDouble(this.j10.getText()) * 10.0D);
    granTotal = Double.valueOf(granTotal.doubleValue() + Double.parseDouble(this.j5.getText()) * 5.0D);
    granTotal = Double.valueOf(granTotal.doubleValue() + Double.parseDouble(this.j2.getText()) * 2.0D);
    granTotal = Double.valueOf(granTotal.doubleValue() + Double.parseDouble(this.j1.getText()) * 1.0D);
    granTotal = Double.valueOf(granTotal.doubleValue() + Double.parseDouble(this.j50c.getText()) * 0.5D);
    this.jGranTotal.setText(granTotal.toString());
    Double resultado = Double.valueOf(granTotal.doubleValue() - this.m_PaymentsToClose.getTotal());
    this.jResultado.setText(resultado.toString());
    int res = JOptionPane.showConfirmDialog(this, 
        AppLocal.getIntString("message.wannaclosecash"), 
        AppLocal.getIntString("message.title"), 0, 3);
    if (res == 0) {
      Date dNow = new Date();
      try {
        if (this.m_App.getActiveCashDateEnd() == null)
          (new StaticSentence(this.m_App.getSession(), "UPDATE CLOSEDCASH SET DATEEND = ? WHERE DATEEND IS NULL", (SerializerWrite)new SerializerWriteBasic(new Datas[] { Datas.TIMESTAMP }))).exec(new Object[] { dNow }); 
      } catch (BasicException e) {
        MessageInf msg = new MessageInf(-67108864, AppLocal.getIntString("message.cannotclosecash"), e);
        msg.show(this);
      } 
      try {
        (new StaticSentence(this.m_App.getSession(), "UPDATE CLOSEDCASH SET DATEEND = ? WHERE DATEEND IS NULL", (SerializerWrite)new SerializerWriteBasic(new Datas[] { Datas.TIMESTAMP }))).exec(new Object[] { dNow });
        this.m_App.setActiveCash(UUID.randomUUID().toString(), this.m_App.getActiveCashSequence() + 1, dNow, null);
        this.m_dlSystem.execInsertCash(new Object[] { this.m_App
              .getActiveCashIndex(), this.m_App.getProperties().getHost(), Integer.valueOf(this.m_App.getActiveCashSequence()), this.m_App.getActiveCashDateStart(), this.m_App.getActiveCashDateEnd() });
        this.m_PaymentsToClose.setDateEnd(dNow);
        printPayments("Printer.CloseCash");
        JOptionPane.showMessageDialog(this, AppLocal.getIntString("message.closecashok"), AppLocal.getIntString("message.title"), 1);
      } catch (BasicException e) {
        MessageInf msg = new MessageInf(-67108864, AppLocal.getIntString("message.cannotclosecash"), e);
        msg.show(this);
      } 
      try {
        loadData();
      } catch (BasicException e) {
        MessageInf msg = new MessageInf(-67108864, AppLocal.getIntString("label.noticketstoclose"), e);
        msg.show(this);
      } 
    } else {
      this.jGranTotal.setText((String)null);
      this.jResultado.setText((String)null);
    } 
  }
}
