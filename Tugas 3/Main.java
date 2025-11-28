// ============================================================================
// Nama  : M. RIDUAN
// NIM   : 053660074
// Kelas : Tugas Praktik Pemrograman Berbasis Objek
// Studi Kasus : Manajemen Restoran
//
// Deskripsi singkat:
// Aplikasi konsol untuk manajemen restoran yang mengimplementasikan:
// - Abstraksi (class MenuItem + method abstrak tampilMenu())
// - Inheritance (Makanan, Minuman, Diskon extends MenuItem)
// - Polymorphism (override tampilMenu() di tiap turunan)
// - Encapsulation (atribut private + getter/setter)
// - Exception Handling (MenuItemTidakDitemukanException, NumberFormatException, IOException)
// - I/O File (menu.csv untuk menu, *.txt struk, baca & tulis)
// - Struktur keputusan & perulangan (menu interaktif di console)
//
// Fitur sesuai tugas:
// - Tampilkan Menu
// - Tambah Item Menu
// - Buat Pesanan
// - Hitung total & diskon
// - Tampilkan Struk
// - Simpan daftar menu ke file CSV
// - Load daftar menu dari file CSV
// - Simpan struk ke file TXT
// - Baca struk dari file TXT
// ============================================================================

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

// ============================================================================
// KELAS MAIN (PROGRAM UTAMA)
// ============================================================================

public class Main {

    // Scanner global untuk input dari keyboard
    private static final Scanner INPUT = new Scanner(System.in);

    // Formatter untuk mata uang Rupiah
    private static final NumberFormat RP =
            NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    // Nama file CSV untuk menyimpan daftar menu
    private static final String NAMA_FILE_MENU = "menu.csv";

    // Folder untuk menyimpan struk pesanan
    private static final String FOLDER_STRUK = "struk";

    // Objek Menu utama yang menyimpan semua item menu
    private static final Menu MENU_RESTORAN = new Menu();

    // Pesanan aktif (yang sedang dikerjakan)
    private static Pesanan pesananAktif = null;

    // ------------------------------------------------------------------------
    // METHOD MAIN
    // ------------------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("     APLIKASI MANAJEMEN RESTORAN - RM NYAMAN     ");
        System.out.println("=================================================");

        // 1. Muat menu dari file CSV (jika ada), jika tidak gunakan menu default
        muatMenuAwal();

        // 2. Pastikan folder struk sudah ada
        buatFolderStrukJikaBelumAda();

