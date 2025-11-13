# 🧩 Projekt Mikroserwisowy

## 📘 Opis projektu

Projekt przedstawia **system portfela inwestycyjnego** zbudowany w architekturze **mikroserwisowej**, oparty o komunikację asynchroniczną z wykorzystaniem **Apache Kafka** oraz pamięć podręczną **Redis**.  
Celem projektu jest prezentacja nowoczesnego podejścia do budowy skalowalnych aplikacji backendowych w technologii **Micronaut**, przy jednoczesnym zachowaniu elastyczności i wydajności.

Każdy moduł (mikroserwis) jest niezależny — można go rozwijać, wdrażać i skalować osobno.  
Aplikacja obsługuje transakcje finansowe, synchronizację danych z zewnętrznych API (np. kursy walut, ceny aktywów), oraz kalkulację wartości portfela użytkownika.

---

## 🏗️ Architektura systemu

Projekt został zaprojektowany w duchu **Clean Architecture** i **Domain-Driven Design (DDD)**.  
Komunikacja między mikroserwisami odbywa się poprzez **Apache Kafka** (asynchronicznie) oraz REST API (synchronizacja danych).

### 🔹 Główne komponenty

| Mikroserwis | Opis | Port domyślny |
|--------------|------|---------------|
| **api-server** | Brama API, obsługuje routing i komunikację z frontendem | `8080` |
| **portfolio-service** | Logika biznesowa zarządzania portfelem, kalkulacje wartości aktywów | `8081` |
| **transaction-service** | Obsługa transakcji kupna/sprzedaży, integracja z zewnętrznymi API cenowymi | `8082` |
| **market-data-service** | Usługa pobierania i cache’owania aktualnych cen aktywów | `8083` |

api-model zawiera klasy modelowe wspóldzielone przez różne mikroserwisy ( w tym .proto)

### 🔹 Komponenty infrastrukturalne

- **Apache Kafka** – komunikacja asynchroniczna między mikroserwisami (event-driven architecture)  
- **Redis** – cache danych (np. ceny aktywów, wyniki kalkulacji)  
- **PostgreSQL** – baza danych dla poszczególnych mikroserwisów  
- **Docker Compose** – uruchamianie całego środowiska lokalnie  
- **Micronaut Framework** – lekki framework do tworzenia mikroserwisów w Javie (szybki start, DI, AOT)

---

## ⚙️ Użyte technologie

| Technologia | Zastosowanie |
|--------------|--------------|
| **Java 21 (Amazon Corretto)** | Główny język backendu |
| **Micronaut 4.x** | Framework mikroserwisowy |
| **Apache Kafka** | Asynchroniczna komunikacja między usługami |
| **Redis** | Cache i pub/sub |
| **PostgreSQL** | Relacyjna baza danych |
| **Docker & Docker Compose** | Uruchamianie środowiska lokalnego |
| **Maven** | Budowanie i zarządzanie zależnościami |

---



<img width="2816" height="1775" alt="image" src="https://github.com/user-attachments/assets/15d5f7a2-5a47-47c8-bd25-eadb78971af7" />


<img width="2468" height="1103" alt="image" src="https://github.com/user-attachments/assets/349e27d6-43d4-44b1-950e-bbca18389241" />


<img width="2030" height="1645" alt="image" src="https://github.com/user-attachments/assets/23eddcba-8d85-4b81-be6d-ac21862a8813" />


<img width="2186" height="1349" alt="image" src="https://github.com/user-attachments/assets/87475688-b0f7-446c-9b75-703ae2b4fd89" />


<img width="2396" height="1540" alt="image" src="https://github.com/user-attachments/assets/a58454c2-e083-4343-9f99-530392eb835e" />

## 🚀 Uruchamianie projektu

Każdy mikroserwis należy uruchamiać osobno.  
Zalecane jest wykorzystanie **Docker Compose**, które automatycznie uruchomi Kafkę, Redisa i bazy danych.





