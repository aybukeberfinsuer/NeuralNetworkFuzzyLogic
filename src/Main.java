import org.neuroph.core.data.DataSet;
import java.io.IOException;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        scanner.useLocale(Locale.US); // Ondalık ayraç olarak nokta kullanımı için

        DataSet trainingSet = null; // Eğitim veri seti
        DataSet testSet = null;     // Test veri seti

        // Veri yükleme
        try {
            System.out.println("Veri seti yükleniyor...");
            DataSet[] data = DataLoader.loadAndSplitData("salary_data.csv", 0.75); // %75 eğitim, %25 test
            trainingSet = data[0];
            testSet = data[1];
            System.out.println("Veri seti başarıyla yüklendi!");
        } catch (IOException e) {
            System.err.println("Veri yükleme hatası: " + e.getMessage());
            return;
        }

        NeuralNetworkProcess nnProcess = new NeuralNetworkProcess(trainingSet, testSet);

        // Ana Menü
        while (true) {
            System.out.println("\n==== Ana Menu ====");
            System.out.println("1- Agi Egit ve Test Et (Momentumlu)");
            System.out.println("2- Agi Egit ve Test Et (Momentumsuz)");
            System.out.println("3- Agi Egit Epoch Goster");
            System.out.println("4- Agi Egit ve Tekli Test (Momentumlu)");
            System.out.println("5- K-Fold Test ve Agi Egit ve Test Et (Momentumlu)");
            System.out.println("0- Cikis");
            System.out.print("Seciminizi yapin: ");

            // Kullanıcı girişini güvenli hale getir
            int choice;
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
            } else {
                System.out.println("Gecersiz giris, lutfen bir sayi girin.");
                scanner.next(); // Geçersiz girdiyi temizle
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.println("\nMomentumlu egitim ve test basliyor...");
                    nnProcess.trainNetwork(true); // Momentumlu eğitim
                    break;

                case 2:
                    System.out.println("\nMomentumsuz egitim ve test basliyor...");
                    nnProcess.trainNetwork(false); // Momentumsuz eğitim
                    break;

                case 3:
                    System.out.println("\nEpoch gosterimi ile egitim basliyor...");
                   // nnProcess.trainNetworkWithEpochDisplay(scanner);  // Epoch gösterimi
                    break;

                case 4:
                    System.out.println("\nKullanici parametreleri ile egitim basliyor...");
                    nnProcess.trainNetworkWithUserParameters(scanner); // Kullanıcı girdisiyle eğitim
                    break;

                case 5:
                    System.out.println("\nK-Fold Test basliyor...");
                     nnProcess.kFoldCrossValidation(scanner); // K-Fold Test (Momentumlu)
                   
                    break;

                case 0:
                    System.out.println("Programdan cikiliyor...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Gecersiz secim, tekrar deneyin.");
            }
        }
    }
}
