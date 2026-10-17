/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Database;
import javax.swing.*;
import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.table.DefaultTableModel;


public class Order extends javax.swing.JFrame {
    
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Order.class.getName());

    /**
     * Creates new form Order
     */
    public Order() {
        initComponents();
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        
         setTitle("Laundry Shop - Create Order");
         this.pack();
         setLocationRelativeTo(null);

    connectButtons();
    loadChoices();
    
    
    }
    
    private final Map<String, Integer> customerIds = new LinkedHashMap<>();
   private final Map<String, Integer> serviceIds = new LinkedHashMap<>();
   private final Map<String, Double> prices = new LinkedHashMap<>();

private void connectButtons() {
    btnSave.addActionListener(e -> saveOrder());
     btnRefresh.addActionListener(e -> {
        loadChoices();
        loadOrders();
    });
}

private void loadChoices() {
    cmbCustomer.removeAllItems();
    cmbServices.removeAllItems();

    customerIds.clear();
    serviceIds.clear();
    prices.clear();

    try (Connection con = DBConnection.getConnection();
         Statement st = con.createStatement();
         ResultSet rs = st.executeQuery(
             "SELECT customer_id, full_name FROM customers")) {

        while (rs.next()) {
            String label = rs.getInt("customer_id")
                    + " - " + rs.getString("full_name");

            cmbCustomer.addItem(label);
            customerIds.put(label, rs.getInt("customer_id"));
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage());
    }

    try (Connection con = DBConnection.getConnection();
         Statement st = con.createStatement();
         ResultSet rs = st.executeQuery(
             "SELECT service_id, service_name, price_per_kg FROM services")) {

        while (rs.next()) {
            String label = rs.getInt("service_id")
                    + " - " + rs.getString("service_name");

            cmbServices.addItem(label);
            serviceIds.put(label, rs.getInt("service_id"));
            prices.put(label, rs.getDouble("price_per_kg"));
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage());
    }
}

private void saveOrder() {
    String cust = (String) cmbCustomer.getSelectedItem();
    String serv = (String) cmbServices.getSelectedItem();

    if (cust == null || serv == null) {
        JOptionPane.showMessageDialog(
            this, "Add a customer and service first.");
        return;
    }

    try {
        double kg = Double.parseDouble(txtWeight.getText().trim());

        if (!Double.isFinite(kg) || kg <= 0) {
            throw new NumberFormatException();
        }

        double total = kg * prices.get(serv);
        String number = "LS-" + System.currentTimeMillis();

        String sql = "INSERT INTO orders "
            + "(order_number, customer_id, service_id, weight_kg, "
            + "total_price, pickup_date, status) "
            + "VALUES (?, ?, ?, ?, ?, NULL, 'Pending')";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, number);
            ps.setInt(2, customerIds.get(cust));
            ps.setInt(3, serviceIds.get(serv));
            ps.setDouble(4, kg);
            ps.setDouble(5, total);
            ps.executeUpdate();
        }

        JOptionPane.showMessageDialog(this,
            "Order: " + number
            + "\nWeight: " + kg + " kg"
            + "\nTotal: ₱" + String.format("%.2f", total));

        txtWeight.setText("1");
          loadChoices();
           loadOrders();

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(
            this, "Enter a valid weight greater than zero.");
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(
            this, ex.getMessage(), "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }
}


private void loadOrders() {

    javax.swing.table.DefaultTableModel model =
        new javax.swing.table.DefaultTableModel(
            new Object[][]{},
            new String[]{
                "Order ID",
                "Order Number",
                "Customer",
                "Service",
                "Weight (kg)",
                "Total Price",
                "Status"
            }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

    tblOrders.setModel(model);

    String sql =
        "SELECT o.order_id, o.order_number, c.full_name, "
        + "s.service_name, o.weight_kg, o.total_price, o.status "
        + "FROM orders o "
        + "JOIN customers c ON o.customer_id = c.customer_id "
        + "JOIN services s ON o.service_id = s.service_id "
        + "ORDER BY o.order_id DESC";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("order_id"),
                rs.getString("order_number"),
                rs.getString("full_name"),
                rs.getString("service_name"),
                rs.getDouble("weight_kg"),
                String.format("₱%.2f", rs.getDouble("total_price")),
                rs.getString("status")
            });
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(
            this,
            "Unable to load orders: " + ex.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}

