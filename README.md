# Implementation of Galaxio Bot Using Greedy Algorithm
> Source Code ini dibuat oleh kami, Kelompok 2B1Reuni, untuk memenuhi Tugas Besar 1 Strategi Algoritma yaitu mengimplementasikan 
> Bot Galaxio dengan Menggunakan Algoritma Greedy

## Daftar Isi
* [Author](#author)
* [Penjelasan Algoritma](#penjelasan-algoritma)
* [Implementasi Program](#implementasi-program)
* [Sistematika File](#sistematika-file)
* [Requirements](#requirements)
* [Cara Menjalankan Program](#cara-menjalankan-program)

## Author
NIM | Nama |
--- | --- |
13521071 | Margaretha Olivia Haryono |
13521084 | Austin Gabriel Pardosi |
13521108 | Michael Leon Putra Widhi

## Penjelasan Algoritma
Dalam permainan ini, penulis mengimplementasikan beberapa algoritma greedy dalam pembuatan bot.  Terdapat enam upa-strategi greedy serta satu algoritma greedy utama yang diimplementasikan. Keenam strategi greedy yang kami buat yaitu sebagai berikut.
1. Strategi Greedy untuk Memperoleh Food dan SuperFood </br>
Strategi ini digunakan untuk melakukan implementasi skema pencarian makanan.
2. Strategi Greedy untuk Menggunakan Torpedo Salvo </br>
Strategi ini digunakan untuk menembakkan torpedo salvo.
3. Strategi Greedy untuk Menggunakan Teleporter </br>
Strategi ini digunakan untuk menembakkan teleporter.
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
* Java Virtual Machine (JVM) versi 11 atau lebih baru.
* IntelliJ IDEA versi 2021.3 atau lebih baru.
* Apache Maven 3.8.4

## Cara Menjalankan Program
....