package quru.qa.parsing;

import com.codeborne.pdftest.PDF;
import com.codeborne.selenide.Selenide;
import com.codeborne.xlstest.XLS;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Selenide.$;

public class FileParsingTest {
    private final ClassLoader cl = FileParsingTest.class.getClassLoader();

    @Test
    void myFile() throws URISyntaxException {
        File myFile1 = new File(cl.getResource("human.json").toURI());


    }

    @Test
    void pdfParseTest() throws Exception {
        Selenide.open("https://junit.org/junit5/docs/current/user-guide/");
        File download = $("a[href*='junit-user-guide-5.9.2.pdf']").download();
        PDF pdf = new PDF(download);
        Assertions.assertEquals(
                "Stefan Bechtold, Sam Brannen, Johannes Link, Matthias Merdes, Marc Philipp, Juliette de Rancourt," +
                        " Christian Stein",
                pdf.author
        );
    }

    @Test
    void xlsParseTest() throws Exception {
        Selenide.open("https://excelvba.ru/programmes/Teachers?ysclid=lfcu77j9j9951587711");
        File download = $("a[href*='teachers.xls']").download();
        XLS xls = new XLS(download);


        Assertions.assertTrue(
                xls.excel.getSheetAt(0).getRow(3).getCell(2).getStringCellValue()
                        .startsWith("1. Суммарное количество часов планируемое на штатную по всем разделам плана.")
        );
    }

    @Test
    void csvParseTest() throws Exception {
        try (InputStream is = cl.getResourceAsStream("qaguru.csv");
             InputStreamReader isr = new InputStreamReader(is)) {
            CSVReader csvReader = new CSVReader(isr);
            List<String[]> content = csvReader.readAll();
            Assertions.assertArrayEquals(new String[]{"Тучс", "Junit5"}, content.get(1));

        }
    }

    @Test
    void zipTest() throws Exception {
        try (InputStream is = cl.getResourceAsStream("sample.txt.zip");
             ZipInputStream zs = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zs.getNextEntry()) != null) {
                Assertions.assertEquals("sample.txt.zip", entry.getName());
            }
        }
    }

    @Test
    void jsonTest() throws Exception {

        Gson gson = new Gson();
        try (InputStream is = cl.getResourceAsStream("human.json");
             InputStreamReader isr = new InputStreamReader(is)) {
            JsonObject jsonObject = gson.fromJson(isr, JsonObject.class);

            Assertions.assertTrue(jsonObject.get("hobbies").getAsBoolean());
            Assertions.assertEquals(25, jsonObject.get("number").getAsInt());

        }

    }

    @Test
    void homeTest() throws IOException {
        try (InputStream is = cl.getResourceAsStream("qa.quru.zip");
             ZipInputStream zs = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zs.getNextEntry()) != null) {
                if (entry.getName() == "test2pdf.pdf") break;
            }
        }
    }

    @Test
    void homeTestCsv() throws IOException {
        try (InputStream is = cl.getResourceAsStream("qa.quru.zip");
             ZipInputStream zs = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zs.getNextEntry()) != null) {
                if (entry.getName() == "test3.csv") break;
            }
        }
    }

    @Test
    void homeTestXls() throws IOException {
        try (InputStream is = cl.getResourceAsStream("qa.quru.zip");
             ZipInputStream zs = new ZipInputStream(is)){
            ZipEntry entry;
            while ((entry = zs.getNextEntry()) != null) {
                if (entry.getName() == "xlstest1.xls") break;
            }
        }
    }
}

