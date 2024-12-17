import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.util.data.norm.MaxMinNormalizer;
import java.io.*;
import java.util.*;

public class DataLoader {

    public static DataSet[] loadAndSplitData(String fileName, double trainRatio) throws IOException {
        List<DataSetRow> rows = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line;
        br.readLine(); // Başlık satırını atla

        // Veriyi oku ve DataSetRow olarak listeye ekle
        while ((line = br.readLine()) != null) {
            String[] values = line.split("\\s+"); // Veriyi boşluklara göre ayır
            double experience = Double.parseDouble(values[0].replace(",", "."));
            double gender = Double.parseDouble(values[1].replace(",", "."));
            double education = Double.parseDouble(values[2].replace(",", "."));
            double predictedSalary = Double.parseDouble(values[3].replace(",", "."));
            rows.add(new DataSetRow(new double[]{experience, gender, education}, new double[]{predictedSalary}));
        }
        br.close();

        // Verileri normalize et
        DataSet dataSet = new DataSet(3, 1); // 3 giriş, 1 çıkış
        for (DataSetRow row : rows) {
            dataSet.addRow(row);
        }
        normalizeData(dataSet);

        // Veriyi karıştır
        Collections.shuffle(rows);

        // Eğitim ve test setlerini oluştur
        int trainSize = (int) (rows.size() * trainRatio);

        DataSet trainingSet = new DataSet(3, 1); // 3 giriş, 1 çıkış
        DataSet testSet = new DataSet(3, 1);

        // Eğitim seti için ilk %trainRatio'luk kısmı ekle
        for (int i = 0; i < rows.size(); i++) {
            if (i < trainSize) {
                trainingSet.addRow(rows.get(i));
            } else {
                testSet.addRow(rows.get(i));
            }
        }

        return new DataSet[]{trainingSet, testSet};
    }

    /**
     * Verileri min-max normalizasyonu ile normalize eder.
     *
     * @param dataSet Normalizasyon yapılacak veri seti
     */
    private static void normalizeData(DataSet dataSet) {
        // MaxMinNormalizer ile normalizasyon
        MaxMinNormalizer normalizer = new MaxMinNormalizer();

        // DataSet üzerinde normalizasyon işlemi
        normalizer.normalize(dataSet); // Veriyi normalize et
        System.out.println("Veriler başarıyla normalize edildi.");
    }
}
