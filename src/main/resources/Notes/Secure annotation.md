
I’ll explain **`@Secured`** from the ground up and also compare it with `@PreAuthorize`, so you know **when to use it and when not to**.

---

# 1️⃣ What is `@Secured`?

`@Secured` is a **method-level security annotation** in Spring Security.

It restricts access to a method **based ONLY on roles / authorities**.

```java
@Secured("ROLE_ADMIN")
public void deleteUser(Long id) { }
```

If the user does not have `ROLE_ADMIN` → **403 Forbidden**

---

# 2️⃣ Important rule (most confusing part)

### `@Secured` does ❗ NOT ❗ add `ROLE_` automatically

This is different from `hasRole()`.

### ✅ Correct

```java
@Secured("ROLE_DOCTOR")
```

### ❌ Wrong

```java
@Secured("DOCTOR")
```

---

# 3️⃣ How `@Secured` works internally

`@Secured`:

* Reads the **current Authentication**
* Gets all `GrantedAuthority`
* Checks **string equality**

No SpEL.
No conditions.
No ownership checks.

---

# 4️⃣ Multiple roles in `@Secured`

```java
@Secured({ "ROLE_ADMIN", "ROLE_DOCTOR" })
```

This means:

> Allow access if user has **ANY ONE** of these roles

---

# 5️⃣ What `@Secured` CANNOT do ❌

You **cannot**:

* Check method parameters
* Compare user IDs
* Use logical conditions
* Use custom expressions

❌ This is NOT possible:

```java
@Secured("ROLE_ADMIN OR ROLE_DOCTOR") // ❌ invalid
```

❌ This is NOT possible:

```java
@Secured("#doctorId == principal.id") // ❌ invalid
```

---

# 6️⃣ Enable `@Secured`

### Spring Boot 3+

```java
@EnableMethodSecurity(securedEnabled = true)
```

### Spring Boot 2.x

```java
@EnableGlobalMethodSecurity(securedEnabled = true)
```

---

# 7️⃣ Comparison: `@Secured` vs `@PreAuthorize`

| Feature             | `@Secured` | `@PreAuthorize` |
| ------------------- | ---------- | --------------- |
| Role check          | ✅          | ✅               |
| Auto `ROLE_` prefix | ❌          | ✅ (`hasRole`)   |
| Method parameters   | ❌          | ✅               |
| Ownership checks    | ❌          | ✅               |
| SpEL support        | ❌          | ✅               |
| Logical operators   | ❌          | ✅               |
| Recommended today   | ❌          | ✅               |

---

# 8️⃣ Same rule written both ways

### Using `@Secured`

```java
@Secured("ROLE_ADMIN")
```

### Using `@PreAuthorize` (preferred)

```java
@PreAuthorize("hasRole('ADMIN')")
```

---

# 9️⃣ When should you use `@Secured`?

### ✅ Acceptable when:

* Simple role-based access
* Legacy projects
* No ownership logic

### ❌ Avoid when:

* You need parameter checks
* You use JWT
* You want clean, modern security

---

# 🔑 Best Practice (recommended)

👉 **Use `@PreAuthorize` for all new code**
👉 Keep `@Secured` only for legacy or very simple checks

---

# 🧠 One-line summary

> **`@Secured` is a basic role-only security check that requires full role names (`ROLE_X`) and offers no flexibility.**

