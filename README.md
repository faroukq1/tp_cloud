
## 📡 **TCP Cloud Sync Protocol**

Each client connects via TCP and sends **commands as text or serialized packets**. All communications are done over a single persistent socket.

---

### 🧾 **Command Overview**

| Command    | Arguments                             | Description                     |
| ---------- | ------------------------------------- | ------------------------------- |
| `REGISTER` | `<username> <password>`               | Register a new user             |
| `LOGIN`    | `<username> <password>`               | Log in and start session        |
| `UPLOAD`   | `<filename> <filesize>\n<file-bytes>` | Upload file to server           |
| `LIST`     | *(none)*                              | List all files for user         |
| `DOWNLOAD` | `<filename>`                          | Download a file                 |
| `DELETE`   | `<filename>`                          | Delete a file (optional)        |
| `CHECKSUM` | *(none)*                              | Return list of file checksums   |
| `QUIT`     | *(none)*                              | Gracefully close the connection |

> Authentication must be completed first (via REGISTER or LOGIN).

---

## 🧪 **Sample Message Flow**

### 1. **REGISTER**

Client →

```
REGISTER alice secret123
```

Server →

```
OK User created
```

---

### 2. **LOGIN**

Client →

```
LOGIN alice secret123
```

Server →

```
OK Login successful
```

---

### 3. **UPLOAD**

Client →

```
UPLOAD a.txt 14
Hello, world!\n
```

Server →

```
OK Upload successful
```

---

### 4. **LIST**

Client →

```
LIST
```

Server →

```
a.txt 1024\n
b.jpg 2048\n
.\n
```

---

### 5. **DOWNLOAD**

Client →

```
DOWNLOAD a.txt
```

Server →

```
OK 14
Hello, world!\n
```

---

### 6. **DELETE**

Client →

```
DELETE a.txt
```

Server →

```
OK File deleted
```

---

### 7. **CHECKSUM**

Client →

```
CHECKSUM
```

Server →

```
a.txt e3b0c44298...\n
b.jpg fa1d3b8c2a...\n
.\n
```

---

## 🛠️ **File Storage Layout**

```
/tcp_cloud_storage/
  └── alice/
        ├── a.txt
        └── b.jpg
```

---

## 🧱 **Implementation Notes**

### Server Side

* Use `java.net.ServerSocket` (Java) or `socket.socket()` (Python/C++) to handle incoming connections.
* Each connection should be handled by a separate thread (or async).
* Maintain a simple `users.db` (can be SQLite, JSON, or flat file).
* Use SHA-256 or MD5 to calculate checksums.

### Client Side

* Build a simple CLI or GUI that can send commands and read responses.
* Serialize file uploads/downloads with header info (filename, size) followed by raw bytes.

---
