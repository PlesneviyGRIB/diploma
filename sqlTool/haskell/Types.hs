module Types where

import Data.Time.Clock

data Query = Query [Command]

data Command = Select [Column]
             | Distinct
             | From String
             | InnerJoin Query Expression JoinStrategy
             | LeftJoin  Query Expression JoinStrategy
             | RightJoin Query Expression JoinStrategy
             | FullJoin  Query Expression JoinStrategy
             | Where Expression
             | OrderBy [Order]
             | GroupBy
             | Limit Int
             | Offset Int
             | ConstructIndex Index
             | Alias String

data Value = NullValue
           | BooleanValue Bool
           | IntegerNumber Int
           | LongNumber Integer
           | FloatNumber Float
           | DoubleNumber Double
           | BigDecimalNumber Integer
           | StringValue String
           | TimestampValue UTCTime

data Column = Column { columnName :: String, tableName :: String }

data Order = Order Column OrderDirection

data OrderDirection = ASC | DESC

data JoinStrategy = LOOP | HASH | MERGE

data UnaryOperation = UnaryOperation { op :: Operator, exp :: Expression }

data BinaryOperation = BinaryOperation { op :: Operator, left :: Expression, right :: Expression }

data TernaryOperation = TernaryOperation { op :: Operator, first :: Expression, second :: Expression, third :: Expression }

data Expression = Expression Query
--                | Operator -> Expression -> Expression
--                | BinaryOperation
--                | TernaryOperation
--                | Value


data Operator = Equal
             | NotEqual
             | LessThan
             | LessThanOrEqual
             | GreaterThan
             | GreaterThanOrEqual
             | And
             | Or

data Index = Index { indexName :: String, columns :: [Column] }

data AggregationFunction = Count | Sum | Avg | Min | Max | Identity
