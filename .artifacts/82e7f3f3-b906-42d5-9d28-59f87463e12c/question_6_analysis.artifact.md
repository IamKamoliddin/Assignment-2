# Question 6 — Reading state in the layout phase

## Context & Task
- **Context:** `Modifier.offset { ... }` lambda runs during the **placement step** of the layout phase, so a state read inside that lambda is a layout-phase read.
- **Tasks:**
  1. Predict the log for original `OffsetDemo` (state read inside `Modifier.offset` lambda).
  2. Answer: Does a composition line appear? Does `layout/measure` appear?
  3. Run the experiment: move the state read above the Text (`val offsetPx = with(LocalDensity.current) { offsetX.roundToPx() }`), tap again, analyze what changed in Logcat and why.

---

## 1. Original `OffsetDemo` (State read inside `Modifier.offset` lambda)

### Prediction:
- **Where is `offsetX` read?** Inside the `Modifier.offset { ... }` lambda, which executes during the **placement step** of the layout phase.
- **Does a composition line (`OffsetDemo: composition`) appear?**
  - **No.** Because `offsetX` is read in the layout/placement phase rather than composition, updating `offsetX` does **not** trigger recomposition of `OffsetDemo`.
- **Does `layout/measure` appear?**
  - **No.** `Modifier.offset` only affects the position (placement), not the size or constraints of the layout. Therefore, the measurement step is skipped (`layout/measure` does not run).
- **What phases run?**
  - **Layout / Place** (`offset demo: layout/place`) and **Draw** (`offset demo: draw`).

### Logcat Output:
```text
D/Phases: offset demo: layout/place
D/Phases: offset demo: draw
```

---

## 2. Experimental `OffsetDemo` (State read above the Text / in Composition)

```kotlin
val offsetPx = with(LocalDensity.current) { offsetX.roundToPx() }

Text(
    text = "Offset demo",
    modifier = Modifier
        .offset { IntOffset(offsetPx, 0) }
        .logPhases("offset demo"),
)
```

### What changed in Logcat and why?
- **Logcat Output:**
  ```text
  D/Phases: OffsetDemo: composition
  D/Phases: offset demo: layout/place
  D/Phases: offset demo: draw
  ```
- **Why did it change?**
  - By moving `val offsetPx = ...` above the `Text` composable, `offsetX` is now read directly inside the `OffsetDemo` composable function body during the **Composition phase**.
  - When the "Move" button is clicked and `offsetX` changes, Compose invalidates the composition scope of `OffsetDemo`, triggering a **recomposition** (`OffsetDemo: composition`).
  - This demonstrates **phase deferral**: keeping state reads inside modifier lambdas (like `Modifier.offset { ... }`) avoids unnecessary recompositions and layout measurements, optimizing performance.
