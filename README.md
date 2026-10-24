# ActeSimple — Ghid Administrativ Civic pentru România (RO / EN)

Aplicație Android nativă de asistență administrativă și informare civică, dezvoltată în **Kotlin** și **Jetpack Compose (Material 3)**.

---

## 🇷🇴 Prezentare Generală (RO)

**ActeSimple** este o inițiativă civică independentă, fără afiliere guvernamentală, creată pentru a ajuta cetățenii români, cetățenii UE/SEE/Elveția și cetățenii non-UE să navigheze clar și rapid prin procedurile administrației publice din România.

Aplicația respectă principiul **Privacy by Design**: nu solicită și nu reține CNP, serii de buletin, parole sau documente confidențiale scanate.

### 🧭 Cele 7 Secțiuni Principale de Navigare
1. **Acasă (Home):** Căutare rapidă cu rezoluție fonetică de sinonime și diacritice, comenzi rapide către asistent, categorii populare, ecusoane de timp estimat (`~X min`), dosare în lucru și avertisment civic.
2. **Explorează (Explore Procedures):** Catalog exhaustiv cu filtre avansate pe profiluri de cetățenie (Români, UE/SEE, Non-UE, Membri de familie), dificultate, disponibilitate depunere online și categorii.
3. **Asistent de Orientare (Orientation Wizard):** Chestionar în 3 pași determinist care recomandă procedurile adecvate, explică motivul recomandării (*rationale*), afișează taxele și actele necesare fără a solicita date cu caracter personal.
4. **Dosarele Mele (My Checklist):** Manager complet de pregătire a documentelor pentru fiecare procedură: bifează actele, adaugă documente personalizate, notează data/locul programării și exportă sumarul prin copiere în clipboard.
5. **Ghiduri Salvate (Saved Guides):** Acces offline instantaneu la procedurile favorite salvate local.
6. **Mementouri Termene (Reminders):** Programare de notificări Android locale pentru expirarea actelor (buletin, permis, viză, reședință), cu presetări rapide la 90, 60, 30 și 7 zile înainte de scadență.
7. **Setări & Transparență Civică (Settings):** Comutare limbă RO/EN, directorul de portaluri guvernamentale oficiale verificate, avertisment de independență civică, raport de fezabilitate ROeID, formular de sesizare modificări legislative și ștergerea integrală a datelor locale.

---

## 🇬🇧 Overview (EN)

**ActeSimple** is an independent civic mobile application designed to help Romanian citizens, EU/EEA/Swiss citizens, and third-country nationals navigate Romanian public administration procedures with clarity and confidence.

### 🧭 7 Core Navigation Sections
1. **Home:** Instant search with diacritic and synonym tolerance, quick-start wizard banner, estimated completion badges (`~X min`), and civic transparency notices.
2. **Explore:** Complete catalog filtered by citizenship status, difficulty, online submission, and institutional categories.
3. **Orientation Wizard:** Deterministic 3-step assistant providing personalized recommendations with explicit rationales and verified requirements.
4. **My Checklist:** Local document dossier preparation: check off required papers, add custom entries, record appointment date & counter location, and export summaries.
5. **Saved Guides:** Offline-first access to bookmarked administrative procedures.
6. **Reminders:** Configurable local Android notifications for expiring documents with 90, 60, 30, and 7-day presets.
7. **Settings & Civic Transparency:** Bilingual RO/EN toggle, directory of official portals, ROeID feasibility documentation, community feedback reporting, and local data reset.

---

## 🔒 Securitate & Transparență

- **Zero Colectare CNP / Parole:** ActeSimple nu reține identificatori naționali unici sau credențiale oficiale.
- **Portaluri Oficiale Verificate:** Redirecționare securizată exclusiv către domeniile guvernamentale verificate (*.gov.ro, ghiseul.ro, dgp.mai.gov.ro, anaf.ro, igi.mai.gov.ro).
- **Studiu de Fezabilitate ROeID:** Consultă fișierul `ROEID_INTEGRATION_FEASIBILITY.md` pentru analiza cadrului legal ADR (OUG 89/2022) și motivele tehnice de necolectare directă a autentificării de stat în aplicații civice terțe.

---

## 📱 Tehnologii & Compilare Android

- **Limbaj:** Kotlin 2.0.21
- **Interfață UI:** Jetpack Compose + Material Design 3 (M3)
- **Arhitectură:** Clean Architecture / MVVM (`ActeSimpleViewModel`, `StateFlow`)
- **Persistență Locală:** SharedPreferences cu `kotlinx.serialization` (complet funcțional offline)
- **Notificări:** Android Notification Manager (`actesimple_reminders` channel)
- **Build System:** Gradle (Kotlin DSL, AGP 8.8.0, Version Catalog `libs.versions.toml`)
