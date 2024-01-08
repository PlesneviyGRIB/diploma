#!/bin/bash

psql -U admin -h localhost -p 5436 sqlTool -c 'drop schema public cascade;create schema public;grant all on schema public to public;'
psql -U admin -h localhost -p 5436 --single-transaction sqlTool < ./test.sql