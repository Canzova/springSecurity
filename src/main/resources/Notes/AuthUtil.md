
# Understanding JJWT Methods (`builder`, `parser`, `verifyWith`, `parseSignedClaims`)

You’re seeing **methods that come from the JJWT (`io.jsonwebtoken`) library**, and they follow a very common design pattern called the **Builder Pattern**.  
This guide explains everything **from basics**, step by step, and relates it directly to your code.

---

## 1️⃣ What is JJWT doing here?

JJWT helps you:

- **Create JWT tokens** (during login)
- **Read / verify JWT tokens** (during authentication)

So there are **two flows** in your class:

1. **Token creation** → `Jwts.builder()`
2. **Token parsing / validation** → `Jwts.parser().build().parseSignedClaims()`

---

## 2️⃣ What is the Builder Pattern? (Basics)

Instead of creating a big object in one step, we:

- Start with a **builder**
- Set values **step by step**
- Finally **build** the object

### Simple example

```java
Car car = Car.builder()
        .color("Red")
        .engine("V8")
        .build();
````

### Why this pattern is useful

* More readable
* Flexible
* Less error-prone

JWT creation and parsing works exactly the same way.

---

## 3️⃣ `builder()` – What does it do?

### Where used?

```java
Jwts.builder()
```

### Meaning

* Starts **building a JWT**
* Returns a `JwtBuilder` object

Think of it as:

> “I want to create a new JWT. Let me start setting its details.”

---

### Token creation flow in your code

```java
Jwts.builder()
    .subject(user.getUsername())
    .claim("userId", user.getId().toString())
    .issuedAt(new Date())
    .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
    .signWith(getSecretKey())
    .compact();
```

| Method         | Purpose                           |
| -------------- | --------------------------------- |
| `subject()`    | Sets **who the token belongs to** |
| `claim()`      | Adds **custom data**              |
| `issuedAt()`   | Token creation time               |
| `expiration()` | Token expiry time                 |
| `signWith()`   | Digitally signs the token         |
| `compact()`    | Produces the final JWT string     |

---

## 4️⃣ `compact()` – Why is it needed?

Internally, a JWT consists of:

* Header
* Payload
* Signature

`compact()`:

* Combines all parts
* Encodes them
* Returns the **final JWT string**

Example output:

```
eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huIn0.xxxxx
```

This is what you send to the frontend.

---

## 5️⃣ `parser()` – What does it do?

### Where used?

```java
Jwts.parser()
```

### Meaning

* Starts the **JWT verification / reading process**
* Returns a `JwtParserBuilder`

Think of it as:

> “I have a JWT. I want to read and validate it.”

---

## 6️⃣ `verifyWith()` – Why is it required?

```java
.verifyWith(getSecretKey())
```

### What it does

* Configures **which secret key** should be used to verify the JWT signature
* Does **NOT** read the token
* Does **NOT** validate expiration yet
* Does **NOT** throw exceptions yet

### In simple words

> “When I parse a token later, use THIS key to verify it.”

### Why this matters

JWT is signed like this:

```
SIGNATURE = HMAC(payload + header, SECRET_KEY)
```

During parsing:

* The **same secret key** must be used
* Otherwise, the token is rejected

This step **only prepares the parser**.

---

## 7️⃣ `build()` – What does it do here?

```java
.build()
```

Just like `builder()` starts building,
`build()` **finalizes the parser configuration**.

After this:

* Parser is ready to parse tokens
* No more configuration is allowed

---

## 8️⃣ `parseSignedClaims(token)` – What does it do?

```java
.parseSignedClaims(token)
```

This is the **main execution step**.

It:

* Verifies the signature
* Checks expiration
* Ensures the token is not modified
* Decodes header & payload
* Extracts claims

If anything is wrong:

* Expired token ❌
* Wrong signature ❌
* Malformed token ❌

➡️ An exception is thrown.

---

## 9️⃣ `getPayload()` – What is payload?

JWT structure:

```
HEADER.PAYLOAD.SIGNATURE
```

The payload contains:

* Subject
* Claims
* Issued time
* Expiration time

```java
Claims claims = ...
```

`getPayload()` returns this **payload (claims)** object.

---

## 🔟 `getSubject()` – Why are we using it?

```java
claims.getSubject();
```

Because during token creation you set:

```java
.subject(user.getUsername())
```

Now:

* You extract the username from the token
* Use it for authentication

---

## 🔁 Full Flow (Simple Words)

### 🔐 Token Creation

1. `builder()` → start token
2. Add data (subject, claims)
3. Sign token
4. `compact()` → get JWT string

---

### 🔓 Token Validation

1. `parser()` → start parsing
2. `verifyWith()` → configure verification key
3. `build()` → finalize parser
4. `parseSignedClaims()` → verify & decode token
5. `getPayload()` → extract data

---

## 🔍 Deep Dive: `verifyWith()` vs `parseSignedClaims()`

### Big Picture

* **`verifyWith()`** → tells *HOW* to verify the token
* **`parseSignedClaims()`** → actually *DOES* the verification and parsing

> One **configures**, the other **executes**.

---

### ID Card Analogy 🪪

**Step 1: Decide how to check**

```java
verifyWith(secretKey)
```

> “Use THIS key to check if the ID is genuine.”

**Step 2: Actually check**

```java
parseSignedClaims(token)
```

> “Now check the ID and read the details.”

---

### Why they are NOT identical

| Method                | Role          |
| --------------------- | ------------- |
| `verifyWith()`        | Configuration |
| `parseSignedClaims()` | Execution     |

They **cannot replace each other**.

---

### Why JJWT separates them

Because you may want to configure more rules **before parsing**:

```java
Jwts.parser()
    .verifyWith(key)
    .requireIssuer("hospital-app")
    .requireAudience("users")
    .clockSkewSeconds(30)
    .build()
    .parseSignedClaims(token);
```

Configuration first, execution last.

---

### Internal Execution Order

```java
Claims claims = Jwts.parser()
    .verifyWith(getSecretKey())
    .build()
    .parseSignedClaims(token)
    .getPayload();
```

1. `parser()` → create parser builder
2. `verifyWith()` → store secret key
3. `build()` → finalize parser
4. `parseSignedClaims()` →

    * Decode token
    * Recalculate signature
    * Compare signatures
    * Check expiration
    * Extract payload
5. `getPayload()` → return claims

---

### What if something is removed?

**Remove `verifyWith()`**

```
JWT signature does not match locally computed signature
```

**Remove `parseSignedClaims()`**

* Token is never parsed
* No verification
* No data extraction

---

### Lock Analogy 🔐

```java
verifyWith(key)        // choose the correct key
parseSignedClaims()    // unlock and open the door
```

Key without unlocking = useless
Unlock without key = impossible

---

## 🧠 Final One-Line Summary

> **`verifyWith()` sets the rules, `parseSignedClaims()` applies them.**

---
