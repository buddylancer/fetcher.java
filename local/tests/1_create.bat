@echo off

echo *** Starting 1_create.bat...

set folder=1_create
call 97_working.bat
call 98_folders.bat %folder%

set mysql=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe
set dbname=dbusnews
set dbuser=%dbname%
set dbpass=%dbname%

rem Set your MySQL admin name / password
set mysql_with_user="%mysql%" --user=root --password=admin

set result=%folder%\log.txt
set log1=%output%\%result%
echo > %log1%

%mysql_with_user% -v -e "drop database %dbname%"
%mysql_with_user% -v -e "create database %dbname%" >>%log1%
%mysql_with_user% -v -e "create user %dbuser%@localhost identified by '%dbpass%'" >>%log1%
%mysql_with_user% -v -e "grant all on %dbname%.* to %dbuser%@localhost" >>%log1%
%mysql_with_user% -v -e "source %input%\create_%dbname%.sql" %dbname% >>%log1%
%mysql_with_user% -v -e "source %input%\load_%dbname%.sql" %dbname% >>%log1%

call 99_compare.bat %result%

set log1=
set mysql_with_user=
set dbpass=
set dbuser=
set dbname=
set mysql=
set result=
set folder=