        // 3. Perulangan menu utama sampai user memilih keluar
        boolean jalan = true;
        while (jalan) {
            tampilkanMenuUtama();
            int pilihan = bacaInt("Pilih menu (1-8): ");

            switch (pilihan) {
                case 1:
                    // Fitur: Tampilkan Menu
                    tampilkanMenuRestoran();
                    break;
                case 2:
                    // Fitur: Tambah Item Menu
                    tambahItemMenuBaru();
                    break;
                case 3:
                    // Fitur: Buat Pesanan
                    terimaPesananPelanggan();
                    break;
                case 4:
                    // Fitur: Hitung total & diskon
                    hitungTotalPesanan();
                    break;
                case 5:
                    // Fitur: Tampilkan Struk + Simpan & Baca dari file
                    tampilkanDanSimpanStruk();
                    break;
                case 6:
                    // Fitur: Simpan daftar menu ke file CSV
                    simpanMenuKeFile();
                    break;
                case 7:
                    // Fitur: Load daftar menu dari file CSV
                    loadMenuDariFile();
                    break;
                case 8:
                    System.out.println("Terima kasih, program selesai.");
                    jalan = false;
                    break;
                default:
                    System.out.println("Pilihan tidak dikenal, silakan pilih 1-8.");
            }
        }
    }

    // ------------------------------------------------------------------------
    // TAMPILAN MENU UTAMA (RAPI)
    // ------------------------------------------------------------------------
    private static void tampilkanMenuUtama() {
        System.out.println();
        System.out.println("============== MENU UTAMA APLIKASI ==============");
        System.out.printf("%-3s %-40s%n", "1.", "Tampilkan Menu Restoran");
        System.out.printf("%-3s %-40s%n", "2.", "Tambah Item Menu");
        System.out.printf("%-3s %-40s%n", "3.", "Buat Pesanan");
        System.out.printf("%-3s %-40s%n", "4.", "Hitung Total & Diskon");
        System.out.printf("%-3s %-40s%n", "5.", "Tampilkan & Simpan Struk");
        System.out.printf("%-3s %-40s%n", "6.", "Simpan Daftar Menu ke File (CSV)");
        System.out.printf("%-3s %-40s%n", "7.", "Load Daftar Menu dari File (CSV)");
        System.out.printf("%-3s %-40s%n", "8.", "Keluar dari Program");
        System.out.println("=================================================");
    }

    // ------------------------------------------------------------------------
    // METHOD UTILITAS UNTUK INPUT (DENGAN VALIDASI)
    // ------------------------------------------------------------------------

    // Membaca bilangan bulat (int) dengan penanganan NumberFormatException
    private static int bacaInt(String pesan) {
        while (true) {
            System.out.print(pesan);
            try {
                int nilai = Integer.parseInt(INPUT.nextLine().trim());
                return nilai;
            } catch (NumberFormatException e) {
                System.out.println("Input harus berupa bilangan bulat. Coba lagi.");
            }
        }
    }

    // Membaca bilangan desimal (double) dengan penanganan NumberFormatException
    private static double bacaDouble(String pesan) {
        while (true) {
            System.out.print(pesan);
            try {
                double nilai = Double.parseDouble(INPUT.nextLine().trim());
                return nilai;
            } catch (NumberFormatException e) {
                System.out.println("Input harus berupa angka (boleh desimal). Coba lagi.");
            }
        }
    }

    // Membaca string 1 baris
    private static String bacaString(String pesan) {
        System.out.print(pesan);
        return INPUT.nextLine().trim();
    }

    // ------------------------------------------------------------------------
    // INISIALISASI MENU (LOAD DARI CSV / BUAT DEFAULT)
    // ------------------------------------------------------------------------

    private static void muatMenuAwal() {
        File f = new File(NAMA_FILE_MENU);

        // Jika file menu.csv sudah ada, coba muat dari file
        if (f.exists()) {
            try {
                MENU_RESTORAN.muatDariFile(NAMA_FILE_MENU);
                System.out.println("Menu berhasil dimuat dari file '" + NAMA_FILE_MENU + "'.");
                return; // jika sukses, tidak perlu buat menu default
            } catch (IOException e) {
                System.out.println("Gagal memuat menu dari file: " + e.getMessage());
                System.out.println("Menu default akan digunakan.");
            }
        }

        // Jika file tidak ada atau gagal dibaca, buat menu default (hard-coded)
        MENU_RESTORAN.tambahItem(new Makanan("Soto Banjar", 25000, "Berkuah"));
        MENU_RESTORAN.tambahItem(new Makanan("Lontong Orari", 28000, "Berat"));
        MENU_RESTORAN.tambahItem(new Makanan("Ayam Masak Habang", 32000, "Lauk"));
        MENU_RESTORAN.tambahItem(new Makanan("Gangan Asam Banjar", 27000, "Berkuah"));

        MENU_RESTORAN.tambahItem(new Minuman("Es Sarang Burung", 10000, "Dingin"));
        MENU_RESTORAN.tambahItem(new Minuman("Es Selasih", 9000, "Dingin"));
        MENU_RESTORAN.tambahItem(new Minuman("Teh Tarik Banjar", 12000, "Hangat"));
        MENU_RESTORAN.tambahItem(new Minuman("Air Mineral", 6000, "Dingin"));

        MENU_RESTORAN.tambahItem(new Diskon("Diskon Member 10%", 10));
        MENU_RESTORAN.tambahItem(new Diskon("Diskon Promo 15%", 15));
        MENU_RESTORAN.tambahItem(new Diskon("Diskon Spesial 25%", 25));
    }

    // Membuat folder "struk" jika belum ada
    private static void buatFolderStrukJikaBelumAda() {
        File folder = new File(FOLDER_STRUK);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    // ------------------------------------------------------------------------
    // FITUR: TAMBAH ITEM MENU BARU
    // ------------------------------------------------------------------------

    private static void tambahItemMenuBaru() {
        System.out.println();
        System.out.println("=========== TAMBAH ITEM BARU KE MENU ===========");
        System.out.println("1. Makanan");
        System.out.println("2. Minuman");
        System.out.println("3. Diskon");
        System.out.println("================================================");
        int jenis = bacaInt("Pilih jenis item (1-3): ");

        switch (jenis) {
            case 1:
                // Input data makanan
                String namaMakanan = bacaString("Nama makanan   : ");
                double hargaMakanan = bacaDouble("Harga makanan  : ");
                String jenisMakanan =
                        bacaString("Jenis makanan  (misal: Berat, Ringan, Berkuah): ");
                MENU_RESTORAN.tambahItem(
                        new Makanan(namaMakanan, hargaMakanan, jenisMakanan));
                System.out.println("Makanan berhasil ditambahkan ke menu.");
                break;
            case 2:
                // Input data minuman
                String namaMinuman = bacaString("Nama minuman   : ");
                double hargaMinuman = bacaDouble("Harga minuman  : ");
                String jenisMinuman =
                        bacaString("Jenis minuman  (misal: Dingin, Hangat): ");
                MENU_RESTORAN.tambahItem(
                        new Minuman(namaMinuman, hargaMinuman, jenisMinuman));
                System.out.println("Minuman berhasil ditambahkan ke menu.");
                break;
            case 3:
                // Input data diskon
                String namaDiskon =
                        bacaString("Nama diskon    (misal: Diskon Spesial 20%): ");
                double persen = bacaDouble("Persentase diskon (misal 20 untuk 20%): ");
                MENU_RESTORAN.tambahItem(new Diskon(namaDiskon, persen));
                System.out.println("Diskon berhasil ditambahkan.");
                break;
            default:
                System.out.println("Pilihan jenis tidak valid.");
        }
    }

    // ------------------------------------------------------------------------
    // FITUR: TAMPILKAN MENU RESTORAN
    // ------------------------------------------------------------------------

    private static void tampilkanMenuRestoran() {
        System.out.println();
        MENU_RESTORAN.tampilkanMenu();
    }

    // ------------------------------------------------------------------------
    // FITUR: MENERIMA PESANAN PELANGGAN
    // ------------------------------------------------------------------------

    private static void terimaPesananPelanggan() {
        // Jika belum ada pesanan aktif, buat objek Pesanan baru
        if (pesananAktif == null) {
            pesananAktif = new Pesanan();
        }

        boolean lanjut = true;
        while (lanjut) {
            // Tampilkan menu terlebih dahulu
            tampilkanMenuRestoran();

            // User memasukkan nama menu yang ingin dipesan
            String namaMenu = bacaString(
                    "Masukkan nama menu yang ingin dipesan (atau '0' untuk selesai): ");
            if ("0".equals(namaMenu)) {
                // Keluar dari perulangan pemesanan
                break;
            }

            try {
                // Mencari MenuItem berdasarkan nama (bisa melempar exception)
                MenuItem item = MENU_RESTORAN.cariItemByNama(namaMenu);

                // Diskon tidak boleh dipesan sebagai item makanan/minuman
                if (item instanceof Diskon) {
                    System.out.println("Diskon tidak dapat dipesan sebagai menu. Pilih makanan/minuman.");
                    continue;
                }

                int jumlah = bacaInt("Jumlah porsi: ");
                if (jumlah <= 0) {
                    System.out.println("Jumlah harus lebih dari 0.");
                    continue;
                }

                // Tambahkan item ke pesanan
                pesananAktif.tambahItem(item, jumlah);
                System.out.println("Item berhasil ditambahkan ke pesanan.");

            } catch (MenuItemTidakDitemukanException e) {
                // Exception Handling: menu tidak ditemukan
                System.out.println(e.getMessage());
            }
        }
    }

    // ------------------------------------------------------------------------
    // FITUR: HITUNG TOTAL PESANAN (DENGAN DISKON)
    // ------------------------------------------------------------------------

    private static void hitungTotalPesanan() {
        if (pesananAktif == null || pesananAktif.isKosong()) {
            System.out.println("Belum ada pesanan yang tercatat.");
            return;
        }

        System.out.println();
        System.out.println("============= PERHITUNGAN TOTAL =============");
        System.out.println("Total sementara (tanpa diskon): " +
                RP.format(pesananAktif.hitungTotalSebelumDiskon()));

        // Ambil daftar diskon yang tersedia dari menu
        List<Diskon> diskonTersedia = MENU_RESTORAN.getDiskonTersedia();
        if (diskonTersedia.isEmpty()) {
            // Jika tidak ada diskon sama sekali
            System.out.println("Tidak ada diskon yang tersedia.");
            pesananAktif.setDiskonDipakai(null);
            System.out.println("Total yang harus dibayar: " +
                    RP.format(pesananAktif.hitungTotalSetelahDiskon()));
            return;
        }

        // Tampilkan daftar diskon sebagai pilihan ke user
        System.out.println("Diskon yang tersedia:");
        for (int i = 0; i < diskonTersedia.size(); i++) {
            Diskon d = diskonTersedia.get(i);
            System.out.printf("%d. %-25s (%.0f%%)%n",
                    i + 1, d.getNama(), d.getPersentaseDiskon());
        }
        System.out.println("0. Tanpa diskon");
        System.out.println("=============================================");

        int pilih = bacaInt("Pilih diskon yang akan digunakan: ");
        if (pilih == 0) {
            // Tanpa diskon
            pesananAktif.setDiskonDipakai(null);
        } else if (pilih > 0 && pilih <= diskonTersedia.size()) {
            // Diskon dipilih dari list
            pesananAktif.setDiskonDipakai(diskonTersedia.get(pilih - 1));
        } else {
            System.out.println("Pilihan diskon tidak valid. Tidak ada diskon yang digunakan.");
            pesananAktif.setDiskonDipakai(null);
        }

        System.out.println("Total yang harus dibayar setelah diskon: " +
                RP.format(pesananAktif.hitungTotalSetelahDiskon()));
    }

    // ------------------------------------------------------------------------
    // FITUR: TAMPILKAN STRUK & SIMPAN + BACA DARI FILE
    // ------------------------------------------------------------------------

    private static void tampilkanDanSimpanStruk() {
        if (pesananAktif == null || pesananAktif.isKosong()) {
            System.out.println("Belum ada pesanan yang tercatat.");
            return;
        }

        // Membuat teks struk
        String struk = pesananAktif.buatStruk(RP);

        // Tampilkan struk ke layar
        System.out.println();
        System.out.println(struk);

        // Simpan struk ke file TXT
        String pathFile = FOLDER_STRUK + File.separator + buatNamaFileStruk();
        try {
            pesananAktif.simpanStrukKeFile(pathFile, RP);
            System.out.println("Struk berhasil disimpan ke file: " + pathFile);

            // Baca kembali struk dari file (I/O: Baca struk dari file)
            System.out.println("\n--- Membaca kembali struk dari file ---");
            try (BufferedReader br = new BufferedReader(new FileReader(pathFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            }
            System.out.println("--- Selesai membaca struk dari file ---\n");
        } catch (IOException e) {
            System.out.println("Gagal menyimpan/muat struk ke/dari file: " + e.getMessage());
        }
    }

    // Membuat nama file struk berdasarkan waktu (agar unik)
    private static String buatNamaFileStruk() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String waktu = LocalDateTime.now().format(fmt);
        return "struk_" + waktu + ".txt";
    }

    // ------------------------------------------------------------------------
    // FITUR: SIMPAN & LOAD MENU KE/DARI FILE CSV
    // ------------------------------------------------------------------------

    private static void simpanMenuKeFile() {
        try {
            MENU_RESTORAN.simpanKeFile(NAMA_FILE_MENU);
            System.out.println("Menu berhasil disimpan ke file '" + NAMA_FILE_MENU + "'.");
        } catch (IOException e) {
            System.out.println("Gagal menyimpan menu ke file: " + e.getMessage());
        }
    }

    private static void loadMenuDariFile() {
        try {
            MENU_RESTORAN.muatDariFile(NAMA_FILE_MENU);
            System.out.println("Menu berhasil dimuat ulang dari file '" + NAMA_FILE_MENU + "'.");
        } catch (IOException e) {
            System.out.println("Gagal memuat menu dari file: " + e.getMessage());
        }
    }
}

// ============================================================================
// ABSTRACT CLASS MENUITEM  (ABSTRAKSI & POLYMORPHISM)
// ============================================================================

abstract class MenuItem {

    // Atribut dienkapsulasi (Encapsulation)
    private String nama;
    private double harga;
    private String kategori; // contoh: "Makanan", "Minuman", "Diskon"

    // Konstruktor umum
    public MenuItem(String nama, double harga, String kategori) {
        this.nama = nama;
        this.harga = harga;
        this.kategori = kategori;
    }

    // Getter & Setter (MenuItem)  -> Poin 10
    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }

    public String getKategori() {
        return kategori;
    }

    protected void setKategori(String kategori) {
        this.kategori = kategori;
    }

    // Method abstrak yang wajib di-override (tampilMenu) -> Poin 3
    public abstract void tampilMenu();
}

