package hu.bsstudio.raktr.pdfgeneration;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.TabStop;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TabAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class RentPdfCreator {

    private static final String DATEFORMAT = "yyyy.MM.dd.";

    @SuppressWarnings({"checkstyle:MethodLength", "checkstyle:MagicNumber"})
    public static void generatePdf(final RentPdfData rentData) {
        String teamName = rentData.getTeamName();
        String teamLeaderName = rentData.getTeamLeaderName();
        String renterName = rentData.getRenterName();
        String renterId = rentData.getRenterId();
        String firstSignerName = rentData.getFirstSignerName();
        String firstSignerTitle = rentData.getFirstSignerTitle();
        String secondSignerName = rentData.getSecondSignerName();
        String secondSignerTitle = rentData.getSecondSignerTitle();

        String outDate = rentData.getOutDate();
        String backDate = rentData.getBackDate();

        Map<String, Integer> items = rentData.getItems();

        try {
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(rentData.getFileName() + ".pdf"));
            Document document = new Document(pdfDoc, PageSize.A4);

            float pageWidth = pdfDoc.getDefaultPageSize().getWidth() - document.getLeftMargin() - document.getRightMargin();

            List<TabStop> centerTabStop = new ArrayList<>();
            centerTabStop.add(new TabStop(pageWidth / 2, TabAlignment.CENTER));

            PdfFont times = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN, PdfEncodings.CP1250);
            PdfFont timesBold = PdfFontFactory.createFont(StandardFonts.TIMES_BOLD, PdfEncodings.CP1250);

            Paragraph title = new Paragraph();
            title
                .addTabStops(centerTabStop)
                .setFont(timesBold)
                .setFontSize(20)
                .add(new Tab())
                .add("Szállítólevél\n")
                .setFixedLeading(2);

            document.add(title);

            Paragraph subTitle = new Paragraph();
            subTitle
                .addTabStops(centerTabStop)
                .setFont(times)
                .setFontSize(10)
                .add(new Tab())
                .add("- Készült négy példányban -")
                .setMarginBottom(0);

            document.add(subTitle);

            Paragraph sch = new Paragraph();
            sch
                .addTabStops(centerTabStop)
                .setFont(timesBold)
                .setFontSize(16)
                .add(new Tab())
                .add("Schönherz Kollégium")
                .setMarginTop(12)
                .setMarginBottom(15);

            document.add(sch);

            Table firstTable = new Table(2).useAllAvailableWidth();
            Cell cellSzallito = new Cell();
            Cell cellFelelosseg = new Cell();

            Paragraph szallitoHeader = new Paragraph();
            szallitoHeader
                .setFont(timesBold)
                .setFontSize(14)
                .add("Szállító:\n")
                .setFixedLeading(30);

            Paragraph szallitoData = new Paragraph();
            szallitoData
                .setFont(times)
                .setFontSize(12)
                .add("Kör neve: " + teamName + "\n")
                .add("Körvezető: " + teamLeaderName)
                .setFixedLeading(30);

            cellSzallito
                .add(szallitoHeader)
                .add(szallitoData)
                .setWidth(pageWidth / 2);

            Paragraph felelossegHeader = new Paragraph();
            felelossegHeader
                .setFont(timesBold)
                .setFontSize(14)
                .add("Felelősségvállaló:\n")
                .setFixedLeading(30);

            Paragraph felelossegData = new Paragraph();
            felelossegData
                .setFont(times)
                .setFontSize(12)
                .add("Személy: " + renterName + " \n")
                .add("Szig. szám: " + renterId)
                .setFixedLeading(30);

            cellFelelosseg
                .add(felelossegHeader)
                .add(felelossegData);

            firstTable
                .addCell(cellSzallito)
                .addCell(cellFelelosseg);

            document.add(firstTable);

            List<TabStop> dateTabs = new ArrayList<>();
            dateTabs.add(new TabStop(pageWidth / 4 + 10, TabAlignment.LEFT));

            Paragraph outDateData = new Paragraph();
            outDateData
                .setFont(times)
                .setFontSize(12)
                .add(outDate)
                .add(" (Év, Hónap, Nap)");

            Paragraph outDateLine = new Paragraph();
            outDateLine
                .addTabStops(dateTabs)
                .setFont(timesBold)
                .setFontSize(12)
                .add("Kiszállítás időpontja:")
                .add(new Tab())
                .add(outDateData);

            Paragraph backDateData = new Paragraph();
            backDateData
                .setFont(times)
                .setFontSize(12)
                .add(backDate)
                .add(" (Év, Hónap, Nap)");

            Paragraph backDateLine = new Paragraph();
            backDateLine
                .addTabStops(dateTabs)
                .setFont(timesBold)
                .setFontSize(12)
                .add("Visszaszállítás időpontja:")
                .add(new Tab())
                .add(backDateData)
                .setMultipliedLeading(0.1f);

            document
                .add(outDateLine)
                .add(backDateLine);

            Paragraph permissionSch = new Paragraph();
            permissionSch
                .setFont(timesBold)
                .setFontSize(12)
                .add("Schönherz Kollégiumból ");

            Paragraph permissionText = new Paragraph();
            permissionText
                .setFont(times)
                .setFontSize(12)
                .add("A szállítón szereplő eszközöknek a fent jelzett időpontban a ")
                .add(permissionSch)
                .add("történő szállítására engedélyt kérek.\n")
                .setMultipliedLeading(1.0f)
                .setMarginTop(10);

            document.add(permissionText);

            SimpleDateFormat format = new SimpleDateFormat(DATEFORMAT);
            Date date = new Date();

            Paragraph today = new Paragraph();
            today
                .setFont(times)
                .setFontSize(12)
                .add("Kelt: " + format.format(date))
                .setMultipliedLeading(2);

            document.add(today);

            var line = new com.itextpdf.kernel.pdf.canvas.draw.DottedLine();
            line.setGap(2.0f);

            List<TabStop> signUnderlineTabs = new ArrayList<>();
            signUnderlineTabs.add(new TabStop(pageWidth / 12));
            signUnderlineTabs.add(new TabStop(pageWidth / 12 + pageWidth / 3, TabAlignment.LEFT, line));
            signUnderlineTabs.add(new TabStop(pageWidth / 12 + pageWidth / 3 + pageWidth / 6));
            signUnderlineTabs.add(new TabStop(pageWidth - pageWidth / 12, TabAlignment.LEFT, line));

            List<TabStop> signNamesTabs = new ArrayList<>();
            signNamesTabs.add(new TabStop(pageWidth / 12 + pageWidth / 6, TabAlignment.CENTER));
            signNamesTabs.add(new TabStop(pageWidth - (pageWidth / 12 + pageWidth / 6), TabAlignment.CENTER));

            Paragraph signsLine = new Paragraph();
            signsLine
                .setFont(times)
                .setFontSize(12)
                .addTabStops(signUnderlineTabs)
                .add(new Tab())
                .add(new Tab())
                .add(new Tab())
                .add(new Tab())
                .setFixedLeading(30);

            document.add(signsLine);

            Paragraph firstSignsNames = new Paragraph();
            firstSignsNames
                .addTabStops(signNamesTabs)
                .setFontSize(12)
                .setFont(timesBold)
                .add(new Tab())
                .add("Körvezető")
                .add(new Tab())
                .add("Felelősségvállaló")
                .setFixedLeading(35);

            document.add(firstSignsNames);

            Paragraph permissionGiveText = new Paragraph();
            permissionGiveText
                .setFont(times)
                .setFontSize(12)
                .add("A szállítón szereplő eszközöknek a fent jelzett időpontban a ")
                .add(permissionSch)
                .add("történő szállítását engedélyezem.\n")
                .setMultipliedLeading(1.0f)
                .setMarginTop(10);

            document.add(permissionGiveText);

            document.add(signsLine);

            Paragraph secondSignsNames = new Paragraph();
            secondSignsNames
                .addTabStops(signNamesTabs)
                .setFontSize(12)
                .setFont(timesBold)
                .add(new Tab())
                .add(firstSignerName)
                .add(new Tab())
                .add(secondSignerName)
                .setFixedLeading(35);

            Paragraph secondSignsTitles = new Paragraph();
            secondSignsTitles
                .addTabStops(signNamesTabs)
                .setFontSize(12)
                .setFont(timesBold)
                .add(new Tab())
                .add(firstSignerTitle)
                .add(new Tab())
                .add(secondSignerTitle)
                .setFixedLeading(-20);

            document.add(secondSignsNames);
            document.add(secondSignsTitles);

            Table itemsTable = new Table(new float[] {pageWidth / 12, pageWidth * 5 / 12, pageWidth / 12, pageWidth * 5 / 12})
                .useAllAvailableWidth()
                .setMarginTop(30);

            for (int i = 0; i < 2; i++) {
                Paragraph itemNumHeader = new Paragraph();
                itemNumHeader
                    .setFont(times)
                    .setFontSize(12)
                    .add("db");

                Paragraph itemNameHeader = new Paragraph();
                itemNameHeader
                    .setFont(times)
                    .setFontSize(12)
                    .add("megnevezés");

                itemsTable.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).add(itemNumHeader));
                itemsTable.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).add(itemNameHeader));
            }

            for (var item : items.entrySet()) {
                Paragraph itemNum = new Paragraph();
                itemNum
                    .setFont(times)
                    .setFontSize(12)
                    .add(item.getValue().toString());

                Paragraph itemName = new Paragraph();
                itemName
                    .setFont(times)
                    .setFontSize(12)
                    .add(item.getKey());

                itemsTable.addCell(new Cell().add(itemNum));
                itemsTable.addCell(new Cell().add(itemName));
            }

            document.add(itemsTable);

            Paragraph seenBy = new Paragraph();
            seenBy
                .addTabStops(signUnderlineTabs)
                .setFont(times)
                .setFontSize(12)
                .setMarginLeft(pageWidth / 2)
                .setFixedLeading(40)
                .add("Látta:")
                .add(new Tab())
                .add(new Tab());

            document.add(seenBy);

            Paragraph seenByTitle = new Paragraph();
            seenByTitle
                .addTabStops(signNamesTabs)
                .setFont(timesBold)
                .setFontSize(12)
                .setMarginLeft(pageWidth / 2)
                .add(new Tab())
                .add("Üzemeltető")
                .setFixedLeading(-20);

            document.add(seenByTitle);

            document.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
