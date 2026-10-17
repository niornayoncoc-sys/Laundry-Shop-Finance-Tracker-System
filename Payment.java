/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Database;

import javax.swing.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;


public class Payment extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Payment.class.getName());

    /**
     * Creates new form Payment
     */
    public Payment() {
        initComponents();
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        
         
    btnPay.addActionListener(e -> recordPayment());
    
    tblPayments.setModel(new DefaultTableModel(
        new Object[][]{},
        new String[]{
            "Payment ID",
            "Order ID",
            "Amount Paid",
            "Payment Method",
            "Payment Date"
        }
    ));

     loadPayments();
    }
    
private void recordPayment() {
    try {
        int id = Integer.parseInt(txtOrderID.getText().trim());
        double paid = Double.parseDouble(txtAmount.getText().trim());

        if (id <= 0 || !Double.isFinite(paid) || paid <= 0) {
            JOptionPane.showMessageDialog(
                this, "Enter a valid order ID and amount.");
            return;
        }

        String selectedMethod = (String) cmbMethod.getSelectedItem();

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);

            try {
                double total;

                try (PreparedStatement ps = con.prepareStatement(
                    "SELECT total_price FROM orders WHERE order_id = Customer FOR UPDATE")) {

                    ps.setInt(1, id);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Order not found.");
                        }
                        total = rs.getDouble("total_price");
                    }
                }

                double previous;

                try (PreparedStatement ps = con.prepareStatement(
                    "SELECT COALESCE(SUM(amount_paid), 0) "
                    + "FROM payments WHERE order_id = ?")) {

                    ps.setInt(1, id);

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        previous = rs.getDouble(1);
                    }
                }

                double balance = total - previous;

                if (paid > balance + 0.000001) {
                    throw new SQLException(
                        "Remaining balance: ₱"
                        + String.format("%.2f", Math.max(0, balance)));
                }

                try (PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO payments "
                    + "(order_id, amount_paid, payment_method) "
                    + "VALUES (?, ?, ?)")) {

                    ps.setInt(1, id);
                    ps.setDouble(2, paid);
                    ps.setString(3, selectedMethod);
                    ps.executeUpdate();
                }

                con.commit();
                loadPayments();

                JOptionPane.showMessageDialog(this,
                    "Payment recorded successfully!\n"
                    + "Remaining balance: ₱"
                    + String.format("%.2f", balance - paid));

                txtOrderID.setText("");
                txtAmount.setText("");

            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        }

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(
            this, "Enter a valid order ID and amount.");
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(
            this, ex.getMessage(),
            "Payment Error", JOptionPane.ERROR_MESSAGE);
    }
}