// ============================================================================
// KELAS TURUNAN: MAKANAN
// ============================================================================

class Makanan extends MenuItem {

    // Atribut tambahan khusus Makanan -> Poin 7
    private String jenisMakanan; // contoh: berat, ringan, berkuah

    public Makanan(String nama, double harga, String jenisMakanan) {
        super(nama, harga, "Makanan");
        this.jenisMakanan = jenisMakanan;
    }

    // Getter/Setter Makanan -> Poin 11
    public String getJenisMakanan() {
        return jenisMakanan;
    }

    public void setJenisMakanan(String jenisMakanan) {
        this.jenisMakanan = jenisMakanan;
    }

    // Override method tampilMenu() -> Poin 16
    @Override
    public void tampilMenu() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        System.out.printf("  %-22s %-12s %-20s%n",
                getNama(),
                formatter.format(getHarga()),
                "(Makanan - " + jenisMakanan + ")");
    }
}

// ============================================================================
// KELAS TURUNAN: MINUMAN
// ============================================================================

class Minuman extends MenuItem {

    // Atribut tambahan khusus Minuman -> Poin 8
    private String jenisMinuman; // contoh: dingin, hangat

    public Minuman(String nama, double harga, String jenisMinuman) {
        super(nama, harga, "Minuman");
        this.jenisMinuman = jenisMinuman;
    }

