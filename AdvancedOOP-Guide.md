# Advanced OOP — Design Thinking in Java
### SOLID Principles + 5 Design Patterns

---

## How to Run

```bash
javac AdvancedOOP.java
java AdvancedOOP
```

> Requires Java 8 or higher. Check with `java -version`.

---

## What is "Design Thinking" in OOP?

Writing code that **compiles** is easy. Writing code that is **maintainable, scalable, and extendable** is a design problem.

> "Any fool can write code that a computer can understand.  
> Good programmers write code that humans can understand." — Martin Fowler

This file teaches two layers of design thinking:

| Layer | What it covers |
|-------|---------------|
| **SOLID Principles** | 5 rules for writing clean, flexible class structures |
| **Design Patterns** | 5 proven, reusable solutions to common engineering problems |

---

## File Structure

```
AdvancedOOP.java
│
├── PART 1: SOLID PRINCIPLES
│   ├── BadReport / Report / ReportFormatter / ReportSaver  → SRP
│   ├── BadPricer / DiscountStrategy / VIPDiscount / ...    → OCP
│   ├── BadBird / BadPenguin / Bird / Sparrow / Penguin     → LSP
│   ├── BadMachine / Printable / Scannable / Faxable        → ISP
│   └── BadOrderService / Database / MySQL / MongoDB        → DIP
│
├── PART 2: DESIGN PATTERNS
│   ├── AppConfig                                 → Singleton
│   ├── UserProfile + UserProfile.Builder         → Builder
│   ├── NewsChannel / Observer / Subscriber       → Observer
│   ├── SortStrategy / BubbleSort / Sorter        → Strategy
│   └── Coffee / Espresso / Decorators            → Decorator
│
└── AdvancedOOP (main)     → Runs all demos
```

---

## SOLID Principles

SOLID is an acronym coined by Robert C. Martin ("Uncle Bob"). Each letter is a design rule. Violating them leads to code that is fragile, hard to test, and painful to extend.

---

### S — Single Responsibility Principle

> **"A class should have ONE reason to change."**

If a class changes for multiple reasons (data changes, formatting changes, saving logic changes), it has multiple responsibilities — that's a design smell.

**The Bad Version:**
```java
class BadReport {
    void addData(String line) { ... }
    String format()           { ... }   // responsibility 1
    void saveToFile(String f) { ... }   // responsibility 2 — wrong!
}
```

If the file format changes, you edit `BadReport`. If the save destination changes, you edit `BadReport` again. Two reasons to change = fragile class.

**The Good Version:**
```java
class Report          { void addData() { ... } }          // stores data
class ReportFormatter { static String format(Report r) }  // formats it
class ReportSaver     { static void save(String c, String f) } // saves it
```

Now each class changes for exactly one reason. You can swap `ReportSaver` for a cloud uploader without touching `ReportFormatter`.

---

### O — Open/Closed Principle

> **"Open for extension. Closed for modification."**

Once a class is tested and working, you should be able to add new behaviour by **adding new code** — not by editing the existing class.

**The Bad Version:**
```java
class BadPricer {
    double getPrice(String customerType, double price) {
        if (customerType.equals("VIP"))     return price * 0.80;
        if (customerType.equals("Student")) return price * 0.90;
        // Adding "Senior" forces you to edit this method ← dangerous
    }
}
```

Every new discount type requires opening `BadPricer` and editing it — risking breaking existing logic.

**The Good Version:**
```java
interface DiscountStrategy {
    double apply(double price);
}

class VIPDiscount     implements DiscountStrategy { ... }
class StudentDiscount implements DiscountStrategy { ... }
class SeniorDiscount  implements DiscountStrategy { ... } // NEW — nothing else changes!

class Pricer {
    double getPrice(double price, DiscountStrategy strategy) {
        return strategy.apply(price);
    }
}
```

`Pricer` never changes. New discounts are new classes. The system is open to extension, closed to modification.

---

### L — Liskov Substitution Principle

> **"A child class must be usable wherever its parent class is expected — without breaking the program."**

