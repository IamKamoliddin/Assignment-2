# Question 13 — Reproduce the loop and fix it with a layout primitive

## Context & Task
- **Context:** A frame that depends on layout results being fed back into composition causes unnecessary work and intermediate UI flickering (layout-composition cycle).
- **Tasks:**
  1. Add `CyclicDemo` and analyze log (`tag:Cycle`): count compositions and inspect `imageHeightPx` values.
  2. Add `CyclicFixed` using a standard `Column` and compare logs.
  3. Explain which node plays the role of the **single source of truth** in the fixed version.

---

## 1. CyclicDemo Analysis (The Cyclic Loop)
- **Number of Compositions:** **2 compositions** per initial render pass.
- **Values of `imageHeightPx`:**
  - **1st Composition:** `imageHeightPx = 0` (initial state). The text renders at `top = 0`.
  - **Layout & Size Change:** After measurement, `onSizeChanged` fires with the actual height (e.g., `height = 360` px), setting `imageHeightPx = 360`.
  - **2nd Composition:** Triggered immediately by the state change, with `imageHeightPx = 360`. The text re-renders at `top = 360` px.
- **Problem:** This feedback loop wastes work (double composition per frame) and causes visual flicker or intermediate incorrect placement.

---

## 2. CyclicFixed Analysis (The Fixed Version)
- **Implementation:** Built using a standard `Column`.
- **Log Output:** Only **1 composition** occurs (`fixed: composition #1`).
- **Behavior:** The layout system handles measurement and vertical arrangement automatically in a single pass without feeding sizes back into composition state.

---

## 3. Single Source of Truth
- **The `Column` layout primitive** (and its measurement engine) acts as the **single source of truth** for sizing and positioning. It measures the header/image box and the text sequentially during the layout phase, eliminating the need for composition state feedback loops.
