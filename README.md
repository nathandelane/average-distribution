# average-distribution

Calculate the potential distribution of positive integer values comprising a real positive average value by brute force.

## Two algorithms

Maximal Distribution and Random Distribution.

### Maximal Distribution

Maximal distribution means we take the maximum values that could comprise an average.

For example:

*Average:* 4.1

*Minimum value:* 1

*Maximum value:* 5

| Num | Value |
| ---: | :-----: |
| 1. | 5 |
| 2. | 4 |
| 3. | 4 |
| 4. | 4 |
| 5. | 4 |
| 6. | 4 |
| 7. | 4 |
| 8. | 4 |
| 9. | 4 |
| 10. | 4 |

*sum:* 41

*mean:* 4.1

*variance:* 0.09

*min:* 4

*max:* 5

### Random Distribution

With random distribution we randomly pick a number of integers between the mininum value and the maximum value.

Then we add one to each integer until the sum is the total amount required to meet the average.

*Average:* 4.1

*Minimum value:* 1

*Maximum value:* 5

| Num | Value |
| ---: | :-----: |
| 1. | 5 |
| 2. | 4 |
| 3. | 4 |
| 4. | 5 |
| 5. | 6 |
| 6. | 4 |
| 7. | 2 |
| 8. | 3 |
| 9. | 5 |
| 10. | 4 |

*sum:* 41

*mean:* 4.1

*variance:* 0.89

*min:* 2

*max:* 5