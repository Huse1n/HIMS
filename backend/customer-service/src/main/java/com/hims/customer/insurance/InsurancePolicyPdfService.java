package com.hims.customer.insurance;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class InsurancePolicyPdfService {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public byte[] generatePdf(InsurancePolicy policy) {

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Document document = new Document();

        try {
            PdfWriter.getInstance(document, output);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
            Font headingFont = new Font(Font.HELVETICA, 13, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 11);

            // Titel
            document.add(new Paragraph(
                    "HUK-COBURG Kfz-Versicherung", titleFont
            ));

            document.add(new Paragraph(" "));
            document.add(new Paragraph(
                    "Versicherungsvertrag", headingFont
            ));
            document.add(new Paragraph(" "));

            // Vertragsinformationen
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);

            addRow(table, "Vertragsnummer", policy.getPolicyNumber());
            addRow(table, "Vertragsstatus", policy.getStatus().name());

            addRow(table, "Kunden-ID",
                    String.valueOf(policy.getCustomer().getId()));

            addRow(table, "Fahrzeughersteller",
                    policy.getVehicleManufacturer());

            addRow(table, "Fahrzeugmodell",
                    policy.getVehicleModel());

            addRow(table, "Kennzeichen",
                    policy.getLicensePlate());

            addRow(table, "Erstzulassung",
                    String.valueOf(policy.getFirstRegistrationYear()));

            addRow(table, "Versicherungsschutz",
                    getInsuranceTypeLabel(policy.getInsuranceType()));

            addRow(table, "Schutzbrief",
                    policy.isRoadsideAssistance() ? "Ja" : "Nein");

            addRow(table, "Zahlungsweise",
                    getPaymentFrequencyLabel(policy.getPaymentFrequency()));

            addRow(table, "Versicherungsbeginn",
                    policy.getStartDate().format(DATE_FORMAT));

            addRow(table, "Versicherungsende",
                    policy.getEndDate().format(DATE_FORMAT));

            addRow(table, "Jahresbeitrag",
                    policy.getAnnualPremium() + " EUR");

            addRow(table, "Beitrag pro Zahlung",
                    policy.getInstallmentAmount() + " EUR");

            document.add(table);

            document.add(new Paragraph(" "));

            Paragraph note = new Paragraph(
                    "Hinweis: Dieses Dokument wurde automatisch " +
                            "durch die HIMS-Demoanwendung erstellt. " +
                            "Es handelt sich nicht um einen echten Versicherungsvertrag.",
                    normalFont
            );

            document.add(note);

        } catch (DocumentException e) {
            throw new IllegalStateException(
                    "PDF konnte nicht erstellt werden.", e
            );
        } finally {
            document.close();
        }

        return output.toByteArray();
    }

    private void addRow(PdfPTable table, String label, String value) {

        table.addCell(label);
        table.addCell(value != null ? value : "-");
    }

    private String getInsuranceTypeLabel(InsuranceType type) {
        return switch (type) {
            case LIABILITY -> "Haftpflicht";
            case PARTIAL_COVERAGE -> "Teilkasko";
            case FULL_COVERAGE -> "Vollkasko";
        };
    }

    private String getPaymentFrequencyLabel(PaymentFrequency frequency) {
        return switch (frequency) {
            case YEARLY -> "Jaehrlich";
            case HALF_YEARLY -> "Halbjaehrlich";
            case QUARTERLY -> "Vierteljaehrlich";
        };
    }
}