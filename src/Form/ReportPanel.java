package Form;

import Report.ReportUtil;
import java.awt.*;
import javax.swing.*;

public class ReportPanel extends javax.swing.JPanel {

    public ReportPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(230, 230, 230));

        // ── Header ───────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(60, 60, 60));
        header.setPreferredSize(new Dimension(0, 55));
        header.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitle = new JLabel("Laporan T-Masch");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);

        JPanel headerText = new JPanel();
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.setBackground(new Color(60, 60, 60));
        headerText.add(lblTitle);
        header.add(headerText, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        // ── Grid 4 kartu laporan ─────────────────────────────
        JPanel grid = new JPanel(new GridLayout(2, 2, 16, 16));
        grid.setBackground(new Color(230, 230, 230));
        grid.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        grid.add(buildReportCard(
            "Rangkuman Tiket",
            "Daftar semua tiket beserta status,\nkategori, prioritas, dan waktu buat.",
            "LaporanRangkumanTiket"
        ));

        grid.add(buildReportCard(
            "Waktu Resolusi",
            "Mengukur waktu penyelesaian tiket\nvs target SLA per prioritas.",
            "LaporanWaktuResolusi"
        ));

        grid.add(buildReportCard(
            "Tren Tiket",
            "Jumlah tiket per bulan berdasarkan\nstatus — untuk analisis tren.",
            "LaporanTrenTiket"
        ));

        grid.add(buildReportCard(
            "Aktivitas User",
            "Ringkasan tiket yang dibuat dan\ndiselesaikan per user aktif.",
            "LaporanAktivitasUser"
        ));

        add(grid, BorderLayout.CENTER);
    }

    /**
     * Membangun card satu laporan dengan tombol Tampilkan dan Export PDF.
     */
    private JPanel buildReportCard(String title,
                                    String desc, String reportName) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Body
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTitle.setForeground(new Color(30, 30, 30));
        lblTitle.setAlignmentX(LEFT_ALIGNMENT);

        JTextArea lblDesc = new JTextArea(desc);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesc.setForeground(new Color(100, 100, 100));
        lblDesc.setBackground(Color.WHITE);
        lblDesc.setEditable(false);
        lblDesc.setWrapStyleWord(true);
        lblDesc.setLineWrap(true);
        lblDesc.setAlignmentX(LEFT_ALIGNMENT);
        lblDesc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // Tombol
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setBackground(Color.WHITE);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);

        JButton btnTampil = makeBtn("Tampilkan", Color.WHITE);

        btnTampil.addActionListener(e -> {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            new Thread(() -> {
                ReportUtil.tampilkan(reportName, null);
                SwingUtilities.invokeLater(() ->
                    setCursor(Cursor.getDefaultCursor()));
            }).start();
        });

        btnRow.add(btnTampil);
        btnRow.add(Box.createHorizontalStrut(8));

        body.add(Box.createVerticalStrut(4));
        body.add(lblTitle);
        body.add(Box.createVerticalStrut(8));
        body.add(lblDesc);
        body.add(Box.createVerticalStrut(12));
        body.add(btnRow);

        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JButton makeBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 11));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(120, 30));
        return b;
    }
}
