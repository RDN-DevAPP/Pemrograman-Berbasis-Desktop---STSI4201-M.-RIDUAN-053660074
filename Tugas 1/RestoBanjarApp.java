// Nama: M. RIDUAN
// NIM: 053660074
// UPBJJ: UT Banjarmasin

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Scanner;

// Kelas utama: tempat seluruh proses program berjalan
public class RestoBanjarApp {

    // Formatter untuk tampilan mata uang Rupiah
    private static final NumberFormat RP = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    // Maksimal 4 entri pesanan sesuai aturan soal
    private static final int MAX_ENTRI = 4;

    // Biaya layanan tetap restoran
    private static final int BIAYA_LAYANAN = 20000;

    // Tarif pajak 10%
    private static final double TARIF_PAJAK = 0.10;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // Tampilan header & nama restoran
        System.out.println("============================================");
        System.out.println("   Selamat Datang di RM BANJAR NYAMAN   ");
        System.out.println("  Cita Rasa Banua - Nyaman di Perut 😋  ");
        System.out.println("============================================\n");

        // Data menu menggunakan Array of Objects (Array dari kelas Menu)
        Menu[] daftarMenu = {
            // 7 menu makanan Banjar
            new Menu(101, "Ketupat Kandangan", Kategori.MAKANAN, 30000),
            new Menu(102, "Soto Banjar", Kategori.MAKANAN, 25000),
            new Menu(103, "Lontong Orari", Kategori.MAKANAN, 28000),
            new Menu(104, "Ayam Masak Habang", Kategori.MAKANAN, 32000),
            new Menu(105, "Gangan Asam Banjar", Kategori.MAKANAN, 27000),
            new Menu(106, "Nasi Sop Banjar", Kategori.MAKANAN, 23000),
            new Menu(107, "Sate Ayam Banjar", Kategori.MAKANAN, 35000),

            // 4 menu minuman
            new Menu(201, "Es Sarang Burung", Kategori.MINUMAN, 10000),
            new Menu(202, "Es Selasih", Kategori.MINUMAN, 9000),
            new Menu(203, "Teh Tarik Banjar", Kategori.MINUMAN, 12000),
            new Menu(204, "Air Mineral", Kategori.MINUMAN, 6000)
        };

        // Menampilkan daftar menu dalam bentuk tabel
        System.out.println("===== MENU RM BANJAR NYAMAN =====");
        System.out.printf("%-6s %-24s %-10s %10s%n", "KODE", "NAMA", "KATEGORI", "HARGA");
        System.out.println("----------------------------------------------------");
        for (Menu m : daftarMenu) {
            System.out.printf("%-6d %-24s %-10s %10s%n",
                    m.kode, m.nama, m.kategori.label, RP.format(m.harga));
        }

        // Array untuk menyimpan pesanan dari user
        int[] kodePesan = new int[MAX_ENTRI];
        int[] jumlahPesan = new int[MAX_ENTRI];
        int count = 0; // penghitung jumlah pesanan

        System.out.println("\nMasukkan pesanan Anda (maks 4 entri)");
        System.out.println("Masukkan '0' jika selesai\n");

        // Perulangan input pesanan
        while (count < MAX_ENTRI) {
            System.out.print("Kode Menu   : ");
            int kode = input.nextInt();

            if (kode == 0) break; // selesai input pesanan

            // Cek apakah kode menu tersedia
            Menu item = cariByKode(daftarMenu, kode);
            if (item == null) {
                System.out.println("⚠️ Kode menu tidak ditemukan!");
                continue;
            }

            System.out.print("Jumlah Pesan: ");
            int qty = input.nextInt();
            if (qty <= 0) {
                System.out.println("⚠️ Jumlah harus lebih dari 0!");
                continue;
            }

            // Simpan ke array
            kodePesan[count] = kode;
            jumlahPesan[count] = qty;
            count++;
        }

        if (count == 0) {
            System.out.println("Tidak ada pesanan. Terima kasih!");
            return;
        }

        // Proses perhitungan total biaya
        int subtotal = 0;
        int diskonMinuman = 0;
        int hargaMinumanTermurah = Integer.MAX_VALUE;
        boolean adaMinuman = false;

        System.out.println("\n========== STRUK PEMBAYARAN ==========");
        System.out.printf("%-24s %5s %12s%n", "Item", "Qty", "Subtotal");
        System.out.println("--------------------------------------");

        // Menampilkan detail pesanan dan menjumlahkan harga
        for (int i = 0; i < count; i++) {
            Menu m = cariByKode(daftarMenu, kodePesan[i]);
            int sub = m.harga * jumlahPesan[i]; // subtotal item
            subtotal += sub;

            System.out.printf("%-24s %5d %12s%n",
                    m.nama, jumlahPesan[i], RP.format(sub));

            // Pengecekan minuman untuk promo
            if (m.kategori == Kategori.MINUMAN) {
                adaMinuman = true;
                if (m.harga < hargaMinumanTermurah)
                    hargaMinumanTermurah = m.harga;
            }
        }

        // Diskon 10% jika subtotal > Rp100.000
        double diskon10 = (subtotal > 100000) ? subtotal * 0.10 : 0;

        // Promo: 1 minuman termurah gratis jika subtotal > Rp50.000
        if (subtotal > 50000 && adaMinuman) {
            diskonMinuman = hargaMinumanTermurah;
        }

        int totalDiskon = (int) (diskon10 + diskonMinuman);
        int dasarPajak = subtotal - totalDiskon;
        int pajak = (int) (dasarPajak * TARIF_PAJAK);
        int totalAkhir = dasarPajak + pajak + BIAYA_LAYANAN;

        // Rincian struk akhir
        System.out.println("--------------------------------------");
        System.out.printf("%-30s %10s%n", "Subtotal", RP.format(subtotal));
        System.out.printf("%-30s %10s%n", "Diskon 10% (>100k)",
                (diskon10 > 0 ? "-"+RP.format((int)diskon10) : "-Rp0"));
        System.out.printf("%-30s %10s%n", "Promo Minuman (>50k)",
                (diskonMinuman > 0 ? "-"+RP.format(diskonMinuman) : "-Rp0"));
        System.out.printf("%-30s %10s%n", "Pajak 10%",
                RP.format(pajak));
        System.out.printf("%-30s %10s%n", "Biaya Layanan",
                RP.format(BIAYA_LAYANAN));
        System.out.println("--------------------------------------");
        System.out.printf("%-30s %10s%n", "TOTAL BAYAR",
                RP.format(totalAkhir));
        System.out.println("======================================");
        System.out.println("Terima kasih! Selamat menikmati 😊");
    }

    // Method untuk mencari menu berdasarkan kode (mengembalikan objek Menu)
    private static Menu cariByKode(Menu[] daftar, int kode) {
        for (Menu m : daftar) {
            if (m.kode == kode) return m;
        }
        return null;
    }
}

// Enum: tipe data khusus untuk kategori makanan/minuman
enum Kategori {
    MAKANAN("Makanan"),
    MINUMAN("Minuman");

    public final String label;
    Kategori(String l) { this.label = l; }
}

// Kelas Menu untuk merepresentasikan data menu di restoran
class Menu {
    int kode;
    String nama;
    Kategori kategori;
    int harga;

    // Konstruktor Menu untuk inisialisasi atribut
    Menu(int kode, String nama, Kategori kategori, int harga) {
        this.kode = kode;
        this.nama = nama;
        this.kategori = kategori;
        this.harga = harga;
    }
}
