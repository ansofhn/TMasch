/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Form;
import Koneksi.koneksi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import tmasch.UserSession;
/**
 *
 * @author izzan.zubair
 */
public class FormTicketList extends javax.swing.JPanel {
    private int selectedTicketId = -1;
    private int userRoleId = tmasch.UserSession.getRoleId(); 
    private int userLoggedInId = tmasch.UserSession.getId();

    /**
     * Creates new form FormTicketList
     */
    public FormTicketList() {
        initComponents();
        loadStatistics();
        loadTicketTable();
    }
    
        private void loadTicketTable() {
    DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    model.addColumn("Ticket ID");
    model.addColumn("Subject");
    model.addColumn("Category");
    model.addColumn("Priority");
    model.addColumn("Status");
    model.addColumn("Assigned To");
    model.addColumn("Resolution Note");

    // 1. Modifikasi query dasar (Tambahkan penampung WHERE dinamis)
    String sql = "SELECT t.id, t.title, c.name AS nama_kategori, p.name AS nama_prioritas, s.name AS nama_status, " +
                 "u.full_name AS nama_staf, t.resolution_note " +
                 "FROM tickets t " +
                 "JOIN categories c ON t.category_id = c.id " +
                 "LEFT JOIN priorities p ON t.priority_id = p.id " +
                 "JOIN statuses s ON t.status_id = s.id " +
                 "LEFT JOIN users u ON t.assigned_to = u.id ";

    // JIKA GURU (role_id = 2), batasi hanya menampilkan tiket milik dia sendiri
    if (userRoleId == 3) { // Siswa
        sql += "WHERE t.created_by = ? ";
    } else if (userRoleId == 2) { // Guru
        sql += "WHERE (t.created_by = ? OR t.assigned_to = ?) ";
    }
    // Jika 1 (Admin) tidak ada WHERE, jadi tampil semua
    
    sql += "ORDER BY t.id DESC";

    try {
        Connection conn = new koneksi().connect(); 
        PreparedStatement ps = conn.prepareStatement(sql);
        
        // --- BAGIAN YANG DIUBAH: Set Parameter ---
        if (userRoleId == 3) {
            ps.setInt(1, userLoggedInId);
        } else if (userRoleId == 2) {
            ps.setInt(1, userLoggedInId);
            ps.setInt(2, userLoggedInId);
        }
        
        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("nama_kategori"),
                rs.getString("nama_prioritas") != null ? rs.getString("nama_prioritas") : "Not Set",
                rs.getString("nama_status"),
                rs.getString("nama_staf") != null ? rs.getString("nama_staf") : "Not Assigned",
                rs.getString("resolution_note") != null ? rs.getString("resolution_note") : "-"
            });
        }
        tblTickets.setModel(model);
        rs.close();
        ps.close();
        conn.close(); // Tambahkan close connection agar tidak terjadi memory leak database
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal memuat tabel tiket: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
private void searchTickets(String keyword) {
    DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    model.addColumn("Ticket ID");
    model.addColumn("Subject");
    model.addColumn("Category");
    model.addColumn("Priority");
    model.addColumn("Status");
    model.addColumn("Assigned To");
    model.addColumn("Resolution Note");

    String sql = "SELECT t.id, t.title, c.name AS nama_kategori, p.name AS nama_prioritas, s.name AS nama_status, " +
                 "u.full_name AS nama_staf, t.resolution_note " +
                 "FROM tickets t " +
                 "JOIN categories c ON t.category_id = c.id " +
                 "LEFT JOIN priorities p ON t.priority_id = p.id " +
                 "JOIN statuses s ON t.status_id = s.id " +
                 "LEFT JOIN users u ON t.assigned_to = u.id " +
                 "WHERE (t.id LIKE ? OR t.title LIKE ?) ";

    // Tambahkan filter role jika guru
if (userRoleId == 3) {
        sql += "AND t.created_by = ? ";
    } else if (userRoleId == 2) {
        sql += "AND (t.created_by = ? OR t.assigned_to = ?) ";
    }
    
    sql += "ORDER BY t.id DESC";

    try {
        Connection conn = new koneksi().connect(); 
        PreparedStatement ps = conn.prepareStatement(sql);
        
        String searchPattern = "%" + keyword + "%";
        ps.setString(1, searchPattern);
        ps.setString(2, searchPattern);
        
        // --- BAGIAN YANG DIUBAH: Set Parameter ---
        if (userRoleId == 3) {
            ps.setInt(3, userLoggedInId);
        } else if (userRoleId == 2) {
            ps.setInt(3, userLoggedInId);
            ps.setInt(4, userLoggedInId);
        }
        
        ResultSet rs = ps.executeQuery();

        int rowCount = 0;
        while(rs.next()) {
            rowCount++;
            model.addRow(new Object[]{
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("nama_kategori"),
                rs.getString("nama_prioritas") != null ? rs.getString("nama_prioritas") : "Not Set",
                rs.getString("nama_status"),
                rs.getString("nama_staf") != null ? rs.getString("nama_staf") : "Not Assigned",
                rs.getString("resolution_note") != null ? rs.getString("resolution_note") : "-"
            });
        }
        
        tblTickets.setModel(model);
        
        if (rowCount == 0) {
            JOptionPane.showMessageDialog(this, "Tidak ditemukan tiket dengan kata kunci: " + keyword, "Info", JOptionPane.INFORMATION_MESSAGE);
        }
        
        rs.close();
        ps.close();
        conn.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal mencari tiket: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void sortTickets(String sortBy) {
    System.out.println("== SORT TICKETS DIPANGGIL ==");
    System.out.println("Sort By Parameter: " + sortBy);
    
    DefaultTableModel model = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    model.addColumn("Ticket ID");
    model.addColumn("Subject");
    model.addColumn("Category");
    model.addColumn("Priority");
    model.addColumn("Status");
    model.addColumn("Assigned To");
    model.addColumn("Resolution Note");

    String orderByClause = "";
    
    switch (sortBy) {
        case "Status":
            orderByClause = "ORDER BY t.status_id ASC, t.id DESC";
            System.out.println("Sort by Status dipilih");
            break;
        case "Priority":
            orderByClause = "ORDER BY CASE " +
                           "WHEN p.name = 'High' THEN 1 " +
                           "WHEN p.name = 'Medium' THEN 2 " +
                           "WHEN p.name = 'Low' THEN 3 " +
                           "ELSE 4 END, t.id DESC";
            System.out.println("Sort by Priority dipilih");
            break;
        case "Category":
            orderByClause = "ORDER BY c.name ASC, t.id DESC";
            System.out.println("Sort by Category dipilih");
            break;
        default:
            orderByClause = "ORDER BY t.id DESC";
            System.out.println("Default sorting dipilih");
    }

    String sql = "SELECT t.id, t.title, c.name AS nama_kategori, p.name AS nama_prioritas, s.name AS nama_status, " +
                 "u.full_name AS nama_staf, t.resolution_note, t.status_id " + // ✅ TAMBAHKAN status_id untuk debug
                 "FROM tickets t " +
                 "JOIN categories c ON t.category_id = c.id " +
                 "LEFT JOIN priorities p ON t.priority_id = p.id " +
                 "JOIN statuses s ON t.status_id = s.id " +
                 "LEFT JOIN users u ON t.assigned_to = u.id ";

    if (userRoleId == 3) {
        sql += "WHERE t.created_by = ? ";
    } else if (userRoleId == 2) {
        sql += "WHERE (t.created_by = ? OR t.assigned_to = ?) ";
    }
    
    sql += orderByClause;

    try {
        Connection conn = new koneksi().connect(); 
        PreparedStatement ps = conn.prepareStatement(sql);
        
        // --- BAGIAN YANG DIUBAH: Set Parameter ---
        if (userRoleId == 3) {
            ps.setInt(1, userLoggedInId);
        } else if (userRoleId == 2) {
            ps.setInt(1, userLoggedInId);
            ps.setInt(2, userLoggedInId);
        }
        
        ResultSet rs = ps.executeQuery();

        int rowNum = 0;
        while(rs.next()) {
            rowNum++;
            int ticketId = rs.getInt("id");
            int statusId = rs.getInt("status_id");
            String statusName = rs.getString("nama_status");
            
            // ✅ DEBUG: Print urutan data
            System.out.println("Row " + rowNum + " -> Ticket ID: " + ticketId + 
                             ", Status ID: " + statusId + ", Status Name: " + statusName);
            
            model.addRow(new Object[]{
                ticketId,
                rs.getString("title"),
                rs.getString("nama_kategori"),
                rs.getString("nama_prioritas") != null ? rs.getString("nama_prioritas") : "Not Set",
                statusName,
                rs.getString("nama_staf") != null ? rs.getString("nama_staf") : "Not Assigned",
                rs.getString("resolution_note") != null ? rs.getString("resolution_note") : "-"
            });
        }
        
        System.out.println("Total rows loaded: " + rowNum);
        
        tblTickets.setModel(model);
        rs.close();
        ps.close();
        conn.close();
    } catch (SQLException e) {
        System.err.println("SQL Error: " + e.getMessage());
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Gagal mengurutkan tiket: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void loadStatistics() {
        int openCount = 0;
        int inProgressCount = 0;
        int closedCount = 0;

        // JOIN dengan statuses agar bisa membaca string statusnya
        String sql = "SELECT s.name AS status_name, COUNT(t.id) AS jumlah " +
                     "FROM tickets t " +
                     "JOIN statuses s ON t.status_id = s.id ";
        
        if (userRoleId == 3) {
            sql += "WHERE t.created_by = ? ";
        } else if (userRoleId == 2) {
            sql += "WHERE (t.created_by = ? OR t.assigned_to = ?) ";
        } 
        
        sql += "GROUP BY s.name";

        try {
            Connection conn = new koneksi().connect(); // Ditambahkan koneksi
            PreparedStatement ps = conn.prepareStatement(sql);
            
            if (userRoleId == 3) {
                ps.setInt(1, userLoggedInId);
            } else if (userRoleId == 2) {
                ps.setInt(1, userLoggedInId);
                ps.setInt(2, userLoggedInId);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String status = rs.getString("status_name");
                int jumlah = rs.getInt("jumlah");

                if (status.equalsIgnoreCase("Open")) {
                    openCount = jumlah;
                } else if (status.equalsIgnoreCase("In-Progress") || status.equalsIgnoreCase("In Progress")) {
                    inProgressCount = jumlah;
                } else if (status.equalsIgnoreCase("Closed")) {
                    closedCount = jumlah;
                }
            }

            lblOpen.setText("Open: " + openCount);
            lblInProgress.setText("In-Progress: " + inProgressCount);
            lblClosed.setText("Closed: " + closedCount);

            rs.close();
            ps.close();
            conn.close(); // Ditambahkan close
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat statistik: " + e.getMessage());
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelTableData = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTickets = new javax.swing.JTable();
        btnTicketDetails = new javax.swing.JButton();
        btnSearch = new javax.swing.JButton();
        txtSearch = new javax.swing.JTextField();
        cmbSortBy = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        btnReset = new javax.swing.JButton();
        lblOpen = new javax.swing.JLabel();
        lblInProgress = new javax.swing.JLabel();
        lblClosed = new javax.swing.JLabel();

        jPanelTableData.setBorder(javax.swing.BorderFactory.createTitledBorder("Data Ticket"));

        tblTickets.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6"
            }
        ));
        tblTickets.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTicketsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblTickets);

        btnTicketDetails.setText("Ticket Details");
        btnTicketDetails.addActionListener(this::btnTicketDetailsActionPerformed);

        btnSearch.setText("Search");
        btnSearch.addActionListener(this::btnSearchActionPerformed);

        txtSearch.setToolTipText("");

        cmbSortBy.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Default", "Status", "Priority", "Category" }));
        cmbSortBy.addActionListener(this::cmbSortByActionPerformed);

        jLabel9.setText("Sort By :");

        btnReset.setText("Reset");
        btnReset.addActionListener(this::btnResetActionPerformed);

        javax.swing.GroupLayout jPanelTableDataLayout = new javax.swing.GroupLayout(jPanelTableData);
        jPanelTableData.setLayout(jPanelTableDataLayout);
        jPanelTableDataLayout.setHorizontalGroup(
            jPanelTableDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTableDataLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTableDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanelTableDataLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSearch)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbSortBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 76, Short.MAX_VALUE)
                        .addComponent(btnTicketDetails)))
                .addContainerGap())
        );
        jPanelTableDataLayout.setVerticalGroup(
            jPanelTableDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTableDataLayout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addGroup(jPanelTableDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnTicketDetails)
                    .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanelTableDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbSortBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel9)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        lblOpen.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblOpen.setText("Open: ");

        lblInProgress.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblInProgress.setText("In progress:");

        lblClosed.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblClosed.setText("Closed:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jPanelTableData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(82, 82, 82)
                .addComponent(lblOpen)
                .addGap(139, 139, 139)
                .addComponent(lblInProgress)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblClosed)
                .addGap(150, 150, 150))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblOpen)
                    .addComponent(lblInProgress)
                    .addComponent(lblClosed))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 94, Short.MAX_VALUE)
                .addComponent(jPanelTableData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void tblTicketsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTicketsMouseClicked
        int row = tblTickets.getSelectedRow();
        if (row != -1) {
            selectedTicketId = Integer.parseInt(tblTickets.getValueAt(row, 0).toString());
        }
    }//GEN-LAST:event_tblTicketsMouseClicked

    private void btnTicketDetailsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTicketDetailsActionPerformed
        if (selectedTicketId == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih baris tiket dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "SELECT t.id, t.title, t.description, c.name AS kategori, p.name AS prioritas, " +
        "s.name AS status, u1.full_name AS pembuat, u2.full_name AS ditugaskan, " +
        "t.resolution_note, t.created_at, t.resolved_at " +
        "FROM tickets t " +
        "JOIN categories c ON t.category_id = c.id " +
        "LEFT JOIN priorities p ON t.priority_id = p.id " +
        "JOIN statuses s ON t.status_id = s.id " +
        "JOIN users u1 ON t.created_by = u1.id " +
        "LEFT JOIN users u2 ON t.assigned_to = u2.id " +
        "WHERE t.id = ?";

        try {
            Connection conn = new koneksi().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, selectedTicketId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                StringBuilder sb = new StringBuilder();
                sb.append("==================================================\n");
                sb.append("            TICKET INFORMATION DETAIL             \n");
                sb.append("==================================================\n\n");
                sb.append(" Ticket ID       : ").append(rs.getInt("id")).append("\n");
                sb.append(" Subject         : ").append(rs.getString("title")).append("\n");
                sb.append(" Category        : ").append(rs.getString("kategori")).append("\n");
                sb.append(" Priority        : ").append(rs.getString("prioritas") != null ? rs.getString("prioritas") : "Belum Diatur").append("\n");
                sb.append(" Status          : ").append(rs.getString("status")).append("\n\n");
                sb.append("--------------------------------------------------\n");
                sb.append(" Created By      : ").append(rs.getString("pembuat")).append("\n");
                sb.append(" Created Time    : ").append(rs.getTimestamp("created_at")).append("\n");
                sb.append(" Assigned To     : ").append(rs.getString("ditugaskan") != null ? rs.getString("ditugaskan") : "Belum Ada").append("\n\n");
                sb.append("--------------------------------------------------\n");
                sb.append(" Description :\n ");
                sb.append(rs.getString("description")).append("\n\n");
                sb.append("--------------------------------------------------\n");
                sb.append(" Resolution Note :\n ");
                sb.append(rs.getString("resolution_note") != null ? rs.getString("resolution_note") : "-").append("\n\n");
                sb.append(" Closed At       : ").append(rs.getTimestamp("resolved_at") != null ? rs.getTimestamp("resolved_at") : "-").append("\n");
                sb.append("==================================================");

                javax.swing.JTextArea textArea = new javax.swing.JTextArea(sb.toString());
                textArea.setEditable(false);
                textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));

                javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(textArea);
                scrollPane.setPreferredSize(new java.awt.Dimension(500, 400));

                JOptionPane.showMessageDialog(this, scrollPane, "Detail Tiket #" + selectedTicketId, JOptionPane.INFORMATION_MESSAGE);
            }
            rs.close();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat detail dari database:\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnTicketDetailsActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        String keyword = txtSearch.getText().trim();

        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Masukkan kata kunci pencarian (ID atau Subject)!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        searchTickets(keyword);
    }//GEN-LAST:event_btnSearchActionPerformed

    private void cmbSortByActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbSortByActionPerformed
        if (cmbSortBy.getSelectedIndex() == 0) {
            return; // Tidak melakukan apa-apa jika pilih "-- Sort By --"
        }

        String sortOption = cmbSortBy.getSelectedItem().toString();
        sortTickets(sortOption);
    }//GEN-LAST:event_cmbSortByActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        txtSearch.setText("");
        cmbSortBy.setSelectedIndex(0);
        loadTicketTable();
        loadStatistics();
    }//GEN-LAST:event_btnResetActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnTicketDetails;
    private javax.swing.JComboBox<String> cmbSortBy;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanelTableData;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblClosed;
    private javax.swing.JLabel lblInProgress;
    private javax.swing.JLabel lblOpen;
    private javax.swing.JTable tblTickets;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
