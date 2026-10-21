# Question 9 — Parallax: composition read versus layout read

## Context & Task
- **Context:** Reading `firstVisibleItemScrollOffset` while building a modifier causes recomposition on every scroll event. Reading it inside `Modifier.offset { ... }` defers the read to the placement phase, optimizing performance.
- **Tasks:**
  1. Build `ParallaxSlow` and observe recomposition counts (`tag:Parallax`).
  2. Build `ParallaxFast` using the lambda version (`.offset { IntOffset(...) }`).
  3. Compare composition counts.
  4. State in one sentence which phases the slow version triggers vs which ones the fast version triggers.

---

## 1. ParallaxSlow (Composition Read)
- **Implementation:**
  ```kotlin
  .offset(
      y = with(LocalDensity.current) {
          (listState.firstVisibleItemScrollOffset / 2).toDp()
      }
  )
  ```
- **Behavior:** `firstVisibleItemScrollOffset` is read during composition when building the modifier. Every scroll event updates `firstVisibleItemScrollOffset`, which invalidates the composition and triggers frequent recompositions (`slow: composition #1`, `#2`, `#3`, ...).

---

## 2. ParallaxFast (Layout/Placement Read)
- **Implementation:**
  ```kotlin
  .offset {
      IntOffset(x = 0, y = listState.firstVisibleItemScrollOffset / 2)
  }
  ```
- **Behavior:** `firstVisibleItemScrollOffset` is read inside the `Modifier.offset { ... }` lambda, which executes during the **placement step** of the layout phase. Recomposition is skipped entirely (`fast: composition #1` remains 1 while scrolling).

---

## 3. Summary of Phases Triggered
- **Slow Version:** Triggers composition, layout (measure and place), and draw phases on each scroll event.
- **Fast Version:** Triggers only the layout (placement) and draw phases on each scroll event (skipping composition and layout measurement).
