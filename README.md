![Alt text](../cloud/cloud.png)

<table style="width:100%">
  <thead>
    <tr>
      <th>Method</th>
      <th>Endpoint</th>
      <th>Description</th>
    </tr>
  </thead>
  <tbody>
    <tr><td>POST</td><td>/api/register</td><td>Register a new user</td></tr>
    <tr><td>POST</td><td>/api/login</td><td>Authenticate user and return a token/session</td></tr>
    <tr><td>POST</td><td>/api/upload</td><td>Upload a file to the user's cloud directory</td></tr>
    <tr><td>GET</td><td>/api/files</td><td>List all files in the user's cloud directory</td></tr>
    <tr><td>GET</td><td>/api/files/{filename}</td><td>Download a specific file from the cloud</td></tr>
    <tr><td>DELETE</td><td>/api/files/{filename}</td><td>Delete a specific file from the cloud (optional)</td></tr>
    <tr><td>GET</td><td>/api/files/checksum</td><td>Return filenames with their checksums for sync</td></tr>
  </tbody>
</table>

---

### 🔐 **1. `POST /api/register` — Register a New User**

#### **Description:**

Create a new user account and a dedicated cloud directory.

#### **Algorithm:**

1. Receive JSON: `{ "username": "user", "password": "pass" }`.
2. Validate input (non-empty, unique username).
3. Hash the password using `BCrypt`.
4. Save user to database.
5. Create a directory: `/cloud_storage/{username}`.
6. Return success response: `201 Created`.

---

### 🔑 **2. `POST /api/login` — Authenticate User**

#### **Description:**

Verify user credentials and return a token/session.

#### **Algorithm:**

1. Receive JSON: `{ "username": "user", "password": "pass" }`.
2. Look up user in database.
---

### 📤 **3. `POST /api/upload` — Upload a File**

#### **Description:**

Store a user file in their cloud directory.

#### **Algorithm:**

1. Accept `multipart/form-data` with file and JWT in headers.
2. Authenticate token → get username.
3. Get file input stream from the request.
4. Save file to: `/cloud_storage/{username}/{filename}`.
5. (Optional) Store metadata in DB: filename, size, hash, timestamp.
6. Return: `200 OK` with success message.

---

### 📂 **4. `GET /api/files` — List User Files**

#### **Description:**

Return the list of all files for the user.

#### **Algorithm:**

1. Authenticate JWT → get username.
2. Read file names from `/cloud_storage/{username}/`.
3. Optionally include size, lastModified time.
4. Return JSON list:

   ```json
   [
     { "filename": "a.txt", "size": 1024 },
     { "filename": "b.jpg", "size": 2048 }
   ]
   ```

---

### 📥 **5. `GET /api/files/{filename}` — Download File**

#### **Description:**

Allow client to download a specific file.

#### **Algorithm:**

1. Authenticate JWT → get username.
2. Locate file: `/cloud_storage/{username}/{filename}`.
3. If file exists, return it as a `ResponseEntity<Resource>` with headers:

   * `Content-Disposition: attachment`.
   * Content-Type.
4. If file missing → return `404 Not Found`.

---

### 🗑️ **6. `DELETE /api/files/{filename}` — Delete File (Optional)**

#### **Description:**

Delete a file from the user’s cloud directory.

#### **Algorithm:**

1. Authenticate JWT → get username.
2. Locate and delete: `/cloud_storage/{username}/{filename}`.
3. Return:

   * `200 OK` if success.
   * `404 Not Found` if file missing.

---

### 🔁 **7. `GET /api/files/checksum` — File Checksum List**

#### **Description:**

Return hashes of user files for sync verification.

#### **Algorithm:**

1. Authenticate JWT → get username.
2. For each file in `/cloud_storage/{username}/`:

   * Calculate SHA-256 hash or MD5.
3. Return list:

   ```json
   [
     { "filename": "a.txt", "checksum": "e3b0c..." },
     { "filename": "b.jpg", "checksum": "fa1d3..." }
   ]
   ```

---