# ArraysDeepDive.java — Detailed Explanation

> **File:** `ArraysDeepDive.java`
> **Run:** `javac ArraysDeepDive.java && java ArraysDeepDive`
> **Topics:** Array memory model, core operations, two pointers, sliding window, prefix sum, 2D arrays, spiral traversal, real-world use cases (stocks, sensors, image processing), practice problems, common mistakes, interview prep

---

## Table of Contents

1. [What is an Array?](#1-what-is-an-array)
2. [Program Entry Point](#2-program-entry-point)
3. [Section 1 — Array Basics and Memory Model](#3-section-1--array-basics-and-memory-model)
4. [Section 2 — Core Operations and Complexity](#4-section-2--core-operations-and-complexity)
5. [Section 3 — Two Pointers Pattern](#5-section-3--two-pointers-pattern)
6. [Section 4 — Sliding Window Pattern](#6-section-4--sliding-window-pattern)
7. [Section 5 — Prefix Sum Pattern](#7-section-5--prefix-sum-pattern)
8. [Section 6 — 2D Arrays](#8-section-6--2d-arrays)
9. [Section 7 — Spiral Traversal](#9-section-7--spiral-traversal)
10. [Section 8 — Real World: Time Series](#10-section-8--real-world-time-series)
11. [Section 9 — Real World: Sensor Monitoring](#11-section-9--real-world-sensor-monitoring)
12. [Section 10 — Real World: Image Processing](#12-section-10--real-world-image-processing)
13. [Section 11 — Practice Problems](#13-section-11--practice-problems)
14. [Section 12 — Common Mistakes](#14-section-12--common-mistakes)
15. [Section 13 — Interview Summary](#15-section-13--interview-summary)
16. [Key Takeaways](#16-key-takeaways)

---

## 1. What is an Array?

An array is a **fixed-size, contiguous block of memory** where every element is the same type and is accessible in O(1) via an index.

### Memory layout

```
Index:    0    1    2    3    4
Value:   10   20   30   40   50
Address: 100  104  108  112  116   ← 4 bytes per int
```

The address of any element is computed directly:
```
address = baseAddress + (index × elementSize)
arr[3] → 100 + (3 × 4) = 112
```

One arithmetic operation, regardless of array size — this is why access is always O(1).

### Java-specific facts

| Property | Detail |
|---|---|
| Fixed size | Set at creation, cannot grow or shrink |
| Zero-initialised | `int[]` → 0, `boolean[]` → false, `Object[]` → null |
| Array is an object | Allocated on heap, holds a `.length` field |
| Primitive array | Stores values directly (contiguous ints in memory) |
| Object/reference array | Stores references; actual objects elsewhere on heap |

---

## 2. Program Entry Point

```java
public static void main(String[] args) {
    section1_ArrayBasicsAndMemory();
    // ... 13 sections
}
```

Each section is self-contained. Comment out all but one to focus on a specific pattern during study.

---

## 3. Section 1 — Array Basics and Memory Model

### Primitive vs reference arrays in memory

**Primitive array** — values stored inline (contiguous):
```
int[] nums = {10, 20, 30};
Heap: [10][20][30]
```

**Reference array** — references stored inline; objects scattered:
```
String[] words = {"A", "B", "C"};
Heap: [ref0][ref1][ref2]
       ↓      ↓      ↓
      "A"    "B"    "C"   ← String objects at other heap addresses
```

This distinction matters for performance: iterating a `int[]` is cache-friendly because all values are adjacent. Iterating a `String[]` may cause cache misses because each String object is at a different heap location.

### Three ways to create an array

```java
int[] zeros     = new int[5];                    // zero-initialised, size 5
int[] withVals  = new int[]{10, 20, 30, 40, 50}; // explicit values
int[] shorthand = {10, 20, 30, 40, 50};          // shorthand (only valid at declaration)
```

The shorthand form `{...}` can only be used in a declaration statement. You cannot write `arr = {1, 2, 3}` as a reassignment — you must use `arr = new int[]{1, 2, 3}`.

### Copying arrays

```java
int[] copy1 = original.clone();                   // full copy
int[] copy2 = Arrays.copyOf(original, 3);         // first 3 elements
int[] copy3 = Arrays.copyOfRange(original, 1, 4); // indices 1, 2, 3 (4 exclusive)
```

For **primitive arrays**, all three methods produce an independent copy — mutating the copy does not affect the original.

For **object arrays**, all three methods produce a **shallow copy** — the references are copied, but the objects they point to are shared. Mutating `copy[0].fieldName` also changes `original[0].fieldName` because both reference the same object. See Section 12 for deep copy.

### `System.arraycopy` — the fastest copy

```java
System.arraycopy(src, srcPos, dest, destPos, length);
```

This calls a native JVM intrinsic — the fastest way to copy array data, even faster than `Arrays.copyOf` for large arrays because it maps directly to a memory copy instruction (`memcpy`).

---

## 4. Section 2 — Core Operations and Complexity

### Why access and update are O(1)

```java
arr[4] = 99;
```

The JVM translates this to:
1. Compute address: `base + 4 × elementSize`
2. Write value at that address

One memory address computation + one memory write. No loop, no search. Constant time for any index.

### Why linear search is O(n)

```java
for (int i = 0; i < arr.length; i++) {
    if (arr[i] == target) { foundAt = i; break; }
}
```

In the worst case (element at the end or not present), every element is checked — n comparisons = O(n).

### Binary search requires a sorted array

```java
Arrays.sort(sorted);                        // MUST sort first — O(n log n)
int idx = Arrays.binarySearch(sorted, 7);  // then search — O(log n)
```

Binary search halves the search space on every step: n → n/2 → n/4 → ... → 1. After log₂(n) steps the target is found or its absence is confirmed. If the array is not sorted, the result is **undefined** — `binarySearch` may return a wrong index or a wrong "not found" indicator.

`Arrays.binarySearch` returns:
- `idx ≥ 0` if found (the index)
- `-(insertionPoint + 1)` if not found (where it would be inserted)

### Insert and delete are O(n) due to shifting

**Insert at index 3:**
```
Before: [A, B, C, D, E]
After:  [A, B, C, X, D, E]   ← D and E shifted right
```
All elements from index 3 onward must shift — up to n-1 shifts in the worst case.

**Delete at index 3:**
```
Before: [A, B, C, D, E]
After:  [A, B, C, E]          ← E shifted left
```
All elements after the deleted position must shift left.

### `Arrays.sort` — two sorting algorithms

- **Primitive arrays** (`int[]`, `long[]`, `double[]`, ...): Uses **dual-pivot Quicksort** — O(n log n) average, performs well in practice. Not stable.
- **Object arrays** (`Integer[]`, `String[]`, ...): Uses **TimSort** — O(n log n) worst case, O(n) for nearly-sorted input. Stable (equal elements preserve their original order).

---

## 5. Section 3 — Two Pointers Pattern

### Core idea

Instead of using a nested loop (O(n²)) to compare every pair of elements, use two indices that move intelligently — one from the left, one from the right. Each pointer moves at most n steps → O(n) total.

### Problem 1: Pair with target sum in sorted array

```java
int left = 0, right = arr.length - 1;
while (left < right) {
    int sum = arr[left] + arr[right];
    if (sum == target) return true;
    if (sum < target) left++;   // need bigger sum → move left pointer right
    else right--;                // need smaller sum → move right pointer left
}
```

**Why this works:** The array is sorted. If `arr[left] + arr[right] < target`, the only way to increase the sum is to move `left` right (to a larger value). Symmetrically for too large. Each pointer moves in one direction only — at most n steps combined.

### Problem 2: Remove duplicates in-place

```java
int write = 1;  // slow pointer — next write position
for (int read = 1; read < arr.length; read++) {  // fast pointer
    if (arr[read] != arr[read - 1]) arr[write++] = arr[read];
}
return write;  // new logical size
```

The **fast pointer** scans every element. The **slow pointer** only advances when a new unique element is found. The first `write` positions of the array contain the deduplicated result.

### Problem 3: Move zeros to end

```java
int write = 0;
for (int read = 0; read < arr.length; read++) {
    if (arr[read] != 0) arr[write++] = arr[read];  // copy non-zero
}
while (write < arr.length) arr[write++] = 0;       // fill remaining with zeros
```

Two-pass approach: first sweep collects all non-zero elements; second sweep fills the tail with zeros. Both passes are O(n) → total O(n).

### Problem 4: Three-sum

```java
Arrays.sort(arr);  // sort first — O(n log n)
for (int i = 0; i < arr.length - 2; i++) {
    // skip duplicates for first element
    int left = i + 1, right = arr.length - 1;
    // two-pointer on the remaining subarray
    while (left < right) { ... }
}
```

For each fixed element `arr[i]`, use two pointers on the rest of the array to find pairs that sum to `-arr[i]`. Total: O(n²) — O(n) for the outer loop × O(n) for each inner two-pointer scan.

### Problem 6: Container with most water

```java
int left = 0, right = heights.length - 1, maxWater = 0;
while (left < right) {
    int water = Math.min(heights[left], heights[right]) * (right - left);
    maxWater = Math.max(maxWater, water);
    if (heights[left] < heights[right]) left++;
    else right--;
}
```

**Key insight:** Water is bounded by the shorter wall. When we move the taller wall inward, width decreases but height cannot increase (it's still bounded by the shorter wall) — so we'd only get less water. Therefore, we must move the **shorter wall** inward to have any chance of finding more water.

---

## 6. Section 4 — Sliding Window Pattern

### The core optimisation

**Naive approach** for maximum sum of k consecutive elements:
```java
// O(n × k) — recomputes the entire window sum each step
for (int i = 0; i <= n - k; i++) {
    int sum = 0;
    for (int j = i; j < i + k; j++) sum += arr[j];
    maxSum = Math.max(maxSum, sum);
}
```

**Sliding window** — O(n):
```java
// Build initial window
int sum = 0;
for (int i = 0; i < k; i++) sum += arr[i];

// Slide: add one new element, remove one old element
for (int i = k; i < arr.length; i++) {
    sum += arr[i] - arr[i - k];  // O(1) update — no inner loop
    maxSum = Math.max(maxSum, sum);
}
```

The window moves one step right by doing two operations: `+arr[i]` (enter right) and `-arr[i-k]` (exit left). The sum is maintained without recomputing.

### Fixed vs variable window

**Fixed window** (size k always the same):
- Initial window: O(k)
- Each slide: O(1)
- Total: O(n)
- Use for: max/min/average over every window of size k

**Variable window** (shrink/expand based on condition):
```java
int left = 0, sum = 0;
for (int right = 0; right < arr.length; right++) {
    sum += arr[right];           // expand: add right element
    while (sum >= target) {
        minLen = Math.min(minLen, right - left + 1);
        sum -= arr[left++];      // shrink: remove left element
    }
}
```

Each element enters the window once (via `right++`) and leaves at most once (via `left++`). Total operations ≤ 2n → O(n).

### Problem 3: Longest substring without repeating characters

```java
Map<Character, Integer> lastSeen = new HashMap<>();
int left = 0, maxLen = 0;
for (int right = 0; right < s.length(); right++) {
    char c = s.charAt(right);
    if (lastSeen.containsKey(c) && lastSeen.get(c) >= left) {
        left = lastSeen.get(c) + 1;  // jump left pointer past the duplicate
    }
    lastSeen.put(c, right);
    maxLen = Math.max(maxLen, right - left + 1);
}
```

The map tracks the most recent index where each character was seen. When a duplicate is found within the current window, the left pointer jumps to just after the previous occurrence. The `>= left` check ensures we only skip if the duplicate is inside the current window — not before it.

### Problem 5: Best time to buy/sell stock

```java
int minPrice = Integer.MAX_VALUE, maxProfit = 0;
for (int price : prices) {
    minPrice  = Math.min(minPrice, price);     // track lowest price seen so far
    maxProfit = Math.max(maxProfit, price - minPrice); // best profit if sold today
}
```

This is a sliding minimum combined with a running maximum. No explicit window — `minPrice` acts as the left boundary of the implicit window. O(n) time, O(1) space.

### Problem 6: Count subarrays with exact sum k

```java
Map<Integer, Integer> prefixCount = new HashMap<>();
prefixCount.put(0, 1);  // empty prefix has sum 0
int count = 0, prefixSum = 0;
for (int val : arr) {
    prefixSum += val;
    count += prefixCount.getOrDefault(prefixSum - k, 0);
    prefixCount.merge(prefixSum, 1, Integer::sum);
}
```

This combines sliding window thinking with prefix sums. If `prefixSum[j] - prefixSum[i] = k`, then `arr[i+1..j]` has sum k. For each position j, we look up how many times `prefixSum - k` has appeared before — each such occurrence represents a valid subarray ending at j.

---

## 7. Section 5 — Prefix Sum Pattern

### The structure

```
arr:    [3,  1,  4,  1,  5,  9,  2,  6]
index:   0   1   2   3   4   5   6   7

prefix: [0,  3,  4,  8,  9, 14, 23, 25, 31]
index:   0   1   2   3   4   5   6   7   8
```

`prefix[i]` = sum of all elements from index 0 to index i-1.
`prefix[0]` = 0 always (sum of zero elements).

Range sum formula:
```
sum(left, right) = prefix[right+1] - prefix[left]
sum(2, 5) = prefix[6] - prefix[2] = 23 - 4 = 19 ✓
  (arr[2]+arr[3]+arr[4]+arr[5] = 4+1+5+9 = 19 ✓)
```

### Why it works

```
prefix[right+1] = sum of arr[0..right]
prefix[left]    = sum of arr[0..left-1]

Subtracting: prefix[right+1] - prefix[left]
           = sum of arr[0..right] - sum of arr[0..left-1]
           = sum of arr[left..right]
```

### Kadane's algorithm — maximum subarray sum

```java
int maxSoFar = arr[0], maxEndingHere = arr[0];
for (int i = 1; i < arr.length; i++) {
    // Either extend the existing subarray or start fresh at this element
    maxEndingHere = Math.max(arr[i], maxEndingHere + arr[i]);
    maxSoFar = Math.max(maxSoFar, maxEndingHere);
}
```

`maxEndingHere` tracks the maximum subarray sum ending at position i. If adding `arr[i]` to the previous sum makes it worse than starting fresh with just `arr[i]`, we start a new subarray. This runs in O(n) with O(1) space — much better than the O(n²) prefix-sum approach.

### Product of array except self

```java
// Pass 1: left products
result[0] = 1;
for (int i = 1; i < n; i++) result[i] = result[i-1] * arr[i-1];

// Pass 2: multiply right products in-place
int right = 1;
for (int i = n-1; i >= 0; i--) {
    result[i] *= right;
    right *= arr[i];
}
```

After pass 1, `result[i]` = product of all elements to the left of i.
In pass 2, `right` accumulates the product of all elements to the right.
Final `result[i]` = left product × right product = product of all except self.
O(n) time, O(1) extra space (the result array itself does not count as extra).

### 2D Prefix Sum

```java
prefix[i][j] = mat[i-1][j-1] + prefix[i-1][j] + prefix[i][j-1] - prefix[i-1][j-1]
```

This uses the **inclusion-exclusion principle**. To build the prefix up to (i,j):
- Start with the cell value `mat[i-1][j-1]`
- Add the prefix of the row above: `prefix[i-1][j]`
- Add the prefix of the column to the left: `prefix[i][j-1]`
- Subtract the corner that was counted twice: `prefix[i-1][j-1]`

Rectangle sum query:
```java
sum(r1, c1, r2, c2) = prefix[r2+1][c2+1] - prefix[r1][c2+1] - prefix[r2+1][c1] + prefix[r1][c1]
```

Same inclusion-exclusion: full rectangle minus top strip minus left strip plus doubly-subtracted corner.

---

## 8. Section 6 — 2D Arrays

### Java's array-of-arrays model

```java
int[][] matrix = new int[3][4];
```

Java does NOT store 2D arrays as a flat 2D grid in one contiguous block. It stores them as:

```
matrix → [refRow0][refRow1][refRow2]   outer array on heap

refRow0 → [0][0][0][0]   each row is a separate heap object
refRow1 → [0][0][0][0]
refRow2 → [0][0][0][0]
```

Consequences:
- `matrix.length` = number of rows (3)
- `matrix[i].length` = number of columns in row i (4) — can differ per row!
- `matrix[i][j]` = two pointer dereferences (outer array → row → element), still O(1)
- Rows can have different lengths (**jagged arrays** are valid Java)

### Row-major traversal is cache-friendly

```java
// GOOD — cache-friendly: accesses memory sequentially within each row
for (int i = 0; i < rows; i++)
    for (int j = 0; j < cols; j++)
        sum += matrix[i][j];

// BAD — cache-unfriendly: jumps between rows (each row is a separate heap object)
for (int j = 0; j < cols; j++)
    for (int i = 0; i < rows; i++)
        sum += matrix[i][j];
```

The first loops access each row's elements consecutively — the CPU caches a row at a time. The column-first order jumps to a different row object on every inner iteration, causing more cache misses.

### Transpose

```java
out[j][i] = m[i][j];  // swap row and column indices
```

Transpose converts an m×n matrix to an n×m matrix. If the matrix is square, transpose can be done in-place by swapping elements across the main diagonal.

### Rotate 90° clockwise

Two approaches:
1. **New array:** `out[j][n-1-i] = m[i][j]`
2. **In-place (square matrix):** Transpose, then reverse each row

The in-place approach uses O(1) extra space.

### Set matrix zeroes

The efficient O(1) space approach uses the first row and first column as flags:
```
1. Scan for zeros; mark their row and column using m[i][0] and m[0][j] as flags
2. Zero out cells based on flags (skip row 0 and col 0 for now)
3. Handle row 0 and col 0 separately using pre-computed booleans
```

A naive O(m+n) space approach would store which rows and columns to zero in separate arrays — the in-place approach uses the matrix itself as the flag storage.

---

## 9. Section 7 — Spiral Traversal

### The four-boundary approach

```
top=0, bottom=3, left=0, right=3   (for 4×4 matrix)

Step 1: Go RIGHT across top row    → top++
Step 2: Go DOWN  right column      → right--
Step 3: Go LEFT  across bottom row → bottom-- (check top <= bottom first)
Step 4: Go UP    left column       → left++   (check left <= right first)
Repeat until top > bottom OR left > right
```

Visual walkthrough for a 4×4 matrix:
```
→ → → →
        ↓
← ← ↑  ↓
↑   ↑  ↓
↑ ↑ ↑  ↓
← ← ←  ←
```

Actually in order: right (top row), down (right column), left (bottom row), up (left column), then repeat for the inner ring.

### Guards on step 3 and 4

```java
if (top <= bottom)   // guard for step 3: needed when only one row remains
    for (int j = right; j >= left; j--) result.add(matrix[bottom][j]);

if (left <= right)   // guard for step 4: needed when only one column remains
    for (int i = bottom; i >= top; i--) result.add(matrix[i][left]);
```

Without these guards, a non-square matrix (like 3×5) would try to traverse the bottom row and left column even after they've already been covered by the top row and right column traversals.

---

## 10. Section 8 — Real World: Time Series

### Moving average as a sliding window

```java
double sum = 0;
for (int i = 0; i < k; i++) sum += arr[i];  // initial window
ma[0] = sum / k;
for (int i = k; i < arr.length; i++) {
    sum += arr[i] - arr[i-k];  // slide: add new, remove old
    ma[i-k+1] = sum / k;
}
```

This O(n) sliding window replaces an O(nk) naive approach. For k=50 over 1,000,000 data points, this is a 50× speedup.

### Common time-series analytics

| Operation | Algorithm | Complexity |
|---|---|---|
| Moving average | Sliding window sum / k | O(n) |
| Running max/min | Track running extreme | O(n) |
| Daily returns | `(curr - prev) / prev * 100` | O(n) |
| Sum over range [l, r] | Prefix sum query | O(1) after O(n) build |
| Max profit (buy/sell once) | Sliding min + max profit | O(n) |

---

## 11. Section 9 — Real World: Sensor Monitoring

### Anomaly detection with sliding window

```java
double windowAvg = /* sum of previous k readings */ / k;
if (Math.abs(temps[i] - windowAvg) > threshold) {
    // ALERT: reading deviates more than threshold from recent average
}
```

This is a production pattern used in IoT monitoring, APM (application performance monitoring), and cloud infrastructure alerting. The sliding window average serves as a dynamic baseline — it adapts as the sensor's normal range drifts over time.

### Batch reduction (hourly averages)

```java
for (int start = 0; start < temps.length; start += batchSize) {
    int end = Math.min(start + batchSize, temps.length);
    // average arr[start..end-1]
}
```

Fixed-window batch processing is how time-series databases (InfluxDB, Prometheus) compute rollups: aggregate raw per-second readings into per-minute or per-hour summaries.

### Local maxima (peak detection)

```java
for (int i = 1; i < temps.length - 1; i++) {
    if (temps[i] > temps[i-1] && temps[i] > temps[i+1]) { /* peak at i */ }
}
```

A local maximum is an element greater than both its neighbours. In sensor data this identifies spikes. In signal processing this is the core of peak-finding algorithms.

---

## 12. Section 10 — Real World: Image Processing

### Why images are 2D arrays

A grayscale image is naturally represented as a 2D array where `image[row][col]` is the pixel intensity (0 = black, 255 = white). Colour images use three 2D arrays (R, G, B channels) or a 3D array.

### Flip horizontal

```java
out[i][j] = m[i][cols - 1 - j];  // mirror column index
```

Reflects the image left-to-right. Row index stays the same; column index is mirrored around the centre.

### Threshold (binarise)

```java
out[i][j] = m[i][j] >= cutoff ? 255 : 0;
```

Converts a grayscale image to pure black-and-white. Used as a preprocessing step in OCR (optical character recognition), document scanning, and computer vision.

### Box blur

```java
for (int di = -r; di <= r; di++)
    for (int dj = -r; dj <= r; dj++) {
        int ni = i + di, nj = j + dj;
        if (ni >= 0 && ni < rows && nj >= 0 && nj < cols) {
            sum += m[ni][nj]; count++;
        }
    }
out[i][j] = sum / count;
```

Each output pixel is the average of all pixels within radius `r`. For radius 1, this is a 3×3 neighbourhood average. The bounds check `ni >= 0 && ni < rows` handles edge pixels where the neighbourhood extends beyond the image. This naïve implementation is O(n × m × k²) — a production image processor would use a 2D prefix sum for O(n × m) box blur.

### Brightness adjustment with clamping

```java
out[i][j] = Math.min(255, Math.max(0, m[i][j] + delta));
```

`Math.max(0, ...)` prevents underflow below 0. `Math.min(255, ...)` prevents overflow above 255. Always clamp when doing arithmetic on pixel values.

---

## 13. Section 11 — Practice Problems

### Missing number — XOR trick

```java
int xor = 0;
for (int i = 0; i <= arr.length; i++) xor ^= i;  // XOR all 0..n
for (int v : arr)                     xor ^= v;   // XOR all values
return xor;  // remaining bit pattern = missing number
```

**Why XOR works:** `x ^ x = 0` and `x ^ 0 = x`. XOR of all numbers 0..n then XOR with all array values — each present number cancels itself out; only the missing number has no pair and survives. O(n) time, O(1) space — better than the O(n) space HashMap approach or the arithmetic sum approach (which can overflow for large n).

### Rotate array right by k — triple reverse

```java
k %= arr.length;             // k can be larger than array
reverseRange(arr, 0, n-1);  // reverse everything
reverseRange(arr, 0, k-1);  // reverse first k
reverseRange(arr, k, n-1);  // reverse remaining
```

**Why triple reverse works:** Rotating right by k places means the last k elements move to the front. After reversing the whole array, the first k elements are the last k of the original (in reverse order). Reversing the first k and then the remaining restores the correct order within each segment. O(n) time, O(1) space.

### Find all duplicates — negation trick

```java
for (int v : arr) {
    int idx = Math.abs(v) - 1;     // value as index (1-indexed → 0-indexed)
    if (arr[idx] < 0) dups.add(Math.abs(v));  // already negated → duplicate
    else arr[idx] = -arr[idx];                 // first visit → negate
}
```

For arrays containing values in range [1, n], use the array itself as a visited marker by negating `arr[value-1]`. If it's already negative when we visit again, the value is a duplicate. O(n) time, O(1) extra space. Remember to restore the values after.

### Trapping rain water — left/right max arrays

```java
leftMax[0] = height[0];
for (int i = 1; i < n; i++) leftMax[i] = Math.max(leftMax[i-1], height[i]);

rightMax[n-1] = height[n-1];
for (int i = n-2; i >= 0; i--) rightMax[i] = Math.max(rightMax[i+1], height[i]);

for (int i = 0; i < n; i++)
    water += Math.min(leftMax[i], rightMax[i]) - height[i];
```

**Key insight:** Water at position i is bounded by the minimum of the tallest bar to its left and the tallest bar to its right. Subtract the height of the bar at i to get the water depth. Precomputing left and right maxima turns a O(n²) naive solution into O(n).

### Jump game — greedy max reach

```java
int maxReach = 0;
for (int i = 0; i < nums.length; i++) {
    if (i > maxReach) return false;  // can't reach position i
    maxReach = Math.max(maxReach, i + nums[i]);
}
return true;
```

Maintain the furthest reachable index. For each position i, if i exceeds `maxReach`, it's unreachable. Otherwise update `maxReach = max(maxReach, i + nums[i])`. O(n) greedy — no need to simulate every possible jump.

---

## 14. Section 12 — Common Mistakes

### Mistake 1 — ArrayIndexOutOfBoundsException

```java
arr[arr.length];    // ❌ valid indices are 0 to arr.length - 1
arr[arr.length-1];  // ✅ last element
```

The most common off-by-one: using `<= arr.length` instead of `< arr.length` in a loop condition, or accessing index `arr.length` which does not exist.

### Mistake 2 — Integer overflow

```java
int a = Integer.MAX_VALUE;  // 2,147,483,647
a + 1;                      // ❌ wraps to -2,147,483,648 (silent bug)
(long) a + 1;               // ✅ 2,147,483,648
```

Arrays of integers with values in the billions, prefix sums of large arrays, or products of multiple array elements can silently overflow. Always use `long` when intermediate results may exceed `Integer.MAX_VALUE` (~2.1 billion).

**Safe midpoint:**
```java
int mid = (left + right) / 2;           // ❌ overflows if left + right > Integer.MAX_VALUE
int mid = left + (right - left) / 2;    // ✅ always safe
```

### Mistake 3 — Not guarding empty arrays

```java
// ALWAYS check before accessing any element
if (arr == null || arr.length == 0) return;
int first = arr[0];  // only safe after guard
```

### Mistake 4 — Shallow copy of 2D arrays

```java
int[][] original = {{1, 2}, {3, 4}};
int[][] bad = original.clone();     // ❌ copies row references, not row contents
bad[0][0] = 999;                    // modifies original[0][0]!

// ✅ Deep copy
int[][] good = Arrays.stream(original).map(int[]::clone).toArray(int[][]::new);
```

`clone()` on a 2D array copies the outer array (row references) but not the inner arrays (the actual rows). Both `bad[0]` and `original[0]` reference the same row object.

### Mistake 6 — Midpoint overflow in binary search

```java
// ❌ If left = 1,000,000,000 and right = 2,000,000,000:
int mid = (left + right) / 2;   // 1B + 2B = 3B > MAX_VALUE → overflow

// ✅ Always use this form:
int mid = left + (right - left) / 2;   // subtraction stays within bounds
```

This is so important that Java's own `Arrays.binarySearch` uses the safe form internally.

---

## 15. Section 13 — Interview Summary

### Complexity quick reference

| Operation | Complexity | Notes |
|---|---|---|
| Access/Update | O(1) | Index arithmetic |
| Linear search | O(n) | Unsorted scan |
| Binary search | O(log n) | **Requires sorted** |
| Insert/Delete | O(n) | Element shifting |
| Sort (primitives) | O(n log n) | Dual-pivot Quicksort |
| Sort (objects) | O(n log n) | TimSort, stable |

### Pattern selection guide

```
Array sorted + find pair/triplet?                → Two pointers
Array sorted + find element?                     → Binary search
Contiguous subarray max/min/avg?                 → Sliding window (fixed)
Contiguous subarray with sum/count constraint?   → Sliding window (variable)
Multiple range sum queries?                      → Prefix sum
Maximum subarray sum?                            → Kadane's algorithm
Product except self?                             → Left + right pass
Matrix traversal in order?                       → Spiral (four boundaries)
Rectangle sum queries on 2D?                     → 2D prefix sum
Rotate array?                                    → Triple reverse
Missing/duplicate in [1..n]?                     → XOR or negation trick
```

---

## 16. Key Takeaways

### The three essential patterns

**Two Pointers:**
- Converts O(n²) nested loops to O(n) for sorted arrays or directional scans
- Template: `left=0, right=n-1; while(left<right) { ... }`
- Sorted pair sum, reverse, remove duplicates, move zeros, three-sum

**Sliding Window:**
- Converts O(nk) window recomputation to O(n) by maintaining a running sum
- Fixed: add `arr[i]`, remove `arr[i-k]` each step
- Variable: expand right, shrink left when constraint violated

**Prefix Sum:**
- Trade O(n) build time for O(1) range query time
- `prefix[i+1] = prefix[i] + arr[i]`
- `rangeSum(l,r) = prefix[r+1] - prefix[l]`
- Extends to 2D for rectangle queries

### Golden rules

1. **Guard against empty/null arrays** before accessing any element
2. **Use `left + (right-left)/2`** for midpoints — prevents integer overflow
3. **Use `long`** when sums or products may exceed ~2.1 billion
4. **Sort first** when using two pointers (unless it's a fast/slow pointer pattern)
5. **Prefix sum** is the correct tool for any "range sum" or "number of subarrays with sum = k" problem
6. **Sliding window** — each element enters and exits at most once → O(n), not O(nk)
7. **Deep copy 2D arrays** row by row — `clone()` on the outer array is a shallow copy
8. **Spiral traversal** — maintain four boundaries, shrink after each direction, add guards for non-square
9. **Negate-in-place trick** — for [1..n] arrays, use the values as indices to mark visited in O(1) space
10. **Binary search** requires sorted input — always sort before calling, or maintain sorted order
