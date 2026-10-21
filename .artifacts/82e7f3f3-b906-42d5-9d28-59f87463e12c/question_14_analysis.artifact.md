# Question 14 — A custom layout when no primitive fits

## Context & Task
- **Context:** Complex layouts that require measuring children before deciding parent size use custom layouts (`Layout`) following the 3-step algorithm: measure children, decide size, place children.
- **Tasks:**
  1. Complete `StackBelow` (all 3 TODOs) and use it to replace `CyclicDemo`.
  2. Inspect Logcat (`tag:StackBelow`): count "measure child" lines before each "place children" line.
  3. Explain what this shows about the "one pass" rule.
  4. Test adding a 3rd child and observe the count change.

---

## 1. Implementation (`StackBelow`)
```kotlin
@Composable
fun StackBelow(
    modifier: Modifier = Modifier,
    spacing: Dp = 8.dp,
    content: @Composable () -> Unit,
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val loose = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.map { measurable ->
            Log.d("StackBelow", "measure child")
            measurable.measure(loose)
        }

        val spacingPx = spacing.roundToPx()
        val width = placeables.maxOfOrNull { it.width } ?: 0
        val height = placeables.sumOf { it.height } + if (placeables.isNotEmpty()) (placeables.size - 1) * spacingPx else 0

        layout(constraints.constrainWidth(width), constraints.constrainHeight(height)) {
            Log.d("StackBelow", "place children")
            var y = 0
            placeables.forEach { placeable ->
                placeable.placeRelative(0, y)
                y += placeable.height + spacingPx
            }
        }
    }
}
```

---

## 2. Logcat Analysis (`tag:StackBelow`)
- **For 2 Children:** Exactly **2 `"measure child"`** log lines appear before **1 `"place children"`** log line.
- **For 3 Children (Adding a 3rd child):** Exactly **3 `"measure child"`** log lines appear before **1 `"place children"`** log line.

---

## 3. What this shows about the "One Pass" Rule
- Within the **Layout phase**, children are measured first (each exactly once) to determine their dimensions, then the parent computes its own size, and finally all children are placed in a single deterministic pass.
- This proves that measurement and placement happen entirely within the layout phase in a structured order, completely eliminating the need for composition feedback loops (unlike `CyclicDemo`).