If you have a method that accepts a `Bird`, passing in any subclass of `Bird` should work perfectly. If a subclass throws an exception or does nothing, LSP is violated.

**The Bad Version:**
```java
class BadBird    { void fly() { ... } }
class BadPenguin extends BadBird {
    @Override
    void fly() { throw new UnsupportedOperationException(); } // 💀 breaks callers!
}
```

Code that expects any `BadBird` to fly will crash when it gets a `BadPenguin`.

**The Good Version:**
```java
abstract class Bird   { abstract void eat(); abstract void makeSound(); }
interface CanFly      { void fly(); }

class Sparrow extends Bird implements CanFly { ... } // flies + eats
class Penguin extends Bird                   { ... } // only eats + swims
```

`Penguin` never claims it can fly. The `BirdHandler` checks before calling `fly()`. No surprises, no crashes.

**Key insight:** LSP violations are often a sign of a wrong inheritance hierarchy. Capability interfaces (like `CanFly`) fix it cleanly.

---

### I — Interface Segregation Principle

> **"No class should be forced to implement methods it doesn't use."**

Fat interfaces that bundle unrelated methods force classes to provide empty or fake implementations — which is dishonest and brittle.

**The Bad Version:**
```java
interface BadMachine {
    void print();
    void scan();
    void fax();  // What does an old printer do here? Empty body? throw exception?
}
```

**The Good Version:**
```java
interface Printable { void print(); }
interface Scannable { void scan();  }
interface Faxable   { void fax();   }

class ModernPrinter implements Printable, Scannable, Faxable { ... } // all 3
class OldPrinter    implements Printable                      { ... } // only 1
```

`OldPrinter` is honest. It only promises what it can deliver.

**Rule of thumb:** If you find yourself writing `// not supported` or `throw new UnsupportedOperationException()` in an interface method, your interface is too fat.

---

### D — Dependency Inversion Principle

> **"High-level modules should not depend on low-level modules. Both should depend on abstractions."**

**The Bad Version:**
```java
class BadOrderService {
    private MySQLDatabase db = new MySQLDatabase(); // hardwired!

    void placeOrder(String item) {
        db.save(item); // can't swap to MongoDB without rewriting this
    }
}
```

`BadOrderService` is married to `MySQLDatabase`. Testing it requires a real MySQL setup. Switching databases requires editing business logic.

**The Good Version:**
```java
interface Database { void save(String data); }

class MySQL   implements Database { ... }
class MongoDB implements Database { ... }

class OrderService {
    private final Database db;  // depends on interface, not implementation

    OrderService(Database db) { this.db = db; }  // INJECTED from outside

    void placeOrder(String item) { db.save(item); }
}
```

```java
// In main — swap databases with zero changes to OrderService
new OrderService(new MySQL()).placeOrder("Laptop");
new OrderService(new MongoDB()).placeOrder("Laptop");
```

This is also called **Dependency Injection** — one of the most important patterns in enterprise Java (Spring framework is built entirely around it).

---

## Design Patterns

Design patterns are **named, proven solutions** to recurring design problems. They are not libraries or frameworks — they are thinking tools.

Patterns are grouped into 3 families:

| Family | Solves | Examples in this file |
|--------|--------|----------------------|
| **Creational** | How objects are created | Singleton, Builder |
| **Behavioral** | How objects communicate | Observer, Strategy |
| **Structural** | How objects are composed | Decorator |

---

### Pattern 1 — Singleton (Creational)

**Intent:** Guarantee that a class has **exactly one instance** in the entire program, with a global access point.

**Use when:** Application configuration, logging systems, database connection pools, thread pools.

**How it works:**
```java
class AppConfig {
    private static AppConfig instance;     // holds the one instance

    private AppConfig() { ... }            // private → no external new()

    public static AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();    // created only once
        }
        return instance;
    }
}
```

```java
AppConfig a = AppConfig.getInstance();
AppConfig b = AppConfig.getInstance();

a.setTheme("dark");
b.display();             // shows dark theme — same object!

System.out.println(a == b); // true — identical reference
```

**Common trap:** The basic Singleton shown here is not thread-safe. In multi-threaded systems, use `synchronized` or an `enum`-based Singleton.

---

### Pattern 2 — Builder (Creational)

**Intent:** Construct complex objects **step-by-step**, making construction readable and preventing telescoping constructors.

**The problem it solves:**
```java
// What are these booleans? Impossible to read without checking the constructor signature.
new User("Alice", 25, "alice@email.com", "Engineer", "Chennai", true, false, true);
```

**The Builder solution:**
```java
UserProfile user = new UserProfile.Builder("Alice", "alice@email.com")
        .age(25)
        .role("Engineer")
        .city("Chennai")
        .verified(true)
        .build();
```

**Key mechanics:**
- Required fields go in the `Builder` constructor
- Optional fields each have a setter that `returns this` (enabling chaining)
- `build()` creates the final object
- The `UserProfile` constructor is `private` — only `Builder` can call it

**Why it's valuable for engineering:** Real-world objects (HTTP requests, database queries, UI components) have 10–20 configuration options. Builder keeps that readable and safe without forcing callers to pass nulls for unused fields.

---

### Pattern 3 — Observer (Behavioral)

**Intent:** When one object (the **Subject**) changes state, all registered objects (the **Observers**) are automatically notified.

**Real-world analogy:** YouTube subscriptions. The channel (Subject) publishes. All subscribers (Observers) get notified. Subscribers can unsubscribe anytime.

```java
interface Observer { void update(String event, String data); }
interface Subject  {
    void subscribe(Observer o);
    void unsubscribe(Observer o);
    void notifyObservers(String event, String data);
}
```

```java
NewsChannel bbc = new NewsChannel("BBC News");
bbc.subscribe(new EmailSubscriber("nav@gmail.com"));
bbc.subscribe(new PushNotificationSubscriber("DEVICE-001"));

bbc.publishNews("India wins the World Cup!"); // both notified

bbc.unsubscribe(phoneUser1);
bbc.publishNews("UN climate deal signed.");   // only email notified
```

**Why it's powerful:** The `NewsChannel` knows nothing about email or push notifications specifically — it just loops through `Observer` objects and calls `update()`. You can add SMS, WhatsApp, Slack notifications without changing a single line of `NewsChannel`.

This is the foundation of **event-driven programming** and **reactive systems**.

---

### Pattern 4 — Strategy (Behavioral)

**Intent:** Define a family of algorithms, encapsulate each one, and make them **interchangeable at runtime**.

**The problem it solves:**
```java
// Giant if-else that grows every time you add an algorithm
if (type.equals("bubble"))    { /* bubble sort */ }
else if (type.equals("quick")) { /* quick sort */ }
// Adding merge sort means editing this block forever
```

**The Strategy solution:**
```java
interface SortStrategy { void sort(int[] arr); }

class BubbleSort     implements SortStrategy { ... }
class SelectionSort  implements SortStrategy { ... }
class JavaBuiltInSort implements SortStrategy { ... }

class Sorter {
    private SortStrategy strategy;

    void setStrategy(SortStrategy s) { this.strategy = s; } // swap at runtime!
    void sort(int[] arr) { strategy.sort(arr); }
}
```

```java
Sorter sorter = new Sorter(new BubbleSort());
sorter.sort(data);

sorter.setStrategy(new JavaBuiltInSort()); // swap strategy — no if-else needed
sorter.sort(data);
```

**Notice:** `DiscountStrategy` from the OCP example IS the Strategy pattern. The two principles often work together.

**Real-world uses:** Payment processing (UPI/Card/Cash), route planning (fastest/shortest/scenic), data compression, AI game difficulty (Easy/Medium/Hard behavior).

---

### Pattern 5 — Decorator (Structural)

**Intent:** Add new behaviour to an object **at runtime** by wrapping it — without modifying its class or creating a subclass explosion.

**The problem with subclassing:**
```java
// If you use inheritance for every combination:
class EspressoWithMilk extends Espresso { ... }
class EspressoWithMilkAndSugar extends EspressoWithMilk { ... }
class EspressoWithMilkAndSugarAndCaramel extends ... // explosion!
// 10 add-ons = 2^10 = 1024 possible subclasses
```

