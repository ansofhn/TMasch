package Report;

import Koneksi.koneksi;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility untuk menjalankan laporan JasperReports.
 * Semua laporan di-load dari folder /reports/*.jrxml
 *
 * Cara pakai:
 *   ReportUtil.tampilkan("LaporanRangkumanTiket", null);
 *   ReportUtil.exportPDF("LaporanRangkumanTiket", null, "C:/output.pdf");
 */
public class ReportUtil {

    /** Path ke folder reports relatif dari root project */
    private static final String REPORTS_DIR = "reports/";

    private static String getReportsDir() {
    // Ambil lokasi class yang sedang berjalan, mundur ke root project
    String classPath = ReportUtil.class.getProtectionDomain()
                           .getCodeSource().getLocation().getPath();
    File classDir = new File(classPath);
    
    // Naik dari /build/classes/ ke root project
    File projectRoot = classDir.getParentFile().getParentFile();
    return projectRoot.getAbsolutePath() + "/reports/";
}
    /**
     * Compile .jrxml jika belum ada .jasper, lalu tampilkan di viewer.
     * @param namaFile  nama file tanpa ekstensi, contoh: "LaporanRangkumanTiket"
     * @param parameter Map parameter tambahan (boleh null)
     */
    public static void tampilkan(String namaFile, Map<String, Object> parameter) {
    try {
        JasperPrint print = buat(namaFile, parameter);
        JasperViewer.viewReport(print, false);
    } catch (Throwable e) { // <-- Ubah Exception menjadi Throwable
        JOptionPane.showMessageDialog(null,
            "Gagal membuka laporan: " + e.toString(), // Gunakan toString() agar nama Error-nya terlihat
            "Error Laporan", JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
    }
}

    /**
     * Buat JasperPrint dari file .jrxml dan koneksi DB.
     */
    private static JasperPrint buat(String namaFile, Map<String, Object> parameter) throws Exception {
        String jrxmlPath  = REPORTS_DIR + namaFile + ".jrxml";
        String jasperPath = REPORTS_DIR + namaFile + ".jasper";

        File jrxmlFile = new File(jrxmlPath);
        System.out.println("=== DEBUG REPORT PATH ===");
        System.out.println("Working dir : " + new File(".").getAbsolutePath());
        System.out.println("jrxml path  : " + jrxmlFile.getAbsolutePath());
        System.out.println("File exists : " + jrxmlFile.exists());
        System.out.println("=========================");
    
        // Compile .jrxml → .jasper (hanya jika .jasper belum ada atau lebih lama)
        File jasperFile = new File(jasperPath);

        if (!jasperFile.exists() || jrxmlFile.lastModified() > jasperFile.lastModified()) {
            JasperCompileManager.compileReportToFile(jrxmlPath, jasperPath);
        }

        // Buat koneksi DB
        koneksi db     = new koneksi();
        Connection conn = db.connect();

        // Parameter default
        Map<String, Object> params = new HashMap<>();
        params.put("REPORT_LOCALE", new java.util.Locale("id", "ID"));
        if (parameter != null) params.putAll(parameter);

        // Fill report
        JasperPrint print = JasperFillManager.fillReport(jasperPath, params, conn);
        conn.close();
        return print;
    }
}
