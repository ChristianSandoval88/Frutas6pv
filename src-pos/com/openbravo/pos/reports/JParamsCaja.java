//    Openbravo POS is a point of sales application designed for touch screens.
//    Copyright (C) 2007-2009 Openbravo, S.L.
//    http://www.openbravo.com/product/pos
//
//    This file is part of Openbravo POS.
//
//    Openbravo POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Openbravo POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Openbravo POS.  If not, see <http://www.gnu.org/licenses/>.

package com.openbravo.pos.reports;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.Datas;
import com.openbravo.data.loader.QBFCompareEnum;
import com.openbravo.data.loader.SerializerWrite;
import com.openbravo.data.loader.SerializerWriteBasic;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import java.awt.Component;
import java.awt.event.ActionListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.LayoutStyle;

/**
 * Filtro de reportes por la caja donde se cobro el ticket (TICKETS.ACTIVECASH).
 * Lista desplegable de solo lectura: no permite escribir texto libre.
 * La primera opcion (vacia) equivale a "todas las cajas".
 */
public class JParamsCaja extends javax.swing.JPanel implements ReportEditorCreator {

    // Valores tal como se guardan en TICKETS.ACTIVECASH (machine.hostname de cada caja)
    private static final String[] CAJAS = {
        "CAJA1", "CAJA2", "CAJA3", "CAJA4", "CAJA5", "CAJA6", "CAJA7", "CAJA8"
    };

    private final DefaultComboBoxModel<String> m_CajasModel;
    private final JComboBox<String> m_jCaja;

    public JParamsCaja() {
        m_CajasModel = new DefaultComboBoxModel<>();
        m_CajasModel.addElement(null); // Sin filtro: todas las cajas
        for (String caja : CAJAS) {
            m_CajasModel.addElement(caja);
        }

        m_jCaja = new JComboBox<>(m_CajasModel);
        m_jCaja.setEditable(false);

        JLabel lblCaja = new JLabel(AppLocal.getIntString("label.caja"));
        setBorder(javax.swing.BorderFactory.createTitledBorder(AppLocal.getIntString("label.bycaja")));

        // Mismo acomodo que JParamsLocation
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCaja, GroupLayout.PREFERRED_SIZE, 110, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(m_jCaja, GroupLayout.PREFERRED_SIZE, 220, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCaja)
                    .addComponent(m_jCaja, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );
    }

    public void init(AppView app) {
    }

    public void activate() throws BasicException {
        m_jCaja.setSelectedIndex(0);
    }

    public SerializerWrite getSerializerWrite() {
        return new SerializerWriteBasic(new Datas[] {Datas.OBJECT, Datas.STRING});
    }

    public Component getComponent() {
        return this;
    }

    public void addActionListener(ActionListener l) {
        m_jCaja.addActionListener(l);
    }

    public void removeActionListener(ActionListener l) {
        m_jCaja.removeActionListener(l);
    }

    public Object createValue() throws BasicException {
        String caja = (String) m_jCaja.getSelectedItem();
        return new Object[] {
            caja == null ? QBFCompareEnum.COMP_NONE : QBFCompareEnum.COMP_EQUALS,
            caja
        };
    }
}