    // Getter/Setter Minuman -> Poin 12
    public String getJenisMinuman() {
        return jenisMinuman;
    }

    public void setJenisMinuman(String jenisMinuman) {
        this.jenisMinuman = jenisMinuman;
    }

    // Override method tampilMenu() -> Poin 17
    @Override
    public void tampilMenu() {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        System.out.printf("  %-22s %-12s %-20s%n",
                getNama(),
                formatter.format(getHarga()),
                "(Minuman - " + jenisMinuman + ")");
    }
}

// ============================================================================
// KELAS TURUNAN: DISKON
// ============================================================================

class Diskon extends MenuItem {

    // Atribut tambahan khusus Diskon -> Poin 9
    private double persentaseDiskon; // contoh: 10 berarti 10%

    public Diskon(String nama, double persentaseDiskon) {
        super(nama, 0, "Diskon"); // harga 0, karena diskon bukan item berbayar
        this.persentaseDiskon = persentaseDiskon;
    }

    // Getter/Setter Diskon -> Poin 13
    public double getPersentaseDiskon() {
        return persentaseDiskon;
    }

    public void setPersentaseDiskon(double persentaseDiskon) {
        this.persentaseDiskon = persentaseDiskon;
    }

    // Override method tampilMenu() -> Poin 18
    @Override
    public void tampilMenu() {
        System.out.printf("  %-22s %-12s %-20s%n",
                getNama(),
                persentaseDiskon + " %",
                "(Diskon)");
    }
}

// ============================================================================
// KELAS MENU (MENGELOLA DAFTAR MENU RESTORAN)
// ============================================================================

class Menu {

    // ArrayList untuk menyimpan MenuItem -> Poin 24
    private final List<MenuItem> daftarMenu = new ArrayList<>();

    // Getter/Setter Menu -> Poin 14
    public List<MenuItem> getDaftarMenu() {
        // mengembalikan shallow copy untuk menjaga enkapsulasi
        return new ArrayList<>(daftarMenu);
    }

    public void setDaftarMenu(List<MenuItem> daftarMenuBaru) {
        daftarMenu.clear();
        if (daftarMenuBaru != null) {
            daftarMenu.addAll(daftarMenuBaru);
        }
    }

    // Fungsi tambah item -> Poin 26
    public void tambahItem(MenuItem item) {
        daftarMenu.add(item);
    }

    // Fungsi tampilkan menu -> Poin 27
    public void tampilkanMenu() {
        System.out.println("=============== DAFTAR MENU RESTORAN ===============");
        System.out.printf("%-10s %-22s %-12s %-20s%n",
                "Kategori", "Nama", "Harga", "Keterangan");
        System.out.println("----------------------------------------------------");

        System.out.println("[MAKANAN]");
        for (MenuItem item : daftarMenu) {
            if (item instanceof Makanan) {
                item.tampilMenu(); // polymorphism
            }
        }

        System.out.println("\n[MINUMAN]");
        for (MenuItem item : daftarMenu) {
            if (item instanceof Minuman) {
                item.tampilMenu(); // polymorphism
            }
        }

        System.out.println("\n[DISKON]");
        for (MenuItem item : daftarMenu) {
            if (item instanceof Diskon) {
                item.tampilMenu(); // polymorphism
            }
        }

        System.out.println("====================================================");
    }

