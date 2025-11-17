// Nama  : M. RIDUAN
// NIM   : 053660074
// Kelas : Tugas Praktik 2 - Aplikasi Restoran Sederhana

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Scanner;

// Kelas utama aplikasi
public class Main {

    // Formatter untuk menampilkan angka dalam format Rupiah
    private static final NumberFormat RP = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));

    // Batas maksimal jumlah menu dan pesanan (array statis)
    private static final int MAX_MENU = 100;
    private static final int MAX_PESANAN = 100;

    // Konstanta untuk biaya layanan dan tarif pajak
    private static final int BIAYA_LAYANAN = 20000;
    private static final double TARIF_PAJAK = 0.10;

    // Array untuk menyimpan menu restoran
    private static Menu[] daftarMenu = new Menu[MAX_MENU];
    private static int jumlahMenu = 0;              // jumlah menu aktif

    // Array untuk menyimpan pesanan pelanggan (kode menu dan jumlah)
    private static int[] kodePesan = new int[MAX_PESANAN];
    private static int[] jumlahPesan = new int[MAX_PESANAN];
    private static int jumlahEntriPesanan = 0;      // jumlah entri pesanan aktif

    // Scanner tunggal untuk membaca input
    private static Scanner input = new Scanner(System.in);

    // Titik masuk program
    public static void main(String[] args) {

        // Mengisi menu awal (minimal 4 makanan dan 4 minuman)
        inisialisasiMenuAwal();

        // Loop utama untuk menu aplikasi
        boolean jalan = true;
        while (jalan) {
            System.out.println("============================================");
            System.out.println("         RM BANJAR NYAMAN APP");
            System.out.println("    Cita Rasa Banua - Nyaman di Perut");
            System.out.println("============================================");
            System.out.println("1. Menu Pelanggan (Pemesanan)");
            System.out.println("2. Menu Pengelola (Manajemen Menu)");
            System.out.println("3. Keluar");
            System.out.print("Pilih menu [1-3]: ");

            String pilih = input.nextLine().trim();

            // Struktur keputusan untuk navigasi menu utama
            switch (pilih) {
                case "1":
                    menuPelanggan();    // Masuk ke alur pemesanan pelanggan
                    break;
                case "2":
                    menuPengelola();    // Masuk ke alur manajemen menu
                    break;
                case "3":
                    System.out.println("Terima kasih telah menggunakan aplikasi.");
                    jalan = false;      // Keluar dari loop utama
                    break;
                default:
                    System.out.println("Pilihan tidak dikenal, silakan coba lagi.\n");
            }
        }
    }

    // ========================= INISIALISASI MENU AWAL =========================

    // Mengisi daftar menu awal restoran
    private static void inisialisasiMenuAwal() {
        // Menambahkan beberapa menu makanan khas Banjar
        tambahMenuBaru(new Menu(101, "Ketupat Kandangan", Kategori.MAKANAN, 30000));
        tambahMenuBaru(new Menu(102, "Soto Banjar", Kategori.MAKANAN, 25000));
        tambahMenuBaru(new Menu(103, "Lontong Orari", Kategori.MAKANAN, 28000));
        tambahMenuBaru(new Menu(104, "Ayam Masak Habang", Kategori.MAKANAN, 32000));
        tambahMenuBaru(new Menu(105, "Gangan Asam Banjar", Kategori.MAKANAN, 27000));
        tambahMenuBaru(new Menu(106, "Nasi Sop Banjar", Kategori.MAKANAN, 23000));
        tambahMenuBaru(new Menu(107, "Sate Ayam Banjar", Kategori.MAKANAN, 35000));

        // Menambahkan minimal 4 menu minuman
        tambahMenuBaru(new Menu(201, "Es Sarang Burung", Kategori.MINUMAN, 10000));
        tambahMenuBaru(new Menu(202, "Es Selasih", Kategori.MINUMAN, 9000));
        tambahMenuBaru(new Menu(203, "Teh Tarik Banjar", Kategori.MINUMAN, 12000));
        tambahMenuBaru(new Menu(204, "Air Mineral", Kategori.MINUMAN, 6000));
    }

    // Menambahkan objek Menu ke array daftarMenu
    private static void tambahMenuBaru(Menu m) {
        if (jumlahMenu < MAX_MENU) {
            daftarMenu[jumlahMenu] = m;
            jumlahMenu++;
        } else {
            System.out.println("Kapasitas menu penuh, tidak bisa menambah menu baru.");
        }
    }

    // ========================= MENU PELANGGAN / PEMESANAN =========================

    // Mengelola alur pemesanan pelanggan
    private static void menuPelanggan() {
        // Reset pesanan setiap kali pelanggan baru
        jumlahEntriPesanan = 0;

        System.out.println("\n===== MENU PELANGGAN (PEMESANAN) =====");
        tampilkanMenuPerKategori();   // Menampilkan daftar menu

        System.out.println("Silakan masukkan pesanan Anda.");
        System.out.println("Ketik kode menu sesuai daftar.");
        System.out.println("Ketik 'selesai' jika sudah selesai memesan.\n");

        // Loop untuk menerima input pesanan hingga pelanggan mengetik 'selesai'
        while (jumlahEntriPesanan < MAX_PESANAN) {
            System.out.print("Masukkan kode menu (atau 'selesai'): ");
            String kodeInput = input.nextLine().trim();

            // Jika pelanggan mengetik 'selesai', keluar dari loop pemesanan
            if (kodeInput.equalsIgnoreCase("selesai")) {
                break;
            }

            int kodeMenu;
            try {
                // Konversi input menjadi angka
                kodeMenu = Integer.parseInt(kodeInput);
            } catch (NumberFormatException e) {
                System.out.println("Input bukan angka kode menu, silakan ulangi.\n");
                continue;
            }

            // Cari menu berdasarkan kode
            Menu dipilih = cariByKode(kodeMenu);
            if (dipilih == null) {
                // Jika kode menu tidak ditemukan di daftar menu
                System.out.println("Kode menu tidak ditemukan, silakan coba lagi.\n");
                continue;
            }

            // Meminta jumlah pesanan untuk menu tersebut
            System.out.print("Masukkan jumlah pesanan untuk " + dipilih.nama + ": ");
            String jumlahStr = input.nextLine().trim();
            int qty;
            try {
                qty = Integer.parseInt(jumlahStr);
            } catch (NumberFormatException e) {
                System.out.println("Jumlah tidak valid, silakan ulangi.\n");
                continue;
            }

            if (qty <= 0) {
                System.out.println("Jumlah harus lebih dari 0, silakan ulangi.\n");
                continue;
            }

            // Simpan kode menu dan jumlah ke array pesanan
            kodePesan[jumlahEntriPesanan] = kodeMenu;
            jumlahPesan[jumlahEntriPesanan] = qty;
            jumlahEntriPesanan++;

            System.out.println(">> Pesanan " + dipilih.nama + " x" + qty + " berhasil ditambahkan.\n");
        }

        // Jika tidak ada pesanan, langsung kembali
        if (jumlahEntriPesanan == 0) {
            System.out.println("Tidak ada pesanan yang tercatat.\n");
            return;
        }

        // Setelah input selesai, berikan kesempatan untuk mengelola pesanan
        kelolaPesananSebelumBayar();
    }

    // Menampilkan menu dengan pengelompokan Makanan dan Minuman
    private static void tampilkanMenuPerKategori() {
        System.out.println("===== DAFTAR MENU RM BANJAR NYAMAN =====");
        System.out.printf("%-6s %-24s %-10s %12s%n", "KODE", "NAMA", "KATEGORI", "HARGA");
        System.out.println("----------------------------------------------------------");

        // Tampilkan semua menu kategori Makanan
        for (int i = 0; i < jumlahMenu; i++) {
            Menu m = daftarMenu[i];
            if (m.kategori == Kategori.MAKANAN) {
                System.out.printf("%-6d %-24s %-10s %12s%n",
                        m.kode, m.nama, m.kategori.label, RP.format(m.harga));
            }
        }

        System.out.println("----------------------------------------------------------");

        // Tampilkan semua menu kategori Minuman
        for (int i = 0; i < jumlahMenu; i++) {
            Menu m = daftarMenu[i];
            if (m.kategori == Kategori.MINUMAN) {
                System.out.printf("%-6d %-24s %-10s %12s%n",
                        m.kode, m.nama, m.kategori.label, RP.format(m.harga));
            }
        }
        System.out.println("----------------------------------------------------------\n");
    }

    // Menampilkan daftar pesanan sementara (keranjang)
    private static void tampilkanPesananSederhana() {
        System.out.println("\n===== DAFTAR PESANAN SAAT INI =====");
        if (jumlahEntriPesanan == 0) {
            System.out.println("Belum ada pesanan.");
            return;
        }
        System.out.printf("%-6s %-24s %5s %12s%n", "KODE", "NAMA", "QTY", "SUBTOTAL");
        System.out.println("--------------------------------------------------");
        for (int i = 0; i < jumlahEntriPesanan; i++) {
            Menu m = cariByKode(kodePesan[i]);
            int qty = jumlahPesan[i];
            int sub = m.harga * qty;
            System.out.printf("%-6d %-24s %5d %12s%n", m.kode, m.nama, qty, RP.format(sub));
        }
        System.out.println("--------------------------------------------------\n");
    }

    // Sub-menu untuk mengelola pesanan sebelum cetak struk
    private static void kelolaPesananSebelumBayar() {
        boolean kembali = false;
        while (!kembali) {
            System.out.println("===== KELOLA PESANAN =====");
            System.out.println("1. Lihat Pesanan");
            System.out.println("2. Hapus Salah Satu Pesanan");
            System.out.println("3. Ubah Jumlah Salah Satu Pesanan");
            System.out.println("4. Lanjut ke Pembayaran & Cetak Struk");
            System.out.println("5. Batalkan Semua Pesanan");
            System.out.print("Pilih menu [1-5]: ");

            String pilih = input.nextLine().trim();

            // Struktur keputusan untuk mengelola pesanan
            switch (pilih) {
                case "1":
                    tampilkanPesananSederhana();   // Lihat isi keranjang
                    break;
                case "2":
                    hapusPesanan();                // Hapus salah satu item pesanan
                    break;
                case "3":
                    ubahJumlahPesanan();           // Ubah jumlah salah satu item
                    break;
                case "4":
                    // Cetak struk dan keluar dari pengelolaan pesanan
                    hitungTotalDanCetakStruk();
                    kembali = true;
                    break;
                case "5":
                    // Batalkan seluruh pesanan
                    if (konfirmasiYa("Yakin batalkan semua pesanan? (Ya/Tidak): ")) {
                        jumlahEntriPesanan = 0;
                        System.out.println("Semua pesanan dibatalkan.\n");
                        kembali = true;
                    }
                    break;
                default:
                    System.out.println("Pilihan tidak dikenal, silakan ulangi.\n");
            }

            // Jika semua pesanan habis, otomatis keluar
            if (jumlahEntriPesanan == 0 && !kembali) {
                System.out.println("Tidak ada pesanan tersisa. Kembali ke menu utama.\n");
                kembali = true;
            }
        }
    }

    // Menghapus salah satu pesanan dari keranjang
    private static void hapusPesanan() {
        if (jumlahEntriPesanan == 0) {
            System.out.println("Belum ada pesanan untuk dihapus.\n");
            return;
        }
        tampilkanPesananSederhana();
        System.out.print("Masukkan KODE menu yang akan dihapus dari pesanan: ");
        String kodeStr = input.nextLine().trim();
        int kode;
        try {
            kode = Integer.parseInt(kodeStr);
        } catch (NumberFormatException e) {
            System.out.println("Kode harus berupa angka. Proses dibatalkan.\n");
            return;
        }

        // Cari indeks pesanan dengan kode tersebut
        int indexHapus = -1;
        for (int i = 0; i < jumlahEntriPesanan; i++) {
            if (kodePesan[i] == kode) {
                indexHapus = i;
                break;
            }
        }

        if (indexHapus == -1) {
            System.out.println("Pesanan dengan kode tersebut tidak ditemukan.\n");
            return;
        }

        Menu m = cariByKode(kodePesan[indexHapus]);
        System.out.println("Pesanan yang akan dihapus: " + m.nama + " x" + jumlahPesan[indexHapus]);

        // Konfirmasi sebelum menghapus
        if (konfirmasiYa("Yakin ingin menghapus pesanan ini? (Ya/Tidak): ")) {
            // Geser elemen array ke kiri untuk menutup celah
            for (int i = indexHapus; i < jumlahEntriPesanan - 1; i++) {
                kodePesan[i] = kodePesan[i + 1];
                jumlahPesan[i] = jumlahPesan[i + 1];
            }
            jumlahEntriPesanan--;
            System.out.println("Pesanan berhasil dihapus.\n");
        } else {
            System.out.println("Penghapusan pesanan dibatalkan.\n");
        }
    }

    // Mengubah jumlah salah satu pesanan
    private static void ubahJumlahPesanan() {
        if (jumlahEntriPesanan == 0) {
            System.out.println("Belum ada pesanan untuk diubah.\n");
            return;
        }
        tampilkanPesananSederhana();
        System.out.print("Masukkan KODE menu yang jumlah pesanannya akan diubah: ");
        String kodeStr = input.nextLine().trim();
        int kode;
        try {
            kode = Integer.parseInt(kodeStr);
        } catch (NumberFormatException e) {
            System.out.println("Kode harus berupa angka. Proses dibatalkan.\n");
            return;
        }

        // Cari indeks pesanan dengan kode tersebut
        int indexEdit = -1;
        for (int i = 0; i < jumlahEntriPesanan; i++) {
            if (kodePesan[i] == kode) {
                indexEdit = i;
                break;
            }
        }

        if (indexEdit == -1) {
            System.out.println("Pesanan dengan kode tersebut tidak ditemukan.\n");
            return;
        }

        Menu m = cariByKode(kodePesan[indexEdit]);
        System.out.println("Pesanan saat ini: " + m.nama + " x" + jumlahPesan[indexEdit]);
        System.out.print("Masukkan jumlah baru: ");
        String qtyStr = input.nextLine().trim();
        int qtyBaru;
        try {
            qtyBaru = Integer.parseInt(qtyStr);
        } catch (NumberFormatException e) {
            System.out.println("Jumlah harus berupa angka. Proses dibatalkan.\n");
            return;
        }

        if (qtyBaru <= 0) {
            System.out.println("Jumlah harus lebih dari 0. Jika ingin menghapus, gunakan menu hapus pesanan.\n");
            return;
        }

        // Konfirmasi sebelum mengubah jumlah
        if (konfirmasiYa("Yakin ingin mengubah jumlah pesanan ini? (Ya/Tidak): ")) {
            jumlahPesan[indexEdit] = qtyBaru;
            System.out.println("Jumlah pesanan berhasil diubah.\n");
        } else {
            System.out.println("Perubahan jumlah pesanan dibatalkan.\n");
        }
    }

    // ========================= PERHITUNGAN & CETAK STRUK =========================

    // Menghitung total biaya dan mencetak struk pesanan
    private static void hitungTotalDanCetakStruk() {
        int subtotal = 0;                        // total harga sebelum diskon
        int diskonMinuman = 0;                  // diskon promo minuman
        int hargaMinumanTermurah = Integer.MAX_VALUE;
        boolean adaMinuman = false;             // penanda apakah ada minuman

        System.out.println("\n========== STRUK PEMBAYARAN ==========");
        System.out.printf("%-24s %5s %15s%n", "Item", "Qty", "Subtotal");
        System.out.println("----------------------------------------------");

        // Loop untuk menampilkan setiap item pesanan dan menjumlah subtotal
        for (int i = 0; i < jumlahEntriPesanan; i++) {
            Menu m = cariByKode(kodePesan[i]);
            int qty = jumlahPesan[i];
            int sub = m.harga * qty;
            subtotal += sub;

            System.out.printf("%-24s %5d %15s%n", m.nama, qty, RP.format(sub));

            // Cek apakah item ini kategori Minuman untuk promo
            if (m.kategori == Kategori.MINUMAN) {
                adaMinuman = true;
                if (m.harga < hargaMinumanTermurah) {
                    hargaMinumanTermurah = m.harga;
                }
            }
        }

        // Diskon 10% jika subtotal > 100.000
        double diskon10 = 0;
        if (subtotal > 100000) {
            diskon10 = 0.10 * subtotal;
        }

        // Promo beli 1 gratis 1 untuk minuman jika subtotal > 50.000
        if (subtotal > 50000 && adaMinuman) {
            diskonMinuman = hargaMinumanTermurah;   // minuman termurah digratiskan
        }

        // Total diskon (dibulatkan ke int)
        int totalDiskon = (int) (diskon10 + diskonMinuman);

        // Dasar pajak = subtotal - diskon
        int dasarPajak = subtotal - totalDiskon;
        if (dasarPajak < 0) dasarPajak = 0;

        // Pajak 10% dari dasar pajak
        int pajak = (int) (dasarPajak * TARIF_PAJAK);

        // Total akhir yang dibayar = dasar pajak + pajak + biaya layanan
        int totalAkhir = dasarPajak + pajak + BIAYA_LAYANAN;

        // Menampilkan ringkasan perhitungan
        System.out.println("----------------------------------------------");
        System.out.printf("%-30s %15s%n", "Subtotal", RP.format(subtotal));
        System.out.printf("%-30s %15s%n", "Diskon 10% (>100k)",
                (diskon10 > 0 ? "-" + RP.format((int) diskon10) : "-Rp0"));
        System.out.printf("%-30s %15s%n", "Promo Minuman (>50k)",
                (diskonMinuman > 0 ? "-" + RP.format(diskonMinuman) : "-Rp0"));
        System.out.printf("%-30s %15s%n", "Pajak 10%", RP.format(pajak));
        System.out.printf("%-30s %15s%n", "Biaya Layanan", RP.format(BIAYA_LAYANAN));
        System.out.println("----------------------------------------------");
        System.out.printf("%-30s %15s%n", "TOTAL BAYAR", RP.format(totalAkhir));
        System.out.println("==============================================");
        System.out.println("Terima kasih! Selamat menikmati :)\n");
    }

    // ========================= MENU PENGELOLA / OWNER =========================

    // Menu untuk pemilik restoran mengelola data menu
    private static void menuPengelola() {
        boolean kembali = false;
        do {
            System.out.println("\n===== MENU PENGELOLA RESTORAN =====");
            System.out.println("1. Lihat Daftar Menu");
            System.out.println("2. Tambah Menu Baru");
            System.out.println("3. Ubah Harga Menu");
            System.out.println("4. Hapus Menu");
            System.out.println("5. Kembali ke Menu Utama");
            System.out.print("Pilih menu [1-5]: ");

            String pilih = input.nextLine().trim();

            // Struktur keputusan menu pengelola
            switch (pilih) {
                case "1":
                    tampilkanMenuPerKategori();
                    break;
                case "2":
                    tambahMenuDariPengelola();
                    break;
                case "3":
                    ubahHargaMenu();
                    break;
                case "4":
                    hapusMenu();
                    break;
                case "5":
                    kembali = true;
                    break;
                default:
                    System.out.println("Pilihan tidak dikenal, silakan ulangi.\n");
            }
        } while (!kembali);  // do-while agar minimal tampil sekali
    }

    // Menambahkan beberapa menu baru sekaligus oleh pengelola
    private static void tambahMenuDariPengelola() {
        System.out.println("\n=== TAMBAH MENU BARU ===");
        boolean lagi = true;

        // Loop agar pengelola bisa menambah beberapa menu sekaligus
        while (lagi && jumlahMenu < MAX_MENU) {
            System.out.print("Masukkan kode menu (angka unik): ");
            String kodeStr = input.nextLine().trim();
            int kode;
            try {
                kode = Integer.parseInt(kodeStr);
            } catch (NumberFormatException e) {
                System.out.println("Kode harus berupa angka. Proses dibatalkan.\n");
                break;
            }

            // Cek apakah kode sudah digunakan
            if (cariByKode(kode) != null) {
                System.out.println("Kode sudah digunakan oleh menu lain. Proses dibatalkan.\n");
                break;
            }

            System.out.print("Masukkan nama menu        : ");
            String nama = input.nextLine();

            System.out.print("Masukkan kategori (1=Makanan, 2=Minuman): ");
            String pilihKat = input.nextLine().trim();
            Kategori kategori;
            if (pilihKat.equals("1")) {
                kategori = Kategori.MAKANAN;
            } else if (pilihKat.equals("2")) {
                kategori = Kategori.MINUMAN;
            } else {
                System.out.println("Kategori tidak valid. Proses dibatalkan.\n");
                break;
            }

            System.out.print("Masukkan harga (dalam Rupiah): ");
            String hargaStr = input.nextLine().trim();
            int harga;
            try {
                harga = Integer.parseInt(hargaStr);
            } catch (NumberFormatException e) {
                System.out.println("Harga harus berupa angka. Proses dibatalkan.\n");
                break;
            }

            // Konfirmasi sebelum benar-benar menambahkan menu
            if (konfirmasiYa("Apakah Anda yakin ingin menambahkan menu ini? (Ya/Tidak): ")) {
                tambahMenuBaru(new Menu(kode, nama, kategori, harga));
                System.out.println("Menu baru berhasil ditambahkan.\n");
            } else {
                System.out.println("Penambahan menu dibatalkan.\n");
            }

            // Tanya apakah ingin menambah menu lagi
            lagi = konfirmasiYa("Tambah menu lagi? (Ya/Tidak): ");
        }

        if (jumlahMenu >= MAX_MENU) {
            System.out.println("Kapasitas menu sudah penuh. Tidak bisa menambah menu lagi.\n");
        }
    }

    // Mengubah harga salah satu menu
    private static void ubahHargaMenu() {
        System.out.println("\n=== UBAH HARGA MENU ===");
        tampilkanMenuPerKategori();

        System.out.print("Masukkan kode menu yang akan diubah: ");
        String kodeStr = input.nextLine().trim();
        int kode;
        try {
            kode = Integer.parseInt(kodeStr);
        } catch (NumberFormatException e) {
            System.out.println("Kode harus berupa angka. Proses dibatalkan.\n");
            return;
        }

        Menu m = cariByKode(kode);
        if (m == null) {
            System.out.println("Menu dengan kode tersebut tidak ditemukan.\n");
            return;
        }

        System.out.println("Menu dipilih: " + m.nama + " (" + m.kategori.label + "), harga saat ini: " + RP.format(m.harga));
        System.out.print("Masukkan harga baru: ");
        String hargaStr = input.nextLine().trim();
        int hargaBaru;
        try {
            hargaBaru = Integer.parseInt(hargaStr);
        } catch (NumberFormatException e) {
            System.out.println("Harga harus berupa angka. Proses dibatalkan.\n");
            return;
        }

        if (konfirmasiYa("Yakin ingin mengubah harga menu ini? (Ya/Tidak): ")) {
            m.harga = hargaBaru;
            System.out.println("Harga menu berhasil diubah.\n");
        } else {
            System.out.println("Perubahan harga dibatalkan.\n");
        }
    }

    // Menghapus salah satu menu dari daftar menu
    private static void hapusMenu() {
        System.out.println("\n=== HAPUS MENU ===");
        tampilkanMenuPerKategori();

        System.out.print("Masukkan kode menu yang akan dihapus: ");
        String kodeStr = input.nextLine().trim();
        int kode;
        try {
            kode = Integer.parseInt(kodeStr);
        } catch (NumberFormatException e) {
            System.out.println("Kode harus berupa angka. Proses dibatalkan.\n");
            return;
        }

        int indexHapus = -1;
        for (int i = 0; i < jumlahMenu; i++) {
            if (daftarMenu[i].kode == kode) {
                indexHapus = i;
                break;
            }
        }

        if (indexHapus == -1) {
            System.out.println("Menu dengan kode tersebut tidak ditemukan.\n");
            return;
        }

        Menu m = daftarMenu[indexHapus];
        System.out.println("Menu yang akan dihapus: " + m.nama + " (" + m.kategori.label + "), harga: " + RP.format(m.harga));

        if (konfirmasiYa("Apakah Anda yakin ingin menghapus menu ini? (Ya/Tidak): ")) {
            // Geser array ke kiri untuk menutup celah
            for (int i = indexHapus; i < jumlahMenu - 1; i++) {
                daftarMenu[i] = daftarMenu[i + 1];
            }
            daftarMenu[jumlahMenu - 1] = null;
            jumlahMenu--;

            System.out.println("Menu berhasil dihapus.\n");
        } else {
            System.out.println("Penghapusan menu dibatalkan.\n");
        }
    }

    // ========================= METHOD BANTUAN LAIN =========================

    // Mencari menu berdasarkan kode
    private static Menu cariByKode(int kode) {
        for (int i = 0; i < jumlahMenu; i++) {
            if (daftarMenu[i].kode == kode) {
                return daftarMenu[i];
            }
        }
        return null;
    }

    // Konfirmasi Ya/Tidak dari pengguna
    private static boolean konfirmasiYa(String pesan) {
        while (true) {
            System.out.print(pesan);
            String jawab = input.nextLine().trim().toLowerCase();
            if (jawab.equals("ya") || jawab.equals("y")) {
                return true;
            } else if (jawab.equals("tidak") || jawab.equals("t")) {
                return false;
            } else {
                System.out.println("Masukkan hanya 'Ya' atau 'Tidak'.");
            }
        }
    }
}

// Enum untuk kategori menu
enum Kategori {
    MAKANAN("Makanan"),
    MINUMAN("Minuman");

    public final String label;   // label tampilan kategori

    Kategori(String l) {
        this.label = l;
    }
}

// Kelas Menu sebagai representasi data menu restoran
class Menu {
    int kode;           // kode unik menu
    String nama;        // nama menu
    Kategori kategori;  // kategori (Makanan / Minuman)
    int harga;          // harga per porsi

    // Konstruktor untuk menginisialisasi atribut menu
    Menu(int kode, String nama, Kategori kategori, int harga) {
        this.kode = kode;
        this.nama = nama;
        this.kategori = kategori;
        this.harga = harga;
    }
}


// Link Youtube: 
// Link Repositori:   