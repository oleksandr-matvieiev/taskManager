# Task Manager

A console-based task manager for digital task organization and tracking.  
It allows you to create, manage, and archive tasks with tags, status, and deadlines, with flexible filtering, notifications, and export options.

---

## Features

- **Task CRUD** – Create, read, update, and delete tasks with tags and statuses.
- **Deadline reminders** – Pop-up notifications using Swing.
- **Archiving** – Deleted tasks(completed) are saved in JSON files (`deleted_tasks_yyyy_mm.json`).
- **Logging** – All actions are logged to a dedicated file.
- **Colorful Console** – Enhanced readability in the terminal.
- **Export** – Export tasks to CSV or JSON.
- **Automatic Cleanup** – Completed tasks are automatically deleted after N days.
- **Flexible Filtering** – Filter tasks by tag, status, deadline, or keyword.
- **Flexible Deadlines** – Users can input dates in multiple formats, which are parsed into a single standardized system format.
- **Custom Export Directory** – Change the folder for exported files.  

---

## Technology Stack

- **Java 17**
- **Maven** for build and dependency management
- **SQLite** via JDBC
- **SLF4J + Logback** for logging
- **Jakarta Validation + Hibernate Validator** for input validation
- **Jackson** for JSON serialization/deserialization
- **OpenCSV** for CSV support
- **Swing** for popup notifications
- **JUnit 5 + Mockito** for unit testing  

---

## Project Structure

```
src/main/java/org/example/
│
├─ dao
│  ├─ Database.java             # Database creation & connection
│  ├─ TagDAO.java               # CRUD operations for tags
│  └─ TaskDAO.java              # CRUD operations for tasks
│
├─ model
│  ├─ Tag.java
│  ├─ Task.java
│  └─ TaskStatus.java
│
├─ service
│  ├─ AppConfig.java            # Global configuration (export dir, cleanup days)
│  ├─ DeletedTaskArchiveService.java
│  ├─ ExportService.java
│  ├─ SwingNotifier.java
│  ├─ TagManager.java
│  ├─ TaskManager.java
│  └─ interface TaskNotifier.java
│
├─ ui
│  ├─ ConsoleColor.java
│  ├─ FilterMenu.java
│  ├─ SettingsMenu.java
│  ├─ SystemPrinter.java
│  ├─ TagPrinter.java
│  ├─ TaskMenu.java
│  ├─ TaskPrinter.java
│  ├─ TaskUI.java
│  └─ Toast.java                 # Swing pop-up for notifications
│
├─ util
│  ├─ DateUtils.java             # Parsing and formatting dates
│  └─ TaskUtils.java             # Utilities for task collections & mapping
│
└─ Main.java

```

```
src/main/resources/
│
└─ logback.xml                   # Logging config for deleted tasks
```

## Tests
```
src/test/java/
│
├─ TagManagerTest.java
│
└─ TaskManagerTest.java

```
---

## Installation & Running

1. Clone the repository:

```bash
git clone https://github.com/oleksandr-matvieiev/taskManager
cd taskManager
```

2. Build the project with Maven:

```bash
mvn clean install
```

3. Run the application:

```bash
mvn exec:java
```

The application will automatically create the SQLite database if it does not exist.

---

## Running Tests

Run unit tests using Maven:

```bash
mvn test
```

Tests cover core functionality of `TagManager` and `TaskManager`.

---

## Configuration

The `AppConfig` class allows you to configure(it's also possible form Setting menu in console):

- `exportDir` – Directory where exported files are saved.
- `deleteDoneAfterDays` – Number of days after which completed tasks are automatically deleted.

## Example Usage (Menu-based)
**Main Menu:**
```
Welcome to Task Manager. Please enter the option:
1. Task operations
2. View all tasks
3. Filters
9. Settings
0. Exit
Choose option: 1
```
**Task Menu:**
```
--- Task Menu ---
1. Add task
2. Remove task
3. Mark task as completed
0. Back
Choose option: 1
```
**Creating a Task:**
```
Write title: Finish project report
Write description: Complete the final project report by deadline
Write date (Format dd-MM-yyyy or ddMMyyyy): 31-12-2025
Do you want to make this task repeatable?
1. Yes
2. No
Your choice: 2

+----+--------------------------+
| №  | Tag Name                 |
+----+--------------------------+
| 1  | Uncategorized            |
+----+--------------------------+
Enter tag number, or 0 to create new: 0
Enter new tag name: Work

Task added successfully!
```

**Viewing All Tasks:**
```
+----+--------------------------------+----------------------------------------------------+------------+----------+--------------+
| №  | Title                          | Description                                        | End Date   | Tag        | Status     |
+----+--------------------------------+----------------------------------------------------+------------+----------+--------------+
| 1  | Finish project report          | Complete the final project report by deadline      | 31.12.2025 | Work       | IN_PROGRESS|
+----+--------------------------------+----------------------------------------------------+------------+----------+--------------+
```

**Filtering Tasks:**
```
Choose filter:
1. By tag
2. By status
3. By deadline
4. By keyword
0. Back
Choose option: 1

1. Failed
2. In Progress
3. Done
Choose option: 2 

+----+--------------------------------+----------------------------------------------------+------------+----------+--------------+
| №  | Title                          | Description                                        | End Date   | Tag        | Status     |
+----+--------------------------------+----------------------------------------------------+------------+----------+--------------+
| 1  | Finish project report          | Complete the final project report by deadline      | 31.12.2025 | Work       | IN_PROGRESS|
+----+--------------------------------+----------------------------------------------------+------------+----------+--------------+
```


## Notes

- Default tag ID is reserved (1) and cannot be deleted.
- All dates input by the user are parsed into a unified internal format.
- Deleted tasks are archived automatically before removal to avoid data loss.