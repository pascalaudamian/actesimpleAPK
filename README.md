# ActeSimple - Android Administrative Guide (RO / EN)

Ghidul tău nativ prin administrația și birocrația din România, rescris ca aplicație Android nativă în **Kotlin** și **Jetpack Compose** cu arhitectură modernă **Material 3**.

---

## 🇷🇴 Prezentare Generală (RO)
ActeSimple ajută cetățenii români și rezidenții internaționali (UE/SEE și Non-UE) să navigheze clar și rapid prin procedurile administrației publice din România:
- **Checklist interactiv pentru dosar**: bifează fiecare act pregătit, adaugă documente suplimentare personalizate și notează data/locația programării la ghișeu.
- **Căutare inteligentă cu sinonime & diacritice**: găsești instant procedurile chiar dacă tastezi cu sau fără diacritice (ex: *buletin*, *talon*, *cazier online*, *viza flotant*, *rezidență UE*).
- **Asistent de orientare în 3 pași (Wizard)**: recomandări personalizate în funcție de cetățenie (Română, UE, Non-UE) și scopul demersului.
- **Catalog verificat editorial**: taxe exacte (fără comisioane), termene de eliberare și linkuri directe către platformele oficiale (HUB MAI, Ghișeul.ro, DGPCI, IGI, ONRC, ANAF).
- **Suport bilingv complet (Română / Engleză)**: comutare instantanee dintr-un singur buton.

---

## 🇬🇧 Overview (EN)
ActeSimple is a bilingual civic guide for navigating public administration procedures in Romania:
- **Interactive Dossier Checklist**: check off prepared papers, add custom required forms, track appointment dates & counter addresses.
- **Search with Synonyms & Diacritics**: instant matching with phonetic expansions (*buletin -> national ID card*, *cazier -> criminal record*, *rezidenta -> EU registration*).
- **3-Step Orientation Wizard**: tailored guides based on citizenship status (Romanian, EU/EEA, Non-EU) and purpose of stay.
- **Verified Official Portals**: direct shortcuts to official portals (HUB MAI, Ghiseul.ro, DGPCI, IGI, ONRC, ANAF).
- **Full Bilingual Support**: instant RO/EN language toggle.

---

## 📱 Arhitectură & Tehnologii Android

- **Limbaj:** Kotlin 2.0+
- **Interfață UI:** Jetpack Compose cu Material Design 3 (M3)
- **Design:** Edge-to-Edge (`enableEdgeToEdge()`) cu temă dinamică adaptivă
- **Iconiță Aplicație:** Adaptive Launcher Icon personalizată cu stema ActeSimple
- **Arhitectură:** Clean Architecture / MVVM (`ActeSimpleViewModel`, `StateFlow`)
- **Persistență Locală:** `ChecklistPreferences` bazat pe SharedPreferences și `kotlinx.serialization`
- **Sistem de Build:** Gradle (Kotlin DSL - `build.gradle.kts` + Version Catalog `libs.versions.toml`)