private void setupOrdersTable() {

    tblOrders = new javax.swing.JTable();

    javax.swing.JScrollPane scrollPane =
        new javax.swing.JScrollPane(tblOrders);

    scrollPane.setPreferredSize(new java.awt.Dimension(650, 400));

    tblOrders.setRowHeight(28);
    tblOrders.setFont(new java.awt.Font("Segoe UI", 0, 13));

    tblOrders.getTableHeader().setFont(
        new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13)
    );

    java.awt.GridBagConstraints gbc =
        new java.awt.GridBagConstraints();

    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    gbc.fill = java.awt.GridBagConstraints.BOTH;
    gbc.insets = new java.awt.Insets(10, 10, 10, 10);

    jPanel2.add(scrollPane, gbc);

    jPanel2.revalidate();
    jPanel2.repaint();
}



   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        btnTracking1 = new javax.swing.JButton();
        btnReports1 = new javax.swing.JButton();
        btnPayments1 = new javax.swing.JButton();
        btnCustomer2 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        btnCustomer = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        cmbCustomer = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        cmbServices = new javax.swing.JComboBox<>();
        asd = new javax.swing.JLabel();
        txtWeight = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        btnSave = new javax.swing.JButton();
        btnRefresh = new javax.swing.JButton();
        btnClear = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblOrders = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 102, 153));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/image/laundry-shop (1).png"))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(204, 204, 204));
        jLabel2.setText("LAUNDRY SHOP");

        jLabel3.setForeground(new java.awt.Color(204, 204, 204));
        jLabel3.setText("FINANCE TRACKER SYSTEM . Orders");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        btnPayments1.setBackground(new java.awt.Color(0, 102, 153));
        btnPayments1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnPayments1.setText("Payment");
        btnPayments1.addActionListener(this::btnPayments1ActionPerformed);

        btnCustomer2.setBackground(new java.awt.Color(0, 102, 153));
        btnCustomer2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCustomer2.setText("Dashboard");
        btnCustomer2.addActionListener(this::btnCustomer2ActionPerformed);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 102, 153));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText("Orders");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 153, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addContainerGap())
        );

        btnCustomer.setBackground(new java.awt.Color(0, 102, 153));
        btnCustomer.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnCustomer.setText("Customer");
        btnCustomer.addActionListener(this::btnCustomerActionPerformed);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnPayments1, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                        .addComponent(btnTracking1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnReports1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnCustomer, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCustomer2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)))
                .addGap(21, 21, 21))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(0, 21, Short.MAX_VALUE)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnCustomer2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54)
                .addComponent(btnCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(btnPayments1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52)
                .addComponent(btnTracking1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(btnReports1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52))
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(26, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 532, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42))
        );

        jPanel2.setBackground(new java.awt.Color(0, 102, 153));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jPanel4.setRequestFocusEnabled(false);
        jPanel4.setVerifyInputWhenFocusTarget(false);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 102, 153));
        jLabel4.setText("Customer :");

        cmbCustomer.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(0, 102, 153));
        jLabel6.setText("Services :");

        cmbServices.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        asd.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        asd.setForeground(new java.awt.Color(0, 102, 153));
        asd.setText("Weight(kg) :");

        txtWeight.setText("jTextField1");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 102, 153));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 102, 153));
        jLabel9.setText("Order Status :");

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pending", "Processing", "Completed" }));

        btnSave.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnSave.setForeground(new java.awt.Color(0, 102, 153));
        btnSave.setText("ADD");
        btnSave.addActionListener(this::btnSaveActionPerformed);

        btnRefresh.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnRefresh.setForeground(new java.awt.Color(0, 102, 153));
        btnRefresh.setText("Refresh");

        btnClear.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnClear.setForeground(new java.awt.Color(0, 102, 153));
        btnClear.setText("Clear");

        tblOrders.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Order ID", "Order Number", "Customer", "Service", "Weight (kg)", "Total Price", "Status"
            }
        ));
        jScrollPane1.setViewportView(tblOrders);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(236, 236, 236)
                        .addComponent(jLabel4)
                        .addGap(6, 6, 6)
                        .addComponent(cmbCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(246, 246, 246)
                        .addComponent(jLabel6)
                        .addGap(6, 6, 6)
                        .addComponent(cmbServices, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(223, 223, 223)
                        .addComponent(asd)
                        .addGap(6, 6, 6)
                        .addComponent(txtWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(215, 215, 215)
                        .addComponent(jLabel9)
                        .addGap(6, 6, 6)
                        .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(84, 84, 84)
                        .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(106, 106, 106)
                        .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(110, 110, 110)
                        .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 799, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel8)
                .addGap(37, 37, 37)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(cmbCustomer, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(29, 29, 29)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(cmbServices, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(27, 27, 27)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(asd, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(txtWeight, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(35, 35, 35)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(78, 78, 78)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSave, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRefresh, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnClear, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 29;
        gridBagConstraints.ipady = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 6, 6);
        jPanel2.add(jPanel4, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 874, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
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

    private void btnPayments1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPayments1ActionPerformed
       Payment payments = new Payment();
       payments.setVisible(true);
       this.dispose();
    }//GEN-LAST:event_btnPayments1ActionPerformed

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

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSaveActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new Order().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel asd;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnCustomer;
    private javax.swing.JButton btnCustomer2;
    private javax.swing.JButton btnPayments1;
    private javax.swing.JButton btnRefresh;
    private javax.swing.JButton btnReports1;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnTracking1;
    private javax.swing.JComboBox<String> cmbCustomer;
    private javax.swing.JComboBox<String> cmbServices;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblOrders;
    private javax.swing.JTextField txtWeight;
    // End of variables declaration//GEN-END:variables
}
