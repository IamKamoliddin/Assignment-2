# Question 7 — Reading state in the drawing phase

## Context & Task
- **Context:** `Canvas`, `Modifier.drawBehind`, and `Modifier.drawWithContent` read their state while drawing. A state read then runs only the drawing phase.
- **Tasks:**
  1. Add `ColorDemo` with `Canvas` and `drawRect(color)`. Predict the log, tap, and compare.
  2. Replace `Canvas` with `Box(Modifier.size(120.dp).background(color).logPhases("color demo"))`.
  3. Observe which phases appear and explain what this difference tells you about where state is read vs stored.

---

## 1. ColorDemo with Canvas (`drawRect(color)`)

### Prediction:
- **Where is `color` read?** Inside the `Canvas` drawing lambda (`drawRect(color)`), which executes during the **drawing phase**.
- **Log Output:**
  ```text
  D/Phases: color demo: draw
  ```
- **Analysis:** Recomposition (`ColorDemo: composition`) and layout measure/place are completely skipped because `color` is read exclusively in the draw phase.

---

## 2. Replacing Canvas with Box (`background(color)`)

```kotlin
Box(
    modifier = Modifier
        .size(120.dp)
        .background(color)
        .logPhases("color demo")
)
```

### Observation:
- **Which phases appear now?**
  ```text
  D/Phases: ColorDemo: composition
  D/Phases: color demo: layout/measure (if layout changes, or draw)
  D/Phases: color demo: draw
  ```
- **Why?** The `background(color)` modifier evaluates its `color` argument when the modifier chain is constructed during the **composition phase**. Therefore, changing `color` invalidates composition and triggers recomposition.

---

## 3. State Read vs. State Storage
- **Where state is created has little bearing on phases:** Whether `var color` is declared inside `ColorDemo` has no direct impact on which phases execute.
- **What matters is *where* the state is *read*:**
  - If read in a draw lambda (`Canvas` / `drawBehind`), updates trigger **only the draw phase**.
  - If read while building modifiers (`background(color)`), updates trigger **composition (recomposition)**.
  - This powerful property of Jetpack Compose allows developers to optimize performance by deferring state reads to later phases (layout or draw), bypassing unnecessary recompositions.
