
# 🧾 JWT Claims Explained

In JWT, a **claim** is a piece of information (a **key–value pair**) stored inside the **payload** of the token.

Your code example:

```java
Jwts.builder()
    .subject(user.getUsername())
    .claim("userId", user.getId().toString())
    .issuedAt(new Date())
    .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
    .signWith(getSecretKey())
    .compact();
````

---

## 🧠 What is a Claim?

A **claim** is:

* Information about the **user**
* Or metadata about the **token**
* Stored inside the **JWT payload**
* Sent with every request (inside the token)

Claims are **not encrypted**, only **signed**.

---

## 📦 JWT Payload Structure

JWT payload is a JSON object:

```json
{
  "sub": "john",
  "userId": "42",
  "iat": 1690000000,
  "exp": 1690000600
}
```

Each entry inside this JSON is a **claim**.

---

## 🏷️ Types of Claims

JWT defines **three types of claims**:

---

### 1️⃣ Registered Claims (Standard)

Predefined by the JWT specification.

| Claim | Meaning                              |
| ----- | ------------------------------------ |
| `sub` | Subject (username / user identifier) |
| `iat` | Issued At (token creation time)      |
| `exp` | Expiration time                      |
| `iss` | Issuer                               |
| `aud` | Audience                             |

Example from your code:

```java
.subject(user.getUsername())   // sub
.issuedAt(new Date())          // iat
.expiration(...)               // exp
```

---

### 2️⃣ Public Claims

* Custom claims
* But follow standard naming conventions
* Avoid collisions with registered claims

Example:

```java
.claim("role", "ADMIN")
.claim("email", "user@gmail.com")
```

---

### 3️⃣ Private Claims (Your `userId`)

Claims defined **only for your application**.

```java
.claim("userId", user.getId().toString())
```

* Not part of JWT standard
* Used internally by your backend
* Very common and perfectly valid

---

## 🔍 Your Claim Explained

```java
.claim("userId", user.getId().toString())
```

This means:

* Key → `"userId"`
* Value → User’s database ID
* Stored in JWT payload
* Used later to:

    * Identify the logged-in user
    * Avoid DB lookup on every request

---

## 🔐 Are Claims Secure?

❌ Claims are **NOT encrypted**
✔ Claims are **BASE64-encoded**
✔ Token is **signed**, not encrypted

### ⚠️ Never store:

* Passwords
* Aadhaar / SSN
* Credit card numbers
* Sensitive personal data

---

## 🧪 How Claims are Used Later

Example in a JWT filter:

```java
Claims claims = Jwts.parserBuilder()
    .setSigningKey(getSecretKey())
    .build()
    .parseClaimsJws(token)
    .getBody();

String userId = claims.get("userId", String.class);
```

---

## 🧠 Simple Analogy

* JWT → ID Card
* Claims → Printed details (name, ID, role)
* Signature → Government seal

Seal ensures data isn’t changed, but details are visible.

---

## ✅ Summary

* ✔ Claims are **key–value pairs** inside JWT payload
* ✔ Used to carry user & token info
* ✔ `userId` is a **private claim**
* ✔ Claims are **signed, not encrypted**
* ❌ Do not store sensitive data

---

If you want, I can also explain:

* Difference between `claim()` and `subject()`
* Best practices for JWT claims
* How roles/authorities are stored in JWT

Just let me know 👍

```
```