**The Decorator solution — wrap instead of extend:**
```java
interface Coffee { String getDescription(); double getCost(); }

class Espresso    implements Coffee { ... }  // base: ₹50

class MilkDecorator extends CoffeeDecorator {
    public String getDescription() { return coffee.getDescription() + " + Milk"; }
    public double getCost()        { return coffee.getCost() + 15.0; }
}
```

```java
// Build any combination dynamically — no new subclass needed
Coffee myOrder = new SugarDecorator(
                    new WhipDecorator(
                        new CaramelDecorator(
                            new MilkDecorator(
                                new Espresso()))));

System.out.println(myOrder.getDescription()); // Espresso + Milk + Caramel + Whip + Sugar
System.out.println(myOrder.getCost());         // ₹115.0
```

**How it works structurally:**
- Every decorator implements `Coffee` (same interface as the base)
- Every decorator holds a reference to another `Coffee` (which could itself be a decorator)
- When you call `getCost()`, each wrapper adds its cost and delegates to the one inside

**Famous real-world use:** Java's own I/O system.
```java
new BufferedReader(new FileReader("file.txt"))
// BufferedReader decorates FileReader — adds buffering without changing FileReader
```

---

## How `main()` Works

The `main()` method in `AdvancedOOP` is a **demonstration runner** only — no business logic lives there. It creates objects, exercises each pattern, and prints formatted output so you can observe every concept running in sequence.

The `section()` and `sub()` helper methods exist purely to make terminal output readable.

---

## Patterns vs Principles — What's the difference?

| | SOLID Principles | Design Patterns |
|--|--|--|
| **What they are** | Rules / guidelines for class design | Named solutions to recurring problems |
| **Level** | How you structure individual classes | How multiple classes collaborate |
| **Origin** | Robert C. Martin | Gang of Four (GoF), 1994 |
| **Apply when** | Designing any class | Recognising a familiar problem type |

They complement each other. SOLID tells you *how each class should behave*. Patterns tell you *how classes should relate to each other*.

---

## Key Takeaways

| Concept | Core Idea | Remember by |
|---------|-----------|-------------|
| **SRP** | One class, one job | "One reason to change" |
| **OCP** | Add new code, don't edit old code | "Extend, don't modify" |
| **LSP** | Child safe to substitute for parent | "No surprise exceptions" |
| **ISP** | Small interfaces over fat interfaces | "Don't force empty methods" |
| **DIP** | Depend on abstractions, not implementations | "Talk to interfaces" |
| **Singleton** | One instance, global access | "Private constructor + static getInstance()" |
| **Builder** | Readable step-by-step construction | "Chain .set().set().build()" |
| **Observer** | Notify many on state change | "Subscribe / publish" |
| **Strategy** | Swap algorithms at runtime | "Plug in behaviour" |
| **Decorator** | Add behaviour by wrapping | "Layers, not subclasses" |

---

## Common Mistakes to Avoid

```java
// ❌ Singleton — forgetting private constructor
class Config {
    public Config() { ... }          // anyone can create multiple instances!
    static Config getInstance() { ... }
}

// ❌ Builder — not returning 'this' in setters
class Builder {
    void setName(String n) { this.name = n; }  // can't chain!
    Builder setName(String n) { this.name = n; return this; } // ✅
}

// ❌ Observer — modifying the subscriber list while iterating
for (Observer o : subscribers) {
    o.update(...);
    subscribers.remove(o);  // ConcurrentModificationException 💀
}
// ✅ Use a copy or CopyOnWriteArrayList

// ❌ Strategy — putting strategy selection logic back in the context
class Sorter {
    void sort(int[] arr, String type) {
        if (type.equals("bubble")) ...  // defeated the purpose of Strategy!
    }
}

// ❌ Decorator — forgetting to delegate to the wrapped object
class MilkDecorator extends CoffeeDecorator {
    public double getCost() {
        return 15.0; // forgot + coffee.getCost() — loses base price!
    }
}
```
