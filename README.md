# Java Concurrency Mastery: From Basics to System Design

This repository contains a complete set of solutions for the Java Concurrency Exercises, covering fundamental thread synchronization, inter-thread communication, and thread-safe system architecture.

## 📚 Project Overview
The project is structured to demonstrate the evolution of multithreading complexity in Java. It progresses from simple thread creation to solving complex synchronization problems using low-level primitives and high-level concurrent utilities.



---

## 🛠 Features & Implementations

### 🟢 Level: Easy
Focuses on basic thread management and the `synchronized` keyword to prevent race conditions.
* **Thread Creation:** Using the `Thread` class and `join()` to coordinate execution.
* **Atomic Counters:** Implementing thread-safe increments for shared variables.
* **Output Synchronization:** Using `synchronized` to prevent message interleaving in the console.
* **Thread-Safe Collections:** Safe concurrent additions to an `ArrayList`.

### 🟡 Level: Medium
Introduces inter-thread communication and built-in concurrent data structures.
* **Producer-Consumer:** Utilizing `BlockingQueue` for automatic synchronization.
* **Wait/Notify Patterns:** Implementing blocking logic in a `BankAccount` (insufficient funds) and a `Task Queue` (empty queue).
* **Bounded Resources:** Creating a `Resource Pool` to manage a fixed set of available IDs.
* **State-Dependent Blocking:** A shared counter that blocks at specific upper and lower bounds.

### 🔴 Level: Hard
Complex coordination involving priority-based scheduling and "Poison Pill" shutdown patterns.
* **Priority Task Queue:** Ordering execution based on task importance using `PriorityQueue` and `wait/notifyAll`.
* **Bounded Buffers:** Manual array-based implementation with producers signaling completion via a "poison pill" value (-1).
* **Resource Allocation:** Prioritizing high-priority resource requests when availability is low.
* **Time-Based Scheduling:** Implementing a scheduler where `executeNextTask()` blocks until a specific epoch time is reached.

---