    // Mencari item menu berdasarkan nama (case-insensitive)
    // Menggunakan Exception Handling -> Poin 19
    public MenuItem cariItemByNama(String nama) throws MenuItemTidakDitemukanException {
        for (MenuItem item : daftarMenu) {
            if (item.getNama().equalsIgnoreCase(nama)) {
                return item;
            }
        }
        throw new MenuItemTidakDitemukanException(
                "Menu dengan nama '" + nama + "' tidak ditemukan.");
    }

    // Ambil semua diskon -> digunakan untuk hitung total & diskon -> Poin 29
    public List<Diskon> getDiskonTersedia() {
        List<Diskon> result = new ArrayList<>();
        for (MenuItem item : daftarMenu) {
            if (item instanceof Diskon) {
                result.add((Diskon) item);
            }
        }
        return result;
    }

    // ------------------------- Operasi File I/O (Menu) ----------------------
    // I/O: Simpan menu ke file CSV -> Poin 21
    public void simpanKeFile(String namaFile) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(namaFile))) {
            for (MenuItem item : daftarMenu) {
                if (item instanceof Makanan) {
                    Makanan m = (Makanan) item;
                    bw.write("MAKANAN," + m.getNama() + "," + m.getHarga() + "," + m.getJenisMakanan());
                } else if (item instanceof Minuman) {
                    Minuman m = (Minuman) item;
                    bw.write("MINUMAN," + m.getNama() + "," + m.getHarga() + "," + m.getJenisMinuman());
                } else if (item instanceof Diskon) {
                    Diskon d = (Diskon) item;
                    bw.write("DISKON," + d.getNama() + "," + d.getPersentaseDiskon());
                }
                bw.newLine();
            }
        }
    }

    // I/O: Load menu dari file CSV -> Poin 20
    public void muatDariFile(String namaFile) throws IOException {
        daftarMenu.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(namaFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",");
                String tipe = parts[0];

                if ("MAKANAN".equalsIgnoreCase(tipe) && parts.length >= 4) {
                    String nama = parts[1];
                    double harga = Double.parseDouble(parts[2]);
                    String jenis = parts[3];
                    tambahItem(new Makanan(nama, harga, jenis));
                } else if ("MINUMAN".equalsIgnoreCase(tipe) && parts.length >= 4) {
                    String nama = parts[1];
                    double harga = Double.parseDouble(parts[2]);
                    String jenis = parts[3];
                    tambahItem(new Minuman(nama, harga, jenis));
                } else if ("DISKON".equalsIgnoreCase(tipe) && parts.length >= 3) {
                    String nama = parts[1];
                    double persen = Double.parseDouble(parts[2]);
                    tambahItem(new Diskon(nama, persen));
                }
            }
        } catch (NumberFormatException e) {
            throw new IOException("Format angka pada file menu tidak valid: " + e.getMessage(), e);
        }
    }
}

// ============================================================================
// KELAS PESANAN (MENGELOLA PESANAN DENGAN ARRAYLIST) -> Poin 25
// ============================================================================

class Pesanan {

    // Kelas inner untuk 1 baris item pesanan
    public static class ItemPesanan {
        private final MenuItem menuItem;
        private final int jumlah;

