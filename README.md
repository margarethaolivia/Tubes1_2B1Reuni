# Implementation of Galaxio Bot Using Greedy Algorithm

> Source Code ini dibuat oleh kami, Kelompok 2B1Reuni, untuk memenuhi Tugas Besar 1 Strategi Algoritma yaitu mengimplementasikan
> Bot Galaxio dengan Menggunakan Algoritma Greedy

## Daftar Isi

- [Author](#author)
- [Penjelasan Algoritma](#penjelasan-algoritma)
- [Implementasi Program](#implementasi-program)
- [Sistematika File](#sistematika-file)
- [Requirements](#requirements)
- [Cara Menjalankan Program](#cara-menjalankan-program)

## Author

| NIM      | Nama                      |
| -------- | ------------------------- |
| 13521071 | Margaretha Olivia Haryono |
| 13521084 | Austin Gabriel Pardosi    |
| 13521108 | Michael Leon Putra Widhi  |

## Penjelasan Algoritma

Dalam permainan ini, penulis mengimplementasikan beberapa algoritma greedy dalam pembuatan bot. Terdapat enam upa-strategi greedy serta satu algoritma greedy utama yang diimplementasikan. Keenam strategi greedy yang kami buat yaitu sebagai berikut.

1. Strategi Greedy untuk Memperoleh Food dan SuperFood </br>
   Strategi ini digunakan untuk melakukan implementasi skema pencarian makanan.
2. Strategi Greedy untuk Menggunakan Torpedo Salvo </br>
   Strategi ini digunakan untuk menembakkan torpedo salvo ketika ukuran bot cukup dan terdapat bot yang dekat sehingga dimungkinkan untuk menembakkan torpedo salvo terhadap bot lawan tersebut.
3. Strategi Greedy untuk Menggunakan Teleporter </br>
   Strategi ini digunakan untuk menembakkan teleporter ketika ukuran bot cukup dan terdapat bot yang dekat sehingga dimungkinkan untuk menembakkan teleporter terhadap bot lawan tersebut.
4. Strategi Greedy untuk Menghindari Gas Clouds </br>
   Strategi ini dijalankan untuk menghindari gas clouds yang terdapat dalam peta, sehingga ketika bot berjalan menuju gas clouds, bot akan membelokkan arah geraknya.
5. Strategi Greedy untuk Menghindari Edge Map </br>
   Ketika bot berada dekat dengan ujung (lingkaran peta), bot akan mengarahkan kapalnya ke arah tengah peta sehingga tidak keluar dari jalur peta.
6. Strategi Greedy untuk Menggunakan Shield </br>
   Strategi dalam penggunaan shield ini menangani dua buah kasus, yaitu teleporter serta torpedo salvo. Pada intinya kedua kasus ini menerapkan teknik yang sama, yaitu menggunakan hukum kinematika 1 dimensi untuk menyalakan shield perlindungan pada waktu yang tepat.

## Implementasi Program

Pada Tugas Besar kali ini, kami berhasil mengimplementasikan fungsionalitas dari bot pada permainan Galaxio menggunakan Algoritma Greedy. Berikut adalah daftar implementasi fitur :

1. `BotService.java` Bot Service Java
2. `KejarMusuh.java` Skema Pengejaran Musuh
3. `Makan.java` Skema Pengejaran Makan

## Sistematika File

```bash
.
├───doc
├───src
│   └───main
│       └───java
│           ├───Enums
│           ├───Models
│           └───Services
└───target
    ├───classes
    │   ├───Enums
    │   ├───Models
    │   └───Services
    ├───generated-sources
    │   └───annotations
    ├───libs
    ├───maven-archiver
    └───maven-status
        └───maven-compiler-plugin
            └───compile
                └───default-compile
```

## Requirements

- Java Virtual Machine (JVM) versi 11 atau lebih baru.
- IntelliJ IDEA versi 2021.3 atau lebih baru.
- Apache Maven 3.8.4

## Cara Menjalankan Program

1. Lakukan konfigurasi jumlah bot yang ingin dimainkan pada file JSON `appsettings.json` dalam folder `runner-publish` dan `engine-publish`
2. Buka terminal baru pada folder runner-publish.
3. Jalankan runner menggunakan perintah `dotnet GameRunner.dll`
4. Buka terminal baru pada folder engine-publish
5. Jalankan engine menggunakan perintah `dotnet Engine.dll`
6. Buka terminal baru pada folder logger-publish
7. Jalankan engine menggunakan perintah `dotnet Logger.dll`
8. Jalankan seluruh bot yang ingin dimainkan
9. Setelah permainan selesai, riwayat permainan akan tersimpan pada 2 file JSON `GameStateLog\_{Timestamp}` dalam folder `logger-publish`. Kedua file tersebut diantaranya GameComplete (hasil akhir dari permainan) dan proses dalam permainan tersebut.

Cara lain menjalankan program yaitu dengan membuat file `run.sh` pada Linux atau `run.bat` pada Windows.
