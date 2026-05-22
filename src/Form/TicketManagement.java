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
 * @author Draugr
 */
public class TicketManagement extends javax.swing.JPanel {
    private int selectedTicketId = -1;
    private int userRoleId = tmasch.UserSession.getRoleId(); 
    private int userLoggedInId = tmasch.UserSession.getId();

    public TicketManagement(int roleId, int userId) {
        this.userRoleId = roleId;
        this.userLoggedInId = userId;
        
        initComponents();
        loadTicketTable();
        loadStaffCombo(); 
        loadPriorityCombo();
        aturHakAksesKomponen();
        
        txtTicketID.setEditable(false);
        txtCreatedBy.setEditable(false);
        txtRole.setEditable(false);
        txtCategory.setEditable(false);
    }
    
    private void aturHakAksesKomponen() {
        if (userRoleId == 1) {
            // Hak Akses ADMIN (role_id = 1)
            cmbAssignTo.setEnabled(true);
            cmbPriority.setEnabled(true);
            btnAssign.setEnabled(true);
            txtResolutionNote.setEnabled(true);
            btnCloseTicket.setEnabled(true);
        } else if (userRoleId == 2) {
            // Hak Akses GURU / STAFF (role_id = 2)
            cmbAssignTo.setEnabled(false);
            cmbPriority.setEnabled(false);
            btnAssign.setEnabled(false);
            txtResolutionNote.setEnabled(true);
            btnCloseTicket.setEnabled(true);
        } else {
            // Opsi Pengaman jika Siswa (role_id = 3) atau orang lain tersasar ke panel ini
            cmbAssignTo.setEnabled(false);
            cmbPriority.setEnabled(false);
            btnAssign.setEnabled(false);
            txtResolutionNote.setEnabled(false);
            btnCloseTicket.setEnabled(false);
        }
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
    if (userRoleId == 2) {
        sql += "WHERE t.assigned_to = ? " ;
    }
    
    sql += "ORDER BY t.id DESC";

    try {
        Connection conn = new koneksi().connect(); 
        PreparedStatement ps = conn.prepareStatement(sql);
        
        // 2. Set parameter jika yang login adalah GURU
        if (userRoleId == 2) {
            ps.setInt(1, userLoggedInId);
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
    private void loadStaffCombo() {
        cmbAssignTo.removeAllItems();
        cmbAssignTo.addItem("-- Pilih Guru/Staff --");
        
        String sql = "SELECT id, full_name FROM users WHERE role_id = 2";
        try {
            Connection conn = new koneksi().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while(rs.next()) {
                cmbAssignTo.addItem(rs.getInt("id") + " - " + rs.getString("full_name"));
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat daftar staf: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadPriorityCombo() {
    cmbPriority.removeAllItems();
    cmbPriority.addItem("-- Pilih Prioritas --");
    
    String sql = "SELECT id, name FROM priorities";
    try {
        Connection conn = new koneksi().connect();
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        
        while(rs.next()) {
            cmbPriority.addItem(rs.getInt("id") + " - " + rs.getString("name"));
        }
        rs.close();
        ps.close();
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal memuat daftar prioritas: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanelTableData = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTickets = new javax.swing.JTable();
        btnTicketDetails = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtTicketID = new javax.swing.JTextField();
        txtCreatedBy = new javax.swing.JTextField();
        txtRole = new javax.swing.JTextField();
        txtCategory = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        cmbAssignTo = new javax.swing.JComboBox<>();
        btnAssign = new javax.swing.JButton();
        cmbPriority = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtResolutionNote = new javax.swing.JTextArea();
        btnCloseTicket = new javax.swing.JButton();

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel1.setText("Ticket Management");

        jLabel2.setText("Ticket ID");

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

        javax.swing.GroupLayout jPanelTableDataLayout = new javax.swing.GroupLayout(jPanelTableData);
        jPanelTableData.setLayout(jPanelTableDataLayout);
        jPanelTableDataLayout.setHorizontalGroup(
            jPanelTableDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelTableDataLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanelTableDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanelTableDataLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnTicketDetails)))
                .addContainerGap())
        );
        jPanelTableDataLayout.setVerticalGroup(
            jPanelTableDataLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelTableDataLayout.createSequentialGroup()
                .addContainerGap(7, Short.MAX_VALUE)
                .addComponent(btnTicketDetails)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel3.setText("Created By");

        jLabel4.setText("Category");

        jLabel5.setText("Role");

        txtCreatedBy.addActionListener(this::txtCreatedByActionPerformed);

        txtCategory.addActionListener(this::txtCategoryActionPerformed);

        jLabel6.setText("Assigned To");

        jLabel7.setText("Priority");

        jLabel8.setText("Resolution Note");

        btnAssign.setText("Assign");
        btnAssign.addActionListener(this::btnAssignActionPerformed);

        txtResolutionNote.setColumns(20);
        txtResolutionNote.setRows(5);
        jScrollPane2.setViewportView(txtResolutionNote);

        btnCloseTicket.setText("Close Ticket");
        btnCloseTicket.addActionListener(this::btnCloseTicketActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(265, 265, 265)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanelTableData, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtTicketID)
                            .addComponent(txtCreatedBy)
                            .addComponent(txtRole)
                            .addComponent(txtCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 106, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel8))
                            .addComponent(btnAssign))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnCloseTicket)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(cmbAssignTo, javax.swing.GroupLayout.Alignment.LEADING, 0, 115, Short.MAX_VALUE)
                                .addComponent(cmbPriority, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addGap(18, 18, 18))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel1)
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtTicketID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(txtCreatedBy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtRole, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(txtCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(cmbAssignTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(cmbPriority, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(34, 34, 34))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAssign)
                    .addComponent(btnCloseTicket))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanelTableData, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtCreatedByActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCreatedByActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCreatedByActionPerformed

    private void txtCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCategoryActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCategoryActionPerformed

    private void btnAssignActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAssignActionPerformed
    // 1. Validasi apakah admin sudah memilih tiket dari tabel atau belum
        if (selectedTicketId == -1) {
            JOptionPane.showMessageDialog(this, "Silakan pilih baris tiket dari tabel terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Validasi ComboBox Staff / Guru
        if (cmbAssignTo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Silakan pilih Staff/Guru yang akan ditugaskan!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Validasi ComboBox Prioritas
        if (cmbPriority.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Silakan pilih tingkat Prioritas tiket!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 4. Mengambil ID Staff dari ComboBox (Format: "ID - Nama")
        String selectedStaff = cmbAssignTo.getSelectedItem().toString();
        int staffId = Integer.parseInt(selectedStaff.split(" - ")[0]);

        // 5. Mengambil ID Prioritas dari ComboBox (Format: "ID - Nama")
        String selectedPriority = cmbPriority.getSelectedItem().toString();
        int priorityId = Integer.parseInt(selectedPriority.split(" - ")[0]);

        // 6. Query UPDATE ke database: status_id diset ke 2 (In Progress)
        String sql = "UPDATE tickets SET assigned_to = ?, priority_id = ?, status_id = 2 WHERE id = ?";

        try {
            Connection conn = new koneksi().connect();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, staffId);
            ps.setInt(2, priorityId);
            ps.setInt(3, selectedTicketId);

            int hasil = ps.executeUpdate();
            if (hasil > 0) {
                JOptionPane.showMessageDialog(this, "Tiket berhasil ditugaskan! Status otomatis menjadi 'In Progress'.", "Sukses", JOptionPane.INFORMATION_MESSAGE);

                // 7. Refresh tabel agar status terupdate di layar GUI
                loadTicketTable(); 

                // Bersihkan textfield detail setelah assign selesai
                txtTicketID.setText("");
                txtCategory.setText("");
                txtCreatedBy.setText("");
                txtRole.setText("");
                cmbAssignTo.setSelectedIndex(0);
                cmbPriority.setSelectedIndex(0);
                selectedTicketId = -1; // Reset selection ID
            }

            ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menugaskan tiket:\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnAssignActionPerformed

    private void tblTicketsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTicketsMouseClicked
        int row = tblTickets.getSelectedRow();
        if (row != -1) {
            selectedTicketId = Integer.parseInt(tblTickets.getValueAt(row, 0).toString());
            txtTicketID.setText(String.valueOf(selectedTicketId));
            txtCategory.setText(tblTickets.getValueAt(row, 2).toString());
            
            // Mengambil info pembuat tiket (Created By) langsung dari database berdasarkan ID Tiket
            String sql = "SELECT u.username, r.name AS role_nama FROM tickets t " +
                         "JOIN users u ON t.created_by = u.id " +
                         "JOIN roles r ON u.role_id = r.id WHERE t.id = ?";
            try {
                Connection conn = new koneksi().connect();
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, selectedTicketId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    txtCreatedBy.setText(rs.getString("username"));
                    txtRole.setText(rs.getString("role_nama"));
                }
                rs.close();
                ps.close();
            } catch (SQLException e) {
                System.out.println("Gagal mengambil info detail user: " + e.getMessage());
            }
            
            // Memasukkan info resolution note ke dalam teks area (khusus role guru/staf)
            Object noteValue = tblTickets.getValueAt(row, 6);
            if (noteValue != null && !noteValue.toString().equals("-")) {
                txtResolutionNote.setText(noteValue.toString());
            } else {
                txtResolutionNote.setText("");
            }
            
            // Pengunci otomatis: Jika status tiket di tabel bertuliskan "Closed", kunci tombol aksi
            String statusTiket = tblTickets.getValueAt(row, 4).toString();
            if (statusTiket.equalsIgnoreCase("Open")) {
                txtResolutionNote.setEnabled(true);
                btnCloseTicket.setEnabled(true);
            }else if (statusTiket.equalsIgnoreCase("In_Progress")) {
                txtResolutionNote.setEnabled(true);
                btnCloseTicket.setEnabled(true);
            }else if (statusTiket.equalsIgnoreCase("Closed")) {
                txtResolutionNote.setEnabled(false);
                btnCloseTicket.setEnabled(false);
            } else if (userRoleId == 2) {
                txtResolutionNote.setEnabled(true);
                btnCloseTicket.setEnabled(true);
            }
        }
    }//GEN-LAST:event_tblTicketsMouseClicked

    private void btnCloseTicketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCloseTicketActionPerformed
            if (selectedTicketId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris tiket pada tabel terlebih dahulu!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String note = txtResolutionNote.getText().trim();
        if (note.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Guru wajib mengisi Catatan Penyelesaian (Resolution Note)!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Mengubah status ke 3 (CLOSED) sesuai struktur DB baru berisi 3 status
        String sql = "UPDATE tickets SET status_id = 3, resolution_note = ?, resolved_at = NOW(), assigned_to = ? WHERE id = ?";

        try {
            Connection conn = new koneksi().connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, note);
            ps.setInt(2, userLoggedInId); 
            ps.setInt(3, selectedTicketId);

            if (ps.executeUpdate() > 0) {
                JOptionPane.showMessageDialog(this, "Tiket resmi diselesaikan dan ditutup (CLOSED)!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                txtResolutionNote.setText("");
                loadTicketTable(); 
            }
            ps.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menutup tiket:\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnCloseTicketActionPerformed

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
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat detail dari database:\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnTicketDetailsActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAssign;
    private javax.swing.JButton btnCloseTicket;
    private javax.swing.JButton btnTicketDetails;
    private javax.swing.JComboBox<String> cmbAssignTo;
    private javax.swing.JComboBox<String> cmbPriority;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanelTableData;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblTickets;
    private javax.swing.JTextField txtCategory;
    private javax.swing.JTextField txtCreatedBy;
    private javax.swing.JTextArea txtResolutionNote;
    private javax.swing.JTextField txtRole;
    private javax.swing.JTextField txtTicketID;
    // End of variables declaration//GEN-END:variables
}