        public ItemPesanan(MenuItem menuItem, int jumlah) {
            this.menuItem = menuItem;
            this.jumlah = jumlah;
        }

        public MenuItem getMenuItem() {
            return menuItem;
        }

        public int getJumlah() {
            return jumlah;
        }

        public double getSubtotal() {
            return menuItem.getHarga() * jumlah;
        }
    }

    // ArrayList untuk menyimpan item pesanan
    private final List<ItemPesanan> daftarItem = new ArrayList<>();

    // Diskon yang dipakai (boleh null)
    private Diskon diskonDipakai;

    // Getter/Setter Pesanan -> Poin 15
    public List<ItemPesanan> getDaftarItem() {
        return new ArrayList<>(daftarItem);
    }

    public void setDaftarItem(List<ItemPesanan> daftarItemBaru) {
        daftarItem.clear();
        if (daftarItemBaru != null) {
            daftarItem.addAll(daftarItemBaru);
        }
    }

    public Diskon getDiskonDipakai() {
        return diskonDipakai;
    }

    public void setDiskonDipakai(Diskon diskon) {
        this.diskonDipakai = diskon;
    }

    // Fungsi tambah item -> Poin 26 (di konteks Pesanan)
    public void tambahItem(MenuItem item, int jumlah) {
        daftarItem.add(new ItemPesanan(item, jumlah));
    }

    public boolean isKosong() {
        return daftarItem.isEmpty();
    }

    // Fungsi hitung total (sebelum & sesudah diskon) -> Poin 29
    public double hitungTotalSebelumDiskon() {
        double total = 0;
        for (ItemPesanan ip : daftarItem) {
            total += ip.getSubtotal();
        }
        return total;
    }

    public double hitungTotalSetelahDiskon() {
        double total = hitungTotalSebelumDiskon();
        if (diskonDipakai != null) {
            double potongan = total * (diskonDipakai.getPersentaseDiskon() / 100.0);
            total -= potongan;
        }
        return total;
    }

    // Fungsi tampilkan struk (return String) -> Poin 30 (dipakai di Main)
    public String buatStruk(NumberFormat formatter) {
        StringBuilder sb = new StringBuilder();
        sb.append("=========== STRUK PESANAN ===========\n");
        sb.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        sb.append("\n\n");
        sb.append(String.format("%-20s %5s %12s %12s%n", "Menu", "Qty", "Harga", "Subtotal"));
        sb.append("-----------------------------------------------------\n");

        for (ItemPesanan ip : daftarItem) {
            String nama = ip.getMenuItem().getNama();
            int qty = ip.getJumlah();
            double harga = ip.getMenuItem().getHarga();
            double subtotal = ip.getSubtotal();
            sb.append(String.format("%-20s %5d %12s %12s%n",
                    nama, qty, formatter.format(harga), formatter.format(subtotal)));
        }

        sb.append("-----------------------------------------------------\n");
        double totalSebelum = hitungTotalSebelumDiskon();
        sb.append(String.format("%-30s %12s%n",
                "Total Sebelum Diskon", formatter.format(totalSebelum)));

        if (diskonDipakai != null) {
            double potongan = totalSebelum * (diskonDipakai.getPersentaseDiskon() / 100.0);
            sb.append(String.format("%-30s %12s%n",
                    "Diskon " + diskonDipakai.getPersentaseDiskon() + "%", "-" + formatter.format(potongan)));
        } else {
            sb.append(String.format("%-30s %12s%n", "Diskon", formatter.format(0)));
        }

        sb.append(String.format("%-30s %12s%n",
                "Total Bayar", formatter.format(hitungTotalSetelahDiskon())));
        sb.append("=====================================\n");
        return sb.toString();
    }

    // I/O: Simpan struk ke file TXT -> Poin 22
    public void simpanStrukKeFile(String namaFile, NumberFormat formatter) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(namaFile))) {
            bw.write(buatStruk(formatter));
        }
    }
}

// ============================================================================
// CUSTOM EXCEPTION: MENUITEMTIDAKDITEMUKANEXCEPTION -> Poin 19
// ============================================================================

class MenuItemTidakDitemukanException extends Exception {
    public MenuItemTidakDitemukanException(String message) {
        super(message);
    }
}
