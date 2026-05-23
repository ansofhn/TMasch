package Report;

import Koneksi.koneksi;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.export.*;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;

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

    /**
     * Compile .jrxml jika belum ada .jasper, lalu tampilkan di viewer.
     * @param namaFile  nama file tanpa ekstensi, contoh: "LaporanRangkumanTiket"
     * @param parameter Map parameter tambahan (boleh null)
     */
    public static void tampilkan(String namaFile, Map<String, Object> parameter) {
        try {
            JasperPrint print = buat(namaFile, parameter);
            JasperViewer.viewReport(print, false);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "Gagal membuka laporan: " + e.getMessage(),
                "Error Laporan", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Export laporan ke PDF.
     * @param namaFile   nama file laporan tanpa ekstensi
     * @param parameter  Map parameter (boleh null)
     * @param outputPath path file PDF tujuan
     */
    public static void exportPDF(String namaFile, Map<String, Object> parameter, String outputPath) {
        try {
            JasperPrint print = buat(namaFile, parameter);
            JasperExportManager.exportReportToPdfFile(print, outputPath);
            JOptionPane.showMessageDialog(null,
                "Laporan berhasil disimpan ke:\n" + outputPath,
                "Export Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "Gagal export PDF: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Export laporan ke Excel (.xlsx).
     */
    public static void exportExcel(String namaFile, Map<String, Object> parameter, String outputPath) {
        try {
            JasperPrint print = buat(namaFile, parameter);
            SimpleXlsxReportConfiguration config = new SimpleXlsxReportConfiguration();
            config.setOnePagePerSheet(false);
            config.setRemoveEmptySpaceBetweenRows(true);

            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputPath));
            exporter.setConfiguration(config);
            exporter.exportReport();

            JOptionPane.showMessageDialog(null,
                "Laporan berhasil disimpan ke:\n" + outputPath,
                "Export Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "Gagal export Excel: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Buat JasperPrint dari file .jrxml dan koneksi DB.
     */
    private static JasperPrint buat(String namaFile, Map<String, Object> parameter) throws Exception {
        String jrxmlPath  = REPORTS_DIR + namaFile + ".jrxml";
        String jasperPath = REPORTS_DIR + namaFile + ".jasper";

        // Compile .jrxml → .jasper (hanya jika .jasper belum ada atau lebih lama)
        File jrxmlFile  = new File(jrxmlPath);
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
