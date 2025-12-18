# What is `@MapsId`?

> **`@MapsId` means:
> “This entity uses the SAME ID as another entity.”**

In other words:

* Two tables
* One primary key value
* Shared between them

---

# Why do we need `@MapsId`?

Sometimes an entity:

* **cannot exist alone**
* **depends completely on another entity**
* should have **the same ID** as its parent

Example:

* `User` → exists on its own
* `UserProfile` → makes no sense without `User`

So:

* User ID = 1
* UserProfile ID = 1

👉 That’s exactly what `@MapsId` does.

---

# Without `@MapsId` (problem)

```java
@Entity
class UserProfile {

    @Id
    private Long id;

    @OneToOne
    private User user;
}
```

❌ Problems:

* `id` and `user_id` are **separate**
* You must manage both manually
* IDs can get out of sync

---

# With `@MapsId` (solution) ✅

```java
@Entity
class UserProfile {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
}
```

✔ `UserProfile.id` = `User.id`
✔ Only **one ID value**
✔ JPA manages it automatically

---

# How it works (mentally)

Think of it like this:

> “This table’s ID **comes from** the parent table.”

When you save:

```java
User user = new User();
userRepository.save(user);

UserProfile profile = new UserProfile();
profile.setUser(user);
profileRepository.save(profile);
```

JPA does:

```
user.id = 5
profile.id = 5   ← mapped automatically
```

---

# Database tables

### user

```
id | name
```

### user_profile

```
id (PK, FK to user.id)
```

✔ Primary Key = Foreign Key

---

# When should you use `@MapsId`?

Use it when:

✔ One-to-One relationship
✔ Child cannot exist without parent
✔ Child ID must equal parent ID

Typical cases:

* User ↔ Profile
* Order ↔ OrderDetails
* Person ↔ Passport

---

# When NOT to use `@MapsId`?

❌ When child has its own independent ID
❌ When relationship is `OneToMany`
❌ When entities can exist separately

---

# Minimal working example

### Parent

```java
@Entity
class User {
    @Id
    @GeneratedValue
    private Long id;
}
```

### Child

```java
@Entity
class UserProfile {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    private User user;
}
```

---

# One-line summary 🧠

> **`@MapsId` tells JPA to reuse another entity’s ID as this entity’s ID.**

---
