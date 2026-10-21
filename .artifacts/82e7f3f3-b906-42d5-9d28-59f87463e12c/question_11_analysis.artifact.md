# Question 11 — Skipping and deferred reads

## Context & Task
- **Context:** Compose skips composable functions whose inputs did not change. Where state is read determines which functions recompose.
- **Tasks:**
  1. Analyze original version (`Child(count: Int)`): predict composition counts for `Parent`, `Child`, and `Sibling` after tapping "Add" 3 times.
  2. Implement the lambda version (`Child(count: () -> Int)`): predict and observe composition counts.
  3. Explain in two sentences why `Parent` no longer recomposes, using the term **restart scope**.

---

## 1. Original Version Analysis
- **`Parent`:** Recomposes on every click because `count` state is read in `Parent` when passing `count` to `Child`.
- **`Child`:** Recomposes on every click because its input parameter `count` changed.
- **`Sibling`:** Skips recomposition on every click because it takes no parameters and reads no state.

---

## 2. Lambda Version Analysis (`Child(count = { count })`)
- **`Parent`:** Recomposition count remains **1** (does not recompose when "Add" is clicked).
- **`Child`:** Recomposes on each click because `count()` is invoked inside `Child`.
- **`Sibling`:** Skips recomposition.

---

## 3. Explanation using Restart Scope
Because `count` is no longer read within `Parent`'s body and is instead deferred inside the lambda passed to `Child`, changes to `count` do not invalidate `Parent`'s restart scope. As a result, Compose skips recomposing `Parent` entirely and only restarts the scope where `count()` is actually invoked (`Child`).
