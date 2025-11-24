#  Library Management System (Java + MySQL)

A lightweight **Library Management System** built in **Java (Swing GUI)** with a **MySQL** backend.  
It supports **user authentication**, **book search**, **borrowed-book tracking**, and **automated database initialization**.


##  About the Project

Developed as part of academic coursework, this project showcases:

- ✅ Java **OOP design**
- ✅ GUI development with **Swing**
- ✅ **MySQL** database integration
- ✅ Form-based user interactions
- ✅ Basic **CRUD** logic
- ✅ SQL schema creation & automatic data loading



##  Features

###  User Authentication
- Login form with credential validation  
- Error handling and friendly user feedback  

###  Search Books
- Search by **title** or **author**  
- Results displayed in a clean **Swing table**  
- Backed by SQL queries  

###  Borrowed Books Management
- View a list of borrowed books  
- Return books from within the UI  
- Track due dates  

###  MySQL Integration
- SQL schema creation  
- Automated table creation via `DatabaseInitializer.java`  
- Includes `library_dump.sql` for quick setup  

###  GUI (Swing)
Multiple Java Swing forms provide a simple, user-friendly interface:

- `LoginForm`
- `MainScreen`
- `MyBooksForm`
- `SearchForm`



##  Project Structure

```text
src/
 ├─ DatabaseInitializer.java
 ├─ LoginForm.java
 ├─ MainScreen.java
 ├─ MyBooksForm.java
 └─ SearchForm.java

library_dump.sql        # MySQL schema + sample data
.gitignore              # Cleans IDE output and binaries