private void loadPayments() {

    DefaultTableModel model =
        (DefaultTableModel) tblPayments.getModel();

    model.setRowCount(0);

    String sql = "SELECT payment_id, order_id, amount_paid, "
               + "payment_method, payment_date "
               + "FROM payments ORDER BY payment_id DESC";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("payment_id"),
                rs.getInt("order_id"),
                rs.getDouble("amount_paid"),
                rs.getString("payment_method"),
                rs.getTimestamp("payment_date")
            });
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(
            this,
            "Unable to load payments: " + ex.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}

    

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        btnTracking1 = new javax.swing.JButton();
        btnReports1 = new javax.swing.JButton();
        btnOrder = new javax.swing.JButton();
        btnCustomer2 = new javax.swing.JButton();
        btnCustomer = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        txtOrderID = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtAmount = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        cmbMethod = new javax.swing.JComboBox<>();
        btnPay = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPayments = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel2.setBackground(new java.awt.Color(0, 102, 153));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/laundry-shop (1).png"))); // NOI18N

        jLabel3.setForeground(new java.awt.Color(204, 204, 204));
        jLabel3.setText("FINANCE TRACKER SYSTEM . Payments");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(204, 204, 204));
        jLabel2.setText("LAUNDRY SHOP");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addContainerGap(34, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(0, 102, 153));

        jPanel5.setBackground(new java.awt.Color(0, 102, 153));

        btnTracking1.setBackground(new java.awt.Color(0, 102, 153));
        btnTracking1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnTracking1.setText("Tracking");
        btnTracking1.addActionListener(this::btnTracking1ActionPerformed);

        btnReports1.setBackground(new java.awt.Color(0, 102, 153));
        btnReports1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnReports1.setText("Reports");
        btnReports1.addActionListener(this::btnReports1ActionPerformed);

        btnOrder.setBackground(new java.awt.Color(0, 102, 153));
        btnOrder.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnOrder.setText("Orders");
        btnOrder.addActionListener(this::btnOrderActionPerformed);

        btnCustomer2.setBackground(new java.awt.Color(0, 102, 153));
        btnCustomer2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCustomer2.setText("Dashboard");
        btnCustomer2.addActionListener(this::btnCustomer2ActionPerformed);

        btnCustomer.setBackground(new java.awt.Color(0, 102, 153));
        btnCustomer.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCustomer.setText("Customer");
        btnCustomer.addActionListener(this::btnCustomerActionPerformed);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 102, 153));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText("Payments");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnTracking1, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                        .addComponent(btnReports1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnCustomer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCustomer2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                        .addComponent(btnOrder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCustomer2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54)
                .addComponent(btnCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54)
                .addComponent(btnOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(49, 49, 49)
                .addComponent(btnTracking1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(47, 47, 47)
                .addComponent(btnReports1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(69, 69, 69))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 531, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(0, 102, 153));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setText("Order ID :");

        txtOrderID.addActionListener(this::txtOrderIDActionPerformed);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setText("Amount Paid :");

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel7.setText("Payment Method :");

        cmbMethod.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cash 💵", "GCash 📱", "Bank Transfer 🏦", "Maya 📱" }));
        cmbMethod.addActionListener(this::cmbMethodActionPerformed);

        btnPay.setBackground(new java.awt.Color(0, 102, 153));
        btnPay.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnPay.setForeground(new java.awt.Color(255, 255, 255));
        btnPay.setText("Pay");

        tblPayments.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblPayments);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnPay, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(251, 251, 251))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(95, 95, 95)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel4)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtOrderID)
                            .addComponent(txtAmount)
                            .addComponent(cmbMethod, 0, 364, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 764, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtOrderID, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmbMethod, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE))
                .addGap(41, 41, 41)
                .addComponent(btnPay, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.ipady = 19;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(19, 17, 25, 36);
        jPanel4.add(jPanel1, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 850, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCustomer2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCustomer2ActionPerformed
        DashboardForm dashboardform = new DashboardForm();
        dashboardform.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnCustomer2ActionPerformed

    private void btnCustomerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCustomerActionPerformed
      Customer customer = new Customer();
      customer.setVisible(true);
      this.dispose();
    }//GEN-LAST:event_btnCustomerActionPerformed

    private void btnOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOrderActionPerformed
        Order order = new Order();
        order.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnOrderActionPerformed

    private void btnTracking1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTracking1ActionPerformed
       Tracking tracking = new Tracking();
       tracking.setVisible(true);
       this.dispose();
    }//GEN-LAST:event_btnTracking1ActionPerformed

    private void btnReports1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReports1ActionPerformed
        Report report = new Report();
        report.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnReports1ActionPerformed

    private void txtOrderIDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtOrderIDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtOrderIDActionPerformed

    private void cmbMethodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbMethodActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbMethodActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Payment().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCustomer;
    private javax.swing.JButton btnCustomer2;
    private javax.swing.JButton btnOrder;
    private javax.swing.JButton btnPay;
    private javax.swing.JButton btnReports1;
    private javax.swing.JButton btnTracking1;
    private javax.swing.JComboBox<String> cmbMethod;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblPayments;
    private javax.swing.JTextField txtAmount;
    private javax.swing.JTextField txtOrderID;
    // End of variables declaration//GEN-END:variables
}
