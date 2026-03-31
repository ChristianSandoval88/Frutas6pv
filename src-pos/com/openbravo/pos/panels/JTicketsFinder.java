package com.openbravo.pos.panels;

import com.openbravo.basic.BasicException;
import com.openbravo.beans.JCalendarDialog;
import com.openbravo.data.gui.ComboBoxValModel;
import com.openbravo.data.gui.ListQBFModelNumber;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.loader.QBFCompareEnum;
import com.openbravo.data.loader.SentenceList;
import com.openbravo.data.user.EditorCreator;
import com.openbravo.data.user.ListProvider;
import com.openbravo.data.user.ListProviderCreator;
import com.openbravo.editor.EditorKeys;
import com.openbravo.editor.JEditorCurrency;
import com.openbravo.editor.JEditorIntegerPositive;
import com.openbravo.editor.JEditorKeys;
import com.openbravo.format.Formats;
import com.openbravo.pos.customers.DataLogicCustomers;
import com.openbravo.pos.customers.JCustomerFinder;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.DataLogicSales;
import com.openbravo.pos.inventory.TaxCategoryInfo;
import com.openbravo.pos.ticket.FindTicketsInfo;
import com.openbravo.pos.ticket.FindTicketsRenderer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class JTicketsFinder extends JDialog implements EditorCreator {
  private ListProvider lpr;
  
  private SentenceList m_sentcat;
  
  private ComboBoxValModel m_CategoryModel;
  
  private DataLogicSales dlSales;
  
  private DataLogicCustomers dlCustomers;
  
  private FindTicketsInfo selectedTicket;
  
  private String location;
  
  private JButton btnCustomer;
  
  private JButton btnDateEnd;
  
  private JButton btnDateStart;
  
  private JButton jButton1;
  
  private JButton jButton3;
  
  private JComboBox jComboBoxTicket;
  
  private JLabel jLabel1;
  
  private JLabel jLabel2;
  
  private JLabel jLabel3;
  
  private JLabel jLabel4;
  
  private JLabel jLabel5;
  
  private JLabel jLabel6;
  
  private JLabel jLabel7;
  
  private JList jListTickets;
  
  private JPanel jPanel1;
  
  private JPanel jPanel2;
  
  private JPanel jPanel3;
  
  private JPanel jPanel4;
  
  private JPanel jPanel5;
  
  private JPanel jPanel6;
  
  private JPanel jPanel7;
  
  private JPanel jPanel8;
  
  private JScrollPane jScrollPane1;
  
  private JTextField jTxtEndDate;
  
  private JTextField jTxtStartDate;
  
  private JComboBox jcboMoney;
  
  private JComboBox jcboUser;
  
  private JButton jcmdCancel;
  
  private JButton jcmdOK;
  
  private JTextField jtxtCustomer;
  
  private JEditorCurrency jtxtMoney;
  
  private JEditorIntegerPositive jtxtTicketID;
  
  private JLabel labelCustomer;
  
  private JEditorKeys m_jKeys;
  
  private JComboBox m_jreason;
  
  private JComboBox m_jreason1;
  
  private JTicketsFinder(Frame parent, boolean modal) {
    super(parent, modal);
  }
  
  private JTicketsFinder(Dialog parent, boolean modal) {
    super(parent, modal);
  }
  
  public static JTicketsFinder getReceiptFinder(Component parent, DataLogicSales dlSales, DataLogicCustomers dlCustomers, String location) {
    JTicketsFinder myMsg;
    Window window = getWindow(parent);
    if (window instanceof Frame) {
      myMsg = new JTicketsFinder((Frame)window, true);
    } else {
      myMsg = new JTicketsFinder((Dialog)window, true);
    } 
    myMsg.init(dlSales, dlCustomers, location);
    myMsg.applyComponentOrientation(parent.getComponentOrientation());
    return myMsg;
  }
  
  public FindTicketsInfo getSelectedCustomer() {
    return this.selectedTicket;
  }
  
  private void init(DataLogicSales dlSales, DataLogicCustomers dlCustomers, String location) {
    this.dlSales = dlSales;
    this.dlCustomers = dlCustomers;
    this.location = location;
    initComponents();
    this.jScrollPane1.getVerticalScrollBar().setPreferredSize(new Dimension(35, 35));
    this.jtxtTicketID.addEditorKeys((EditorKeys)this.m_jKeys);
    this.jtxtMoney.addEditorKeys((EditorKeys)this.m_jKeys);
    this.lpr = (ListProvider)new ListProviderCreator(dlSales.getTicketsList(this.location), this);
    this.jListTickets.setCellRenderer((ListCellRenderer)new FindTicketsRenderer());
    getRootPane().setDefaultButton(this.jcmdOK);
    initCombos();
    defaultValues();
    this.selectedTicket = null;
  }
  
  public void executeSearch() {
    try {
      this.jListTickets.setModel(new MyListData(this.lpr.loadData()));
      if (this.jListTickets.getModel().getSize() > 0)
        this.jListTickets.setSelectedIndex(0); 
    } catch (BasicException e) {
      e.printStackTrace();
    } 
  }
  
  private void initCombos() {
    String[] values = { AppLocal.getIntString("label.sales"), AppLocal.getIntString("label.refunds"), AppLocal.getIntString("label.all") };
    this.jComboBoxTicket.setModel(new DefaultComboBoxModel<>(values));
    this.jcboMoney.setModel((ComboBoxModel)ListQBFModelNumber.getMandatoryNumber());
    this.m_sentcat = this.dlSales.getUserList();
    this.m_CategoryModel = new ComboBoxValModel();
    List catlist = null;
    try {
      catlist = this.m_sentcat.list();
    } catch (BasicException ex) {
      ex.getMessage();
    } 
    catlist.add(0, null);
    this.m_CategoryModel = new ComboBoxValModel(catlist);
    this.jcboUser.setModel((ComboBoxModel)this.m_CategoryModel);
  }
  
  private void defaultValues() {
    this.jListTickets.setModel(new MyListData(new ArrayList()));
    this.jcboUser.setSelectedItem((Object)null);
    this.jtxtTicketID.reset();
    this.jtxtTicketID.activate();
    this.jComboBoxTicket.setSelectedIndex(0);
    this.jcboUser.setSelectedItem((Object)null);
    this.jcboMoney.setSelectedItem(((ListQBFModelNumber)this.jcboMoney.getModel()).getElementAt(0));
    this.jcboMoney.revalidate();
    this.jcboMoney.repaint();
    this.jtxtMoney.reset();
    this.jTxtStartDate.setText((String)null);
    this.jTxtEndDate.setText((String)null);
    this.jtxtCustomer.setText((String)null);
  }
  
  public Object createValue() throws BasicException {
    Object[] afilter = new Object[18];
    if (this.jtxtTicketID.getText() == null || this.jtxtTicketID.getText().equals("")) {
      afilter[0] = QBFCompareEnum.COMP_NONE;
      afilter[1] = null;
    } else {
      afilter[0] = QBFCompareEnum.COMP_EQUALS;
      afilter[1] = Integer.valueOf(this.jtxtTicketID.getValueInteger());
    } 
    if (this.jComboBoxTicket.getSelectedIndex() == 2) {
      afilter[2] = QBFCompareEnum.COMP_DISTINCT;
      afilter[3] = Integer.valueOf(2);
    } else if (this.jComboBoxTicket.getSelectedIndex() == 0) {
      afilter[2] = QBFCompareEnum.COMP_EQUALS;
      afilter[3] = Integer.valueOf(0);
    } else if (this.jComboBoxTicket.getSelectedIndex() == 1) {
      afilter[2] = QBFCompareEnum.COMP_EQUALS;
      afilter[3] = Integer.valueOf(1);
    } 
    afilter[5] = this.jtxtMoney.getDoubleValue();
    afilter[4] = (afilter[5] == null) ? QBFCompareEnum.COMP_NONE : this.jcboMoney.getSelectedItem();
    Object startdate = Formats.TIMESTAMP.parseValue(this.jTxtStartDate.getText());
    Object enddate = Formats.TIMESTAMP.parseValue(this.jTxtEndDate.getText());
    afilter[6] = (startdate == null) ? QBFCompareEnum.COMP_NONE : QBFCompareEnum.COMP_GREATEROREQUALS;
    afilter[7] = startdate;
    afilter[8] = (enddate == null) ? QBFCompareEnum.COMP_NONE : QBFCompareEnum.COMP_LESS;
    afilter[9] = enddate;
    if (this.jcboUser.getSelectedItem() == null) {
      afilter[10] = QBFCompareEnum.COMP_NONE;
      afilter[11] = null;
    } else {
      afilter[10] = QBFCompareEnum.COMP_EQUALS;
      afilter[11] = ((TaxCategoryInfo)this.jcboUser.getSelectedItem()).getName();
    } 
    if (this.jtxtCustomer.getText() == null || this.jtxtCustomer.getText().equals("")) {
      afilter[12] = QBFCompareEnum.COMP_NONE;
      afilter[13] = null;
    } else {
      afilter[12] = QBFCompareEnum.COMP_RE;
      afilter[13] = "%" + this.jtxtCustomer.getText() + "%";
    } 
    if (this.m_jreason.getSelectedIndex() == 0) {
      afilter[14] = QBFCompareEnum.COMP_NONE;
      afilter[15] = null;
    } else {
      afilter[14] = QBFCompareEnum.COMP_RE;
      afilter[15] = "%" + this.m_jreason.getSelectedItem().toString() + "%";
    } 
    if (this.m_jreason1.getSelectedIndex() == 0) {
      afilter[16] = QBFCompareEnum.COMP_NONE;
      afilter[17] = null;
    } else {
      afilter[16] = QBFCompareEnum.COMP_RE;
      afilter[17] = "%" + this.m_jreason1.getSelectedItem().toString() + "%";
    } 
    return afilter;
  }
  
  private static Window getWindow(Component parent) {
    if (parent == null)
      return new JFrame(); 
    if (parent instanceof Frame || parent instanceof Dialog)
      return (Window)parent; 
    return getWindow(parent.getParent());
  }
  
  private static class MyListData extends AbstractListModel {
    private List m_data;
    
    public MyListData(List data) {
      this.m_data = data;
    }
    
    public Object getElementAt(int index) {
      return this.m_data.get(index);
    }
    
    public int getSize() {
      return this.m_data.size();
    }
  }
  
  private void initComponents() {
    this.jPanel3 = new JPanel();
    this.jPanel5 = new JPanel();
    this.jPanel7 = new JPanel();
    this.jLabel1 = new JLabel();
    this.jLabel6 = new JLabel();
    this.jLabel7 = new JLabel();
    this.jtxtMoney = new JEditorCurrency();
    this.jcboUser = new JComboBox();
    this.jcboMoney = new JComboBox();
    this.jtxtTicketID = new JEditorIntegerPositive();
    this.labelCustomer = new JLabel();
    this.jLabel3 = new JLabel();
    this.jLabel4 = new JLabel();
    this.jTxtStartDate = new JTextField();
    this.jTxtEndDate = new JTextField();
    this.btnDateStart = new JButton();
    this.btnDateEnd = new JButton();
    this.jtxtCustomer = new JTextField();
    this.btnCustomer = new JButton();
    this.jComboBoxTicket = new JComboBox();
    this.m_jreason = new JComboBox();
    this.jLabel2 = new JLabel();
    this.m_jreason1 = new JComboBox();
    this.jLabel5 = new JLabel();
    this.jPanel6 = new JPanel();
    this.jButton1 = new JButton();
    this.jButton3 = new JButton();
    this.jPanel4 = new JPanel();
    this.jScrollPane1 = new JScrollPane();
    this.jListTickets = new JList();
    this.jPanel8 = new JPanel();
    this.jPanel1 = new JPanel();
    this.jcmdOK = new JButton();
    this.jcmdCancel = new JButton();
    this.jPanel2 = new JPanel();
    this.m_jKeys = new JEditorKeys();
    setDefaultCloseOperation(2);
    setTitle(AppLocal.getIntString("form.tickettitle"));
    setPreferredSize(new Dimension(700, 476));
    this.jPanel3.setPreferredSize(new Dimension(468, 476));
    this.jPanel3.setLayout(new BorderLayout());
    this.jPanel5.setLayout(new BorderLayout());
    this.jPanel7.setPreferredSize(new Dimension(0, 250));
    this.jLabel1.setText(AppLocal.getIntString("label.ticketid"));
    this.jLabel6.setText(AppLocal.getIntString("label.user"));
    this.jLabel7.setText(AppLocal.getIntString("label.totalcash"));
    this.labelCustomer.setText(AppLocal.getIntString("label.customer"));
    this.jLabel3.setText(AppLocal.getIntString("Label.StartDate"));
    this.jLabel4.setText(AppLocal.getIntString("Label.EndDate"));
    this.jTxtStartDate.setPreferredSize(new Dimension(200, 25));
    this.jTxtEndDate.setPreferredSize(new Dimension(200, 25));
    this.btnDateStart.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/date.png")));
    this.btnDateStart.setPreferredSize(new Dimension(50, 25));
    this.btnDateStart.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            JTicketsFinder.this.btnDateStartActionPerformed(evt);
          }
        });
    this.btnDateEnd.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/date.png")));
    this.btnDateEnd.setPreferredSize(new Dimension(50, 25));
    this.btnDateEnd.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            JTicketsFinder.this.btnDateEndActionPerformed(evt);
          }
        });
    this.jtxtCustomer.setPreferredSize(new Dimension(200, 25));
    this.btnCustomer.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/kuser.png")));
    this.btnCustomer.setFocusPainted(false);
    this.btnCustomer.setFocusable(false);
    this.btnCustomer.setMargin(new Insets(8, 14, 8, 14));
    this.btnCustomer.setPreferredSize(new Dimension(50, 25));
    this.btnCustomer.setRequestFocusEnabled(false);
    this.btnCustomer.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            JTicketsFinder.this.btnCustomerActionPerformed(evt);
          }
        });
    this.m_jreason.setModel(new DefaultComboBoxModel<>(new String[] { " ", "CAJA1", "CAJA2", "CAJA3", "CAJA4", "CAJA5", "CAJA6", "CAJA7" }));
    this.jLabel2.setText("Caja");
    this.m_jreason1.setModel(new DefaultComboBoxModel<>(new String[] { " ", "TARJETA", "TRANSFERENCIA" }));
    this.jLabel5.setText("Método");
    GroupLayout jPanel7Layout = new GroupLayout(this.jPanel7);
    this.jPanel7.setLayout(jPanel7Layout);
    jPanel7Layout.setHorizontalGroup(jPanel7Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
          .addGap(33, 33, 33)
          .addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(this.jLabel1)
            .addComponent(this.jLabel3)
            .addComponent(this.jLabel4)
            .addComponent(this.jLabel7)
            .addComponent(this.jLabel6)
            .addComponent(this.labelCustomer)
            .addComponent(this.jLabel2, -2, 100, -2)
            .addComponent(this.jLabel5, -2, 100, -2))
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
              .addComponent((Component)this.jtxtTicketID, -2, 120, -2)
              .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, -1, 32767)
              .addComponent(this.jComboBoxTicket, -2, 130, -2)
              .addGap(217, 217, 217))
            .addGroup(jPanel7Layout.createSequentialGroup()
              .addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                .addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                  .addComponent(this.jcboUser, -2, 255, -2)
                  .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(this.jcboMoney, -2, 100, -2)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent((Component)this.jtxtMoney, -2, 182, -2))
                  .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(this.jtxtCustomer, -2, -1, -2)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(this.btnCustomer, -2, -1, -2))
                  .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(this.jTxtEndDate, -2, -1, -2)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(this.btnDateEnd, -2, -1, -2))
                  .addGroup(jPanel7Layout.createSequentialGroup()
                    .addComponent(this.jTxtStartDate, -2, -1, -2)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(this.btnDateStart, -2, -1, -2))
                  .addComponent(this.m_jreason, -2, 255, -2))
                .addGroup(jPanel7Layout.createSequentialGroup()
                  .addComponent(this.m_jreason1, -2, 255, -2)
                  .addGap(33, 33, 33)))
              .addContainerGap(-1, 32767)))));
    jPanel7Layout.setVerticalGroup(jPanel7Layout
        .createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGroup(jPanel7Layout.createSequentialGroup()
          .addContainerGap()
          .addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(this.jLabel1)
            .addComponent((Component)this.jtxtTicketID, -2, -1, -2)
            .addComponent(this.jComboBoxTicket, -2, -1, -2))
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(this.jLabel3)
            .addComponent(this.jTxtStartDate, -2, -1, -2)
            .addComponent(this.btnDateStart, -2, -1, -2))
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(this.jLabel4)
            .addComponent(this.jTxtEndDate, -2, -1, -2)
            .addComponent(this.btnDateEnd, -2, -1, -2))
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(this.labelCustomer)
            .addComponent(this.jtxtCustomer, -2, -1, -2)
            .addComponent(this.btnCustomer, -2, -1, -2))
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(this.jLabel6)
            .addComponent(this.jcboUser, -2, -1, -2))
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.CENTER)
            .addComponent(this.jLabel7)
            .addComponent(this.jcboMoney, -2, -1, -2)
            .addComponent((Component)this.jtxtMoney, -2, -1, -2))
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(this.m_jreason, -2, -1, -2)
            .addComponent(this.jLabel2))
          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
          .addGroup(jPanel7Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(this.m_jreason1, -2, -1, -2)
            .addComponent(this.jLabel5))
          .addContainerGap(12, 32767)));
    this.jPanel5.add(this.jPanel7, "Center");
    this.jButton1.setText(AppLocal.getIntString("button.clean"));
    this.jButton1.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            JTicketsFinder.this.jButton1ActionPerformed(evt);
          }
        });
    this.jPanel6.add(this.jButton1);
    this.jButton3.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/launch.png")));
    this.jButton3.setText(AppLocal.getIntString("button.executefilter"));
    this.jButton3.setFocusPainted(false);
    this.jButton3.setFocusable(false);
    this.jButton3.setRequestFocusEnabled(false);
    this.jButton3.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            JTicketsFinder.this.jButton3ActionPerformed(evt);
          }
        });
    this.jPanel6.add(this.jButton3);
    this.jPanel5.add(this.jPanel6, "South");
    this.jPanel3.add(this.jPanel5, "First");
    this.jPanel4.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    this.jPanel4.setLayout(new BorderLayout());
    this.jListTickets.setFocusable(false);
    this.jListTickets.setRequestFocusEnabled(false);
    this.jListTickets.addMouseListener(new MouseAdapter() {
          public void mouseClicked(MouseEvent evt) {
            JTicketsFinder.this.jListTicketsMouseClicked(evt);
          }
        });
    this.jListTickets.addListSelectionListener(new ListSelectionListener() {
          public void valueChanged(ListSelectionEvent evt) {
            JTicketsFinder.this.jListTicketsValueChanged(evt);
          }
        });
    this.jScrollPane1.setViewportView(this.jListTickets);
    this.jPanel4.add(this.jScrollPane1, "Center");
    this.jPanel3.add(this.jPanel4, "Center");
    this.jPanel8.setLayout(new BorderLayout());
    this.jcmdOK.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/button_ok.png")));
    this.jcmdOK.setText(AppLocal.getIntString("Button.OK"));
    this.jcmdOK.setEnabled(false);
    this.jcmdOK.setFocusPainted(false);
    this.jcmdOK.setFocusable(false);
    this.jcmdOK.setMargin(new Insets(8, 16, 8, 16));
    this.jcmdOK.setRequestFocusEnabled(false);
    this.jcmdOK.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            JTicketsFinder.this.jcmdOKActionPerformed(evt);
          }
        });
    this.jPanel1.add(this.jcmdOK);
    this.jcmdCancel.setIcon(new ImageIcon(getClass().getResource("/com/openbravo/images/button_cancel.png")));
    this.jcmdCancel.setText(AppLocal.getIntString("Button.Cancel"));
    this.jcmdCancel.setFocusPainted(false);
    this.jcmdCancel.setFocusable(false);
    this.jcmdCancel.setMargin(new Insets(8, 16, 8, 16));
    this.jcmdCancel.setRequestFocusEnabled(false);
    this.jcmdCancel.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            JTicketsFinder.this.jcmdCancelActionPerformed(evt);
          }
        });
    this.jPanel1.add(this.jcmdCancel);
    this.jPanel8.add(this.jPanel1, "After");
    this.jPanel3.add(this.jPanel8, "South");
    getContentPane().add(this.jPanel3, "Center");
    this.jPanel2.setPreferredSize(new Dimension(200, 250));
    this.jPanel2.setLayout(new BorderLayout());
    this.jPanel2.add((Component)this.m_jKeys, "North");
    getContentPane().add(this.jPanel2, "After");
    setSize(new Dimension(812, 684));
    setLocationRelativeTo((Component)null);
  }
  
  private void jcmdOKActionPerformed(ActionEvent evt) {
    selectedTicket = (FindTicketsInfo)jListTickets.getSelectedValue();
    dispose();
  }
  
  private void jcmdCancelActionPerformed(ActionEvent evt) {
    dispose();
  }
  
  private void jButton3ActionPerformed(ActionEvent evt) {
    executeSearch();
  }
  
  private void jListTicketsValueChanged(ListSelectionEvent evt) {
    this.jcmdOK.setEnabled((this.jListTickets.getSelectedValue() != null));
  }
  
  private void jListTicketsMouseClicked(MouseEvent evt) {
    if (evt.getClickCount() == 2) {
      selectedTicket = (FindTicketsInfo)jListTickets.getSelectedValue();
      dispose();
    } 
  }
  
  private void jButton1ActionPerformed(ActionEvent evt) {
    defaultValues();
  }
  
  private void btnDateStartActionPerformed(ActionEvent evt) {
      Date date;
    try {
      date = (Date)Formats.TIMESTAMP.parseValue(this.jTxtStartDate.getText());
    } catch (BasicException e) {
      date = null;
    } 
    date = JCalendarDialog.showCalendarTimeHours(this, date);
    if (date != null)
      this.jTxtStartDate.setText(Formats.TIMESTAMP.formatValue(date)); 
  }
  
  private void btnDateEndActionPerformed(ActionEvent evt) {
      Date date;
    try {
      date = (Date)Formats.TIMESTAMP.parseValue(this.jTxtEndDate.getText());
    } catch (BasicException e) {
      date = null;
    } 
    date = JCalendarDialog.showCalendarTimeHours(this, date);
    if (date != null)
      this.jTxtEndDate.setText(Formats.TIMESTAMP.formatValue(date)); 
  }
  
  private void btnCustomerActionPerformed(ActionEvent evt) {
    JCustomerFinder finder = JCustomerFinder.getCustomerFinder(this, this.dlCustomers, this.location);
    finder.search(null);
    finder.setVisible(true);
    try {
      this.jtxtCustomer.setText((finder.getSelectedCustomer() == null) ? null : this.dlSales
          
          .loadCustomerExt(finder.getSelectedCustomer().getId()).toString());
    } catch (BasicException e) {
      MessageInf msg = new MessageInf(-33554432, AppLocal.getIntString("message.cannotfindcustomer"), e);
      msg.show(this);
    } 
  }
}
