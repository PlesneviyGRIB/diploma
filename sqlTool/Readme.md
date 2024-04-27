# Simulator for creating SQL query plans

&nbsp;

## Example tables

#### Customers

| id (long) | nickname (string) |
|-----------|-------------------|
| 1         | Egor              |
| 2         | Ilya              |

#### Orders

| id (long) | customer_id (long) | item (string) | date (timestamp)           |
|-----------|--------------------|---------------|----------------------------|
| 1         | 1                  | phone         | 2019-10-07 19:29:39.711000 |
| 2         | 1                  | lamp          | 2019-05-29 16:15:02.439000 |
| 3         | 2                  | tv            | 2023-11-04 14:41:38.170000 |

```java 
    new Query(projection)
        .from("customers")
        .innerJoin(
            new Query(projection).from("orders"),
            Q.op(EQ, Q.column("customers", "id"), Q.column("orders","customer_id")),
            JoinStrategy.HASH
        )
        .as("result")
        .select(
            Q.column("result", "nickname"),
            Q.column("result","item")
        )
        .build()
```

&nbsp;

## Supported commands

| Command         | Usage | Description |
|-----------------|-------|-------------|
| Select          |       |             |
| From            |       |             |
| Where           |       |             |
| InnerJoin       |       |             |
| LeftJoin        |       |             |
| RightJoin       |       |             |
| FullJoin        |       |             |
| OrderBy         |       |             |
| GroupBy         |       |             |
| Limit           |       |             |
| Offset          |       |             |
| Distinct        |       |             |
| Alias           |       |             |
| Construct Index |       |             |
| Drop Index      |       |             |

&nbsp;

## Expressions

    Expression - a single value, context column, subquery or composition of that atoms.

    Expression : Operation | Value | AdditionalExpression

### Supported value types

| Type       | Usage                               |
|------------|-------------------------------------|
| Integer    | IntegerNumber(1)                    |
| Long       | LongNumber(1L)                      |
| Float      | FloatNumber(1F)                     |
| Double     | DoubleNumber(1.0)                   |
| BigDecimal | BigDecimalNumber(new BigDecimal(1)) |
| Boolean    | BooleanValue(false)                 |
| String     | StringValue("hello")                |
| Timestamp  | TimestampValue(new Timestamp(1))    |
| Null       | NullValue()                         |

### Supported operations

| Operation | Usage                                                                  | Supported operators                                                                                                                 |
|-----------|------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------|
| Unary     | UnaryOperation(`operator`, `EXPRESSION`)                               | `EXISTS`, `IS_NULL`                                                                                                                 |
| Binary    | BinaryOperation(`operator`, `EXPRESSION`, `EXPRESSION`)                | `AND`, `IN`, `OR`, `EQ`, `NOT_EQ`, `GREATER_OR_EQ`, `LESS_OR_EQ`, `GREATER`, `LESS`, `PLUS`, `MINUS`, `MULTIPLY`, `DIVISION`, `MOD` |
| Ternary   | TernaryOperation(`operator`, `EXPRESSION`, `EXPRESSION`, `EXPRESSION`) | `BETWEEN`                                                                                                                           |

### Additional expressions

| Additional     | Usage                                                                                    | Description                                                                      |
|----------------|------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------|
| Column         | Column(`orders`, `id`)                                                                   | Reference to the value of the current context                                    |
| SubTable       | new Query(projection).from("orders").where(Q.op(EXISTS, Q.column("orders", "date")))     | Internal query. If it depends on the context, it will be calculated for each HeaderRow |
| ExpressionList | ExpressionList(`LongNumber(1L)`, `LongNumber(2L)`, `LongNumber(3L)`, `LongNumber.class`) | Used for internal type casting. Сan also be used directly as a list of `value`   |