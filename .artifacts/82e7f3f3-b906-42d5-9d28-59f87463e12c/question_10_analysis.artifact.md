# Question 10 — derivedStateOf: fewer recompositions for a Boolean

## Context & Task
- **Context:** When state must be read in composition, `derivedStateOf` limits recompositions by filtering out intermediate changes.
- **Tasks:**
  1. Build `ScrollToTopNaive` and observe composition counts (`tag:Derived`) while scrolling 30 items.
  2. Switch to `ScrollToTopDerived` using `derivedStateOf`, repeat, and compare.
  3. Explain why the difference in composition count is so large when visibility changes only twice.
  4. Implement the "Top" button click action using `rememberCoroutineScope()` and `animateScrollToItem(0)`.

---

## 1. Naive Version (`ScrollToTopNaive`)
- **Implementation:**
  ```kotlin
  val showButton = listState.firstVisibleItemIndex > 0
  ```
- **Behavior:** Reading `listState.firstVisibleItemIndex` directly in composition subscribes the composable to scroll snapshot updates. Since `LazyListState` updates internal scroll position values continuously on every pixel scrolled, the naive version triggers a recomposition on **every scroll frame** (tens or hundreds of recompositions), even though the final `showButton` boolean value only changes once (from `false` to `true`).

---

## 2. Derived State Version (`ScrollToTopDerived`)
- **Implementation:**
  ```kotlin
  val showButton by remember { derivedStateOf { listState.firstVisibleItemIndex > 0 } }
  ```
- **Behavior:** `derivedStateOf` acts as a filter between the high-frequency upstream state (`LazyListState` position changes) and downstream UI composition. It only recalculates and invalidates the composition when the *derived result* (`Boolean`) actually changes. Therefore, recomposition runs only when `showButton` flips (at most twice).

---

## 3. Button Action Implementation
- Implemented using `rememberCoroutineScope()` and `animateScrollToItem(0)`:
  ```kotlin
  val coroutineScope = rememberCoroutineScope()
  ...
  Button(onClick = {
      coroutineScope.launch {
          listState.animateScrollToItem(0)
      }
  }) {
      Text("Top")
  }
  ```
