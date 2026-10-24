# Studiu de Fezabilitate: Integrare ROeID (Identitate Digitală Guvernamentală)
## ActeSimple — Ghid Administrativ Civic Independent pentru România

---

### 1. Rezumat Executiv & Context Legal

**ROeID** (Platforma Națională de Identitate Digitală) este sistemul oficial guvernamental gestionat de **Autoritatea pentru Digitalizarea României (ADR)** în temeiul **OUG nr. 89/2022** privind înființarea, administrarea și dezvoltarea infrastructurilor și serviciilor informatice de tip cloud guvernamental și a cadrului de reglementare european **eIDAS (Regulamentul UE nr. 910/2014)**.

Scopul ROeID este de a oferi o identitate digitală unică la nivel federat, garantând niveluri de asigurare a identității (*substantial* sau *high*) pentru accesul la serviciile publice electronice din România și Uniunea Europeană (ex: Ghișeul.ro, Spațiul Privat Virtual ANAF, HUB MAI, portalurile primăriilor).

---

### 2. Poziția Civic-Tehnică a Aplicației ActeSimple

ActeSimple este o **aplicație civică independentă de asistență administrativă și informare publică**. În conformitate cu principiile de securitate prin design (*Privacy by Design* și *Least Privilege*):

1. **Zero Colectare de Date de Identitate:** ActeSimple **NU solicită, NU interceptează și NU stochează** CNP-ul, seria/numărul de buletin, parolele sau documentele de identitate scanate ale cetățenilor.
2. **Fără Simulare de Autentificare:** Aplicația nu utilizează formulare false sau simulări de login guvernamental.
3. **Delegație Către Instituțiile Competente:** Toate etapele ce presupun autentificare electronică, semnare digitală calificată, plată online securizată sau depunere formală a dosarului sunt direcționate exclusiv către portalurile oficiale securizate gestionate de instituțiile statului (cu certificat SSL oficial *.gov.ro*).

---

### 3. Arhitectura Tehnică ROeID

| Componentă | Specificație Tehnică |
| :--- | :--- |
| **Protocol Principal** | OpenID Connect (OIDC) / OAuth 2.0 cu Proof Key for Code Exchange (PKCE) |
| **Protocol Moștenit** | SAML 2.0 (pentru portaluri administrative clasice) |
| **Nivel de Încredere (LoA)** | *Substantial* / *High* conform Regulamentului eIDAS |
| **Biometrie & Verificare** | Validare facială biometrică (liveness detection) corelată cu baza de date DEP (Direcția Generală pentru Evidența Persoanelor) |
| **Emitent Token** | STS (Serviciul de Telecomunicații Speciale) / ADR Identity Provider (IdP) |

---

### 4. Bariere Tehnice și Legale pentru Integratori Terți Non-Guvernamentali

În prezent, integrarea ca furnizor de servicii (*Service Provider* / Relying Party) cu ROeID presupune:

1. **Acord Inter-instituțional cu ADR:** Conform cadrului actual, accesul este rezervat instituțiilor publice din administrația centrală și locală, precum și anumitor operatori de servicii publice esențiale acreditați.
2. **Audit de Securitate Național:** Cerințe stricte de găzduire pe infrastructuri auditate conform normelor CERT-RO / DNSC (Directoratul Național de Securitate Cibernetică).
3. **Inutilitatea Colectării de Date:** Pentru scopul pur civic al ActeSimple (orientare administrativă, mementouri locale de expirare, pregătire checklist documente), solicitarea autentificării cu ROeID ar introduce riscuri majore de confidențialitate și o barieră de acces nejustificată pentru cetățeni.

---

### 5. Strategia de Integrare Recomandată (Arhitectura ActeSimple)

Pentru a oferi valoare maximă fără a compromite securitatea utilizatorilor:

```
+-------------------------------------------------------------------+
|                           ActeSimple                              |
|   (Asistent Civic: Orientare, Checklist, Mementouri Locale)       |
+-------------------------------------------------------------------+
                                  |
                                  | Redirecționare Securizată (Custom Tabs / Intent)
                                  v
+-------------------------------------------------------------------+
|                   Portaluri Oficiale Guvernamentale               |
|                                                                   |
|   +-----------------------+           +-----------------------+   |
|   |   Ghișeul.ro (ADR)    |           |   HUB MAI / DGEP      |   |
|   |   [Login cu ROeID]    |           |   [Programări / Cereri] |   |
|   +-----------------------+           +-----------------------+   |
|   |   ANAF - SPV          |           |   IGI Portal          |   |
|   |   [Certificat / ROeID]|           |   [Permise de Ședere] |   |
|   +-----------------------+           +-----------------------+   |
+-------------------------------------------------------------------+
```

1. **Ghidare Pre-Autentificare:** ActeSimple informează utilizatorul când o procedură necesită cont ROeID sau semnătură electronică calificată (ex: cazier judiciar online pe HUB MAI, înregistrare fiscală SPV).
2. **Deep-linking Direct:** Butoanele „Continuă pe portalul oficial” deschid direct adresele web oficiale ale ministerelor și agențiilor (dgp.mai.gov.ro, portal.mai.gov.ro, ghiseul.ro, anaf.ro).
3. **Informație Neutrală & Verificată:** ActeSimple separă cerințele legale obligatorii de recomandările practice de deplasare sau programare.

---

### 6. Concluzie

Integrarea directă a fluxurilor de autentificare ROeID într-o aplicație mobilă civică open/third-party este **nefezabilă legal** în actualul cadru ADR și **contraindicată din punct de vedere al securității și protecției datelor cu caracter personal (GDPR)**. 

Modelul actual implementat de ActeSimple — ghidare neutră, verificare editorială transparentă, pregătire dosar offline și redirecționare către serviciile oficiale statale — reprezintă standardul de aur pentru protejarea integrității și încrederii cetățenilor.
