module Types where

import Data.Time.Clock


type Query = [Command]

type TableName = [Char]
type ColumnName = [Char]
type IndexName = [Char]

newtype Column = Column(TableName, ColumnName)

data Command = Select [Column]
             | Distinct
             | From String
             | InnerJoin Query Expression JoinStrategy
             | LeftJoin  Query Expression JoinStrategy
             | RightJoin Query Expression JoinStrategy
             | FullJoin  Query Expression JoinStrategy
             | Where Expression
             | OrderBy [(Column, OrderDirection)]
             | GroupBy [(Column, AggregationFunction)]
             | Limit Int
             | Offset Int
             | ConstructIndex (IndexName, [Column])
             | Alias String

data OrderDirection = ASC | DESC deriving (Enum)
data JoinStrategy = LOOP | HASH | MERGE deriving (Enum)
data AggregationFunction = COUNT | SUM | AVG | MIN | MAX | IDENTITY deriving (Enum)

data Value = NullValue
           | BooleanValue Bool
           | IntegerNumber Int
           | LongNumber Integer
           | FloatNumber Float
           | DoubleNumber Double
           | BigDecimalNumber Integer
           | StringValue String
           | TimestampValue UTCTime

data Expression = Value
                | UnaryOperation (Operator, Expression)
                | BinaryOperation (Operator, Expression, Expression)
                | TernaryOperation (Operator, Expression, Expression, Expression)
                | SubTable Query
                | ExpressionList [Value]

data Operator =
  AND | BETWEEN | EXISTS | NOT | IN | OR | IS_NULL | LIKE |
  EQ | NOT_EQ | GREATER_OR_EQ | LESS_OR_EQ | GREATER | LESS |
  PLUS | MINUS | MULTIPLY | DIVISION | MOD deriving (Enum)

