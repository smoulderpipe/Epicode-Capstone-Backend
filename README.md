# 🎯Focufy (backend)

Questa repository contiene la parte backend del progetto personale FOCUFY, sviluppato come esame finale del corso full-stack developer di Epicode nel 2024.

Per la parte frontend del progetto, fare riferimento a questa repository: [Epicode-Capstone-Frontend.](https://github.com/smoulderpipe/Epicode-Capstone-Frontend)
## 📚 Che cosa è Focufy?

Focufy è un&#39;app motivazionale per studenti, pensata per aiutarli a conoscere meglio se stessi e a pianificare routine di studio in linea con i propri bisogni.
## ✨ Funzionalità Principali

- **Gestione utenti**: Registrazione, autenticazione e gestione degli utenti.
- **Test abitudini, personalità e obiettivi**: Sistema di domande e risposte necessario alla costruzione del profilo dell&#39;utente.
- **Assegnazione avatar**: Assegnazione avatar specifico in base alle abitudini e alla personalità dell&#39;utente.
- **Creazione e gestione del piano di studio**: Costruzione di un calendario personalizzato in linea con l&#39;avatar e gli obiettivi dell&#39;utente, con sessioni di attività, mantra quotidiani, checkpoint settimanali e deadline.
- **Test autovalutativi**: Sistema prelievo e salvataggio domande e risposte di tipo checkpoint e deadline (necessario all&#39;elaborazione delle statistiche dell&#39;utente in frontend).
- **Funzioni di reset**: Sistema per l&#39;eliminazione selettiva dei dati personali (per permettere agli utenti di ricominciare da capo l&#39;esperienza, dando risposte diverse).

## 🛠️ Tecnologie Utilizzate

- **Java 22**: Linguaggio di programmazione utilizzato.
- **Spring Boot 3.3.0**: Framework per lo sviluppo di applicazioni Java.
    - *spring-boot-starter-data-jpa*: Starter per JPA e Hibernate.
    - *spring-boot-starter-security*: Starter per la sicurezza dell'applicazione.
    - *spring-boot-starter-validation*: Starter per la validazione.
    - *spring-boot-starter-web*: Starter per le applicazioni web, inclusi RESTful.
    - *spring-boot-starter-test*: Starter per i test.
- **PostgreSQL**: Database relazionale utilizzato.
- **Lombok**: Libreria per ridurre il boilerplate code.
- **JSON Web Tokens (JWT)**: Autenticazione e autorizzazione.
- **Apache Commons IO**: Utility per la gestione dell&#39;I/O.
- **Cloudinary**: Servizio per la gestione delle immagini.

## 🚀 Installazione e Configurazione
**Prerequisiti**

- JDK 22
- IntelliJ IDEA (o altro IDE)
- PostgreSQL

**Clona la repository**

	git clone https://github.com/smoulderpipe/Epicode-Capstone-Backend.git

**Configura il database:**

Assicurati di avere un&#39;istanza di PostgreSQL in esecuzione e crea un database per l&#39;applicazione.

**Configura l&#39;ambiente:**

Aggiungi le configurazioni necessarie nel file *application.properties* nella directory *src/main/resources*:

	spring.datasource.username=tuo_username
	spring.datasource.password=tua_password
	jwt.secret=tua_jwt_secret
	jwt.duration=tua_jwt_duration
	cloudinary.cloud-name=tuo_cloud_name
	cloudinary.api-key=tua_api_key
	cloudinary.api-secret=tua_api_secret

**Compilazione e avvio dell&#39;applicazione con IntelliJ IDEA**

   - Apri IntelliJ IDEA e importa il progetto **focufy** che si trova dentro la repository clonata, importandolo come un progetto maven esistente.
   - Lascia che IntelliJ scarichi tutte le dipendenze necessarie.
   - Configura il tuo SDK Java 22.
   - Esegui il file FocufyApplication.java per avviare l&#39;applicazione.

Il server sarà disponibile all&#39;indirizzo http://localhost:8080.

In frontend bisogna utilizzare la porta 4200 (http://localhost:4200), oppure modificare il file AppConfig dentro src/main/java/it.epicode.focufy/config.
## 📂 Struttura del Progetto

   - **src/main/java**: Contiene il codice sorgente dell&#39;applicazione.
        - **controllers**: Contiene i controller per gestire le richieste API.
        - **entities**: Contiene le entità JPA.
        - **repositories**: Contiene le interfacce repository per l&#39;accesso ai dati.
        - **dtos**: Contiene le classi data transfer object
        - **services**: Contiene la logica di business dell&#39;applicazione.
        - **security**: Contiene le configurazioni di sicurezza.
        - **config**: Contiene le configurazioni dell&#39;applicazione.
        - **exceptions**: Contiene le classi per gestire le eccezioni.
   - **src/main/resources**: Contiene le risorse statiche e il file di configurazione *application.properties*.
   - **src/test**: Contiene i test unitari e di integrazione.

## 🌐 API

**AUTH**
- POST **/auth/register**: Registra un nuovo utente.
- POST **/auth/login**: Effettua il login di un utente.
- POST **/auth/logout**: Effettua il logout di un utente.

**USER CONTROLLER**
- GET **/api/users**: Ottiene una lista di tutti gli utenti.
- GET **/api/users/{id}**: Ottiene i dettagli di un utente specifico.
- GET **/api/users/{userId}/avatar**: Ottiene l&#39;avatar di un utente.
- GET **/api/users/{userId}/checkpoint**: Ottiene tutte le risposte Checkpoint di un utente.
- GET **/api/users/{userId}/deadline**: Ottiene tutte le risposte Deadline di un utente.
- PUT **/api/users/{id}**: Aggiorna i dettagli di un utente.
- PUT **/api/users/{id}/long-term-goal**: Aggiorna il long-term goal di un utente.
- DELETE **/api/users/{id}**: Elimina un utente.

**QUESTION**
- GET **/api/questions**: Ottiene una lista di tutte le domande.
- GET /**api/questions/{id}**: Ottiene i dettagli di una domanda specifica.
- POST **/api/questions**: Crea una nuova domanda.
- POST **/api/questions/upload**: Carica domande da un file .txt per pre-popolare il database.
- PUT **/api/questions/{id}**: Aggiorna una domanda esistente.
- DELETE **/api/questions/{id}**: Elimina una domanda.

**ANSWER**
- GET **/api/answers/question/{questionId}**: Ottiene le risposte per una specifica domanda.
- GET **/api/answers/shared**: Ottiene tutte le risposte multiple.
- GET **/api/answers/shared/{id}**: Ottieni una risposta multipla specifica.
- GET **/api/answers/users/{userId}/shared**: Ottieni tutte le risposte multiple di un utente.
- POST **/api/answers/shared**: Crea una nuova risposta multipla.
- POST **/api/answers/uploadSharedAnswers**: Carica risposte multiple da un file .txt.
- PUT **/api/answers/shared/{id}**: Aggiorna una risposta multipla.
- PUT **/api/answers/shared/assign/{userId}**: Assegna una lista di risposte multiple a un utente.
- DELETE **/api/answers/shared/{id}**: Elimina una risposta multipla.
- DELETE **/api/answers/user/shared/{userId}**: Elimina tutte le risposte multiple di un utente.
- GET **/api/answers/personal**: Ottiene tutte le risposte personali.
- GET **/api/answers/personal/{id}**: Ottiene una risposta personale specifica.
- GET **/api/answers/users/{userId}/personal**: Ottiene tutte le risposte personali di un utente.
- POST **/api/answers/personal/{id}**: Crea una nuova risposta personale.
- DELETE **/api/answers/personal/{id}**: Elimina una risposta personale.
- DELETE **/api/answers/users/{userId}/personal**: Elimina tutte le risposte personali di un utente.
- GET **/api/answers/users/{userId}/checkpoint/{cdAnswerType}**: Ottiene tutte le risposte checkpoint di un certo tipo inserite da un utente nel proprio piano di studi.
- POST **/api/answers/users/{userId}/checkpoint**: Crea una nuova risposta di tipo checkpoint.
- GET **/api/answers/users/{userId}/deadline/{cdAnswerType}**: Ottiene tutte le risposte deadline di un certo tipo inserite da un utente nel proprio piano di studi.
- POST **/api/answers/users/{userId}/deadline**: Crea una nuova risposta di tipo deadline.

**CHRONOTYPE**
- GET **/api/chronotypes**: Ottiene una lista di tutti i cronotipi.
- GET **/api/chronotypes/{id}**: Ottiene i dettagli di un cronotipo specifico.
- POST **/api/chronotypes**: Crea un nuovo cronotipo.
- PUT **/api/chronotypes/{id}**: Aggiorna un cronotipo esistente.
- DELETE **/api/chronotypes/{id}**: Elimina un cronotipo.

**TEMPER**
- GET **/api/tempers**: Ottiene una lista di tutti i temperamenti.
- GET **/api/tempers/{id}**: Ottiene i dettagli di un temperamento specifico.
- POST **/api/tempers**: Crea un nuovo temperamento.
- PUT **/api/tempers/{id}**: Aggiorna un temperamento esistente.
- DELETE **/api/tempers/{id}**: Elimina un temperamento.

**AVATAR**
- POST **/api/avatars/upload**: Carica tutti gli avatar da un file .txt
- PUT **/api/users/{userId}/remove-avatar**: Rimuove l&#39;avatar di un utente.

**MANTRA**
- POST **/api/mantras/upload**: Carica tutti i mantra da un file .txt.

**STUDY PLAN**
- GET **/api/users/{userId}/studyplans**: Ottiene tutti i piani di studio di un utente specifico.
- POST **/api/users/{userId}/studyplans**: Crea un nuovo piano di studio per uno specifico utente.
- POST **/api/users/{userId}/addMantras**: Aggiunge i mantra al piano di studio di un utente specifico.
- DELETE **/api/users/{userId}/studyplans**: Elimina un piano di studio di un utente specifico.

## 📋 Popolamento Database

Per ottenere i contenuti necessari al funzionamento dell&#39;applicazione, bisogna pre-popolare il database con i file .txt presenti nella cartella **/persistence**, alla base di questa repository.

1. **Creare un utente:**
	POST /api/auth/register

2. **Assegnargli il ruolo ADMIN e riavviare l&#39;applicazione**

3. **Effettuare le seguenti operazioni tramite admin loggato, includendo bearer token:**

	POST /api/questions/upload
PAYLOAD: file questions.txt

	POST /api/answers/uploadSharedAnswers
PAYLOAD: file shared-answers.txt

	POST /api/avatars/upload
PAYLOAD: file avatars.txt

	POST /api/mantras/upload
PAYLOAD: file mantras.txt
