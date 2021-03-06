@echo off

set start_time=%time%

set wget=bin\wget.exe
if exist %wget% goto :WGET_OK
echo *** %wget% is required!
goto :END
:WGET_OK

rem Set your test site (local) info:
set website=http://www.ff.com:8000
set mobile=http://m.ff.com:8000
set site=%website%

set ext=.jsp
set index_page=index%ext%
set action_page=action%ext%

rem Set security code exactly the same as in Config!
set code=1234

set input=input
set output=output

rem Set for ordinary testing (images OFF)
rem Set SHOW_IMAGES=false in Config also
rem set origin_folder=origin
rem Set for images testing (images ON)
rem Set SHOW_IMAGES=true in Config also
set origin_folder=origin-images

if not exist %output% mkdir %output%
if not exist %origin_folder% mkdir %origin_folder%

set log=%output%\log.txt
echo > %log%

rem Just for debugging -- remove on production!!!
rem goto :STYLES

:CREATE
echo *** Starting 1_create.bat >> %log%
call 1_create.bat

:FETCH
echo *** Starting 2_fetch.bat >> %log%
call 2_fetch.bat

:STYLES
echo *** Starting 3_styles.bat >> %log%
call 3_styles.bat

:PAGES
echo *** Starting 4_pages.bat >> %log%
call 4_pages.bat

:PAGES_MOBILE
echo *** Starting 4_pages.bat for mobile >> %log%
call 4_pages.bat mobile

:PAGES_REST
echo *** Starting 4_pages.bat for REST >> %log%
call 4_pages.bat rest

:ACTIONS
echo *** Starting 6_actions.bat >> %log%
call 5_actions.bat

:VIEW
echo *** Starting 5_view.bat >> %log%
call 6_view.bat

:VIEW_MOBILE
echo *** Starting 6_view.bat for mobile >> %log%
call 6_view.bat mobile

:RSS
echo *** Starting 7_rss.bat >> %log%
call 7_rss.bat

:METHODS
echo *** Starting 8_methods.bat >> %log%
call 8_methods.bat

:FINAL
rem ... and finally check the whole log
set end_time=%time%
call 99_compare.bat log.txt

:END

rem Clean variables
set log=
set origin_folder=
set output=
set input=
set wget=
set code=
set mobile=
set site=

:EOF

echo Start Time - [%start_time%]; End Time - [%end_time%].
pause
