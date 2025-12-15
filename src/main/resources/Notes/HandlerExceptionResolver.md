
# Why `HandlerExceptionResolver` Is Used in JWT / Security Filters

This explanation clarifies **why `HandlerExceptionResolver` is required in a Spring Security filter**, and why simply throwing an exception does **not** work with `@ControllerAdvice`.

---

## 1️⃣ Where Does a Security Filter Run?

Your class extends:

```java
OncePerRequestFilter
````

This means the filter runs **before the controller** and **inside the Servlet Filter Chain**.

### Request Lifecycle (Simplified)

```
Client
  ↓
Servlet Filters        ← 🔴 JwtAuthFilter runs here
  ↓
DispatcherServlet
  ↓
Controller
  ↓
@ResponseBody / @ExceptionHandler
```

---

## 2️⃣ How `@ControllerAdvice` / GlobalExceptionHandler Works

A typical global exception handler:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handle(...) {
        ...
    }
}
```

### Important Rule

> ❗ `@ControllerAdvice` only handles exceptions thrown **after** the `DispatcherServlet` starts (i.e., in controllers or service layers called by controllers).

It does **not** automatically handle exceptions thrown in:

* Servlet filters
* Spring Security filters
* Pre-dispatch logic

---

## 3️⃣ What Happens If You Throw an Exception Directly in a Filter?

```java
throw new UsernameNotFoundException("User not found");
```

### Expected (but incorrect) assumption

* GlobalExceptionHandler will intercept it

### Actual behavior

* ❌ GlobalExceptionHandler is **never called**
* ❌ Spring MVC is not involved yet
* ❌ You may get a default error, empty response, or HTML error page

---

## 4️⃣ Then How Do We Return a Proper JSON Error Response?

This is where **`HandlerExceptionResolver`** comes in.

---

## 5️⃣ What Is `HandlerExceptionResolver`?

`HandlerExceptionResolver` is Spring’s internal **bridge between low-level servlet errors and Spring MVC exception handling**.

It allows:

* Exceptions from filters/security layers
* To be processed by `@ControllerAdvice`
* And returned as structured JSON responses

---

## 6️⃣ Why `handlerExceptionResolver.resolveException()` Is Used

Your catch block:

```java
catch (Exception e) {
    handlerExceptionResolver.resolveException(request, response, null, e);
}
```

### What Happens Internally

1. Exception is caught in the filter
2. It is passed to `HandlerExceptionResolver`
3. Spring finds a matching `@ExceptionHandler`
4. Your `GlobalExceptionHandler` is invoked
5. JSON response is written to `HttpServletResponse`
6. Request terminates cleanly

✔️ Consistent API error format
✔️ No HTML or default error responses

---

## 7️⃣ Why Not Just Rethrow the Exception?

```java
catch (Exception e) {
    throw e;
}
```

This leads to:

* ❌ Default Spring Security error handling
* ❌ HTML error pages
* ❌ No control over response format

This breaks REST API consistency.

---

## 8️⃣ Analogy: Two Buildings 🏢

### Building A — Filters (Security Layer)

* Knows nothing about controllers
* Cannot access `@ControllerAdvice`

### Building B — Controllers (MVC Layer)

* Formats responses
* Uses `@ExceptionHandler`

➡️ `HandlerExceptionResolver` acts as the **bridge** connecting both.

---

## 9️⃣ Why Spring Security Doesn’t Handle This Automatically

* Filters follow the Servlet specification
* Spring MVC is optional
* Security filters must remain framework-agnostic

Therefore, **manual forwarding is required**.

---

## 🔟 When Should You Use `HandlerExceptionResolver`?

Use it when:

* Exceptions occur in filters or security layers
* You want a consistent JSON response
* You rely on `@ControllerAdvice`

---

## 1️⃣1️⃣ When Is It NOT Needed?

You do **not** need it when exceptions are thrown:

* Inside controllers
* Inside services called by controllers

Example:

```java
@GetMapping("/users")
public User getUser() {
    throw new UsernameNotFoundException("Not found");
}
```

✔️ GlobalExceptionHandler will catch this automatically

---

## 1️⃣2️⃣ Best Practices Summary

### ❌ Incorrect (inside filters)

```java
throw new RuntimeException("Error");
```

### ✅ Correct (inside filters)

```java
handlerExceptionResolver.resolveException(request, response, null, e);
```

---

## 🧠 One-Line Rule to Remember

> **Exceptions thrown in filters will NOT reach `@ControllerAdvice` unless you explicitly forward them using `HandlerExceptionResolver`.**

---

## 🎯 Final Takeaway

| Question                                       | Answer                             |
| ---------------------------------------------- | ---------------------------------- |
| Can we throw exceptions directly from filters? | ❌ No                               |
| Will `@ControllerAdvice` catch them?           | ❌ No                               |
| Why use `HandlerExceptionResolver`?            | To forward exceptions to MVC layer |
| Is this best practice?                         | ✅ Yes                              |

---

## 📌 Related Topics (Optional)

* `AuthenticationEntryPoint` vs `AccessDeniedHandler`
* How Spring Security handles `AuthenticationException`
* Production-grade JWT filter design

```
```
