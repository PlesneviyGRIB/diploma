#!/bin/bash

psql -U admin -h localhost -p 5432 sqltool -c 'drop schema public cascade;create schema public;grant all on schema public to public;'
psql -U admin -h localhost -p 5432 --single-transaction sqltool < ./test.sql